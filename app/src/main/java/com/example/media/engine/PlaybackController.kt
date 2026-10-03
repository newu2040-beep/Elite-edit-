package com.example.media.engine

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.model.ActiveClipInfo
import com.example.data.model.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

@OptIn(UnstableApi::class)
class PlaybackController(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var playbackLoopJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1000L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private var activeVideoUri: String? = null
    private var currentProject: Project? = null

    fun getPlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context).build().also { player ->
            exoPlayer = player
            player.repeatMode = Player.REPEAT_MODE_OFF
            player.playWhenReady = false
        }
    }

    fun syncProject(project: Project) {
        currentProject = project
        _durationMs.value = project.totalDurationMs
        val pos = _currentPositionMs.value.coerceIn(0L, project.totalDurationMs)
        _currentPositionMs.value = pos
        syncActiveMedia(pos, isSeeking = true)
    }

    fun play(project: Project) {
        currentProject = project
        _isPlaying.value = true
        startPlaybackLoop()
    }

    fun pause() {
        _isPlaying.value = false
        stopPlaybackLoop()
        exoPlayer?.pause()
    }

    fun togglePlayPause(project: Project) {
        if (_isPlaying.value) {
            pause()
        } else {
            play(project)
        }
    }

    fun seekTo(positionMs: Long, project: Project) {
        currentProject = project
        val clamped = positionMs.coerceIn(0L, project.totalDurationMs)
        _currentPositionMs.value = clamped
        syncActiveMedia(clamped, isSeeking = true)
    }

    fun stepForward(ms: Long = 33L, project: Project) {
        seekTo(_currentPositionMs.value + ms, project)
    }

    fun stepBackward(ms: Long = 33L, project: Project) {
        seekTo(_currentPositionMs.value - ms, project)
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        exoPlayer?.setPlaybackSpeed(speed)
    }

    fun toggleMute() {
        val muted = !_isMuted.value
        _isMuted.value = muted
        exoPlayer?.volume = if (muted) 0.0f else 1.0f
    }

    private fun syncActiveMedia(posMs: Long, isSeeking: Boolean) {
        val project = currentProject ?: return
        val activeInfo = project.getActiveClipInfo(posMs) ?: return
        val clip = activeInfo.clip

        if (clip.isVideo) {
            val player = getPlayer()
            if (activeVideoUri != clip.uri) {
                activeVideoUri = clip.uri
                try {
                    val file = File(clip.uri)
                    val uri = if (file.exists()) Uri.fromFile(file) else Uri.parse(clip.uri)
                    player.setMediaItem(MediaItem.fromUri(uri))
                    player.prepare()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val targetVideoPos = (clip.trimStartMs + (activeInfo.localOffsetMs * clip.speed)).toLong()
            if (isSeeking || kotlin.math.abs(player.currentPosition - targetVideoPos) > 300) {
                player.seekTo(targetVideoPos.coerceAtLeast(0L))
            }
            player.playbackParameters = PlaybackParameters(clip.speed * _playbackSpeed.value)
            player.volume = if (_isMuted.value || clip.isMuted) 0f else clip.volume

            if (_isPlaying.value && !player.isPlaying) {
                player.play()
            } else if (!_isPlaying.value && player.isPlaying) {
                player.pause()
            }
        } else {
            // Photo clip
            exoPlayer?.pause()
            activeVideoUri = null
        }
    }

    private fun startPlaybackLoop() {
        stopPlaybackLoop()
        playbackLoopJob = scope.launch {
            var lastTime = System.currentTimeMillis()
            while (isActive && _isPlaying.value) {
                val now = System.currentTimeMillis()
                val delta = (now - lastTime).coerceIn(1L, 100L)
                lastTime = now

                val project = currentProject
                if (project == null || project.clips.isEmpty()) {
                    delay(33L)
                    continue
                }

                val totalDuration = project.totalDurationMs
                val activeInfo = project.getActiveClipInfo(_currentPositionMs.value)

                if (activeInfo == null) {
                    _currentPositionMs.value = 0L
                    syncActiveMedia(0L, isSeeking = true)
                    delay(33L)
                    continue
                }

                val clip = activeInfo.clip

                if (clip.isVideo) {
                    val player = getPlayer()
                    if (activeVideoUri != clip.uri) {
                        syncActiveMedia(_currentPositionMs.value, isSeeking = true)
                    }

                    if (!player.isPlaying && player.playbackState == Player.STATE_READY) {
                        player.play()
                    }

                    val playerPos = player.currentPosition
                    val localVideoOffset = ((playerPos - clip.trimStartMs) / clip.speed).toLong()
                    val newTimelinePos = activeInfo.clipStartInProjectMs + localVideoOffset

                    // Check if clip ended
                    if (playerPos >= clip.trimEndMs || localVideoOffset >= clip.effectiveDurationMs) {
                        val nextPos = activeInfo.clipStartInProjectMs + clip.effectiveDurationMs
                        if (nextPos >= totalDuration) {
                            // Loop back to start
                            _currentPositionMs.value = 0L
                            syncActiveMedia(0L, isSeeking = true)
                        } else {
                            _currentPositionMs.value = nextPos
                            syncActiveMedia(nextPos, isSeeking = true)
                        }
                    } else {
                        _currentPositionMs.value = newTimelinePos.coerceIn(0L, totalDuration)
                    }
                } else {
                    // Photo clip advance
                    val advance = (delta * _playbackSpeed.value).toLong()
                    val nextPos = _currentPositionMs.value + advance
                    if (nextPos >= activeInfo.clipStartInProjectMs + clip.effectiveDurationMs) {
                        if (nextPos >= totalDuration) {
                            _currentPositionMs.value = 0L
                            syncActiveMedia(0L, isSeeking = true)
                        } else {
                            _currentPositionMs.value = activeInfo.clipStartInProjectMs + clip.effectiveDurationMs
                            syncActiveMedia(_currentPositionMs.value, isSeeking = true)
                        }
                    } else {
                        _currentPositionMs.value = nextPos
                    }
                }

                delay(30L)
            }
        }
    }

    private fun stopPlaybackLoop() {
        playbackLoopJob?.cancel()
        playbackLoopJob = null
    }

    fun release() {
        stopPlaybackLoop()
        exoPlayer?.release()
        exoPlayer = null
        activeVideoUri = null
    }
}
