package com.example.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AudioClip
import com.example.data.model.Project
import com.example.data.model.VideoClip
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePlayheadColor
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary
import com.example.ui.theme.EliteTextTertiary
import com.example.ui.theme.EliteTimelineBg
import com.example.ui.theme.EliteTrackBg
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun TimelineView(
    project: Project,
    currentPositionMs: Long,
    selectedClipIndex: Int,
    onSeek: (Long) -> Unit,
    onSelectClip: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDurationMs = project.totalDurationMs
    val msPerDp = 80f // 80ms per dp gives smooth scrubbing

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(EliteTimelineBg)
            .border(width = 0.5.dp, color = EliteBorderDark)
            .testTag("timeline_container")
    ) {
        val containerWidthPx = constraints.maxWidth.toFloat()
        val centerX = containerWidthPx / 2f
        val playheadOffsetDp = (currentPositionMs / msPerDp).dp

        val scrollableState = rememberScrollableState { delta ->
            // Delta is in pixels: scrolling right moves timeline back, left moves forward
            val deltaMs = (-delta * msPerDp).toLong()
            onSeek((currentPositionMs + deltaMs).coerceIn(0L, totalDurationMs))
            delta
        }

        // Time Ruler Canvas at top
        TimeRulerCanvas(
            currentPositionMs = currentPositionMs,
            totalDurationMs = totalDurationMs,
            msPerDp = msPerDp,
            centerX = centerX,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )

        // Horizontal Tracks: Video Track + Audio Track
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 22.dp)
                .scrollable(
                    state = scrollableState,
                    orientation = Orientation.Horizontal
                )
        ) {
            // Track content container shifted so current playhead aligns with center
            Row(
                modifier = Modifier
                    .offset {
                        val shift = centerX - (currentPositionMs / msPerDp * density)
                        IntOffset(shift.roundToInt(), 0)
                    }
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // Video Clips Row
                    Row(
                        modifier = Modifier
                            .height(52.dp)
                            .background(EliteTrackBg, RoundedCornerShape(8.dp))
                    ) {
                        project.clips.forEachIndexed { index, clip ->
                            val isSelected = index == selectedClipIndex
                            val clipWidth = (clip.effectiveDurationMs / msPerDp).dp.coerceAtLeast(40.dp)

                            ClipItemView(
                                clip = clip,
                                isSelected = isSelected,
                                widthDp = clipWidth,
                                onClick = { onSelectClip(index) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Audio Waveform Track
                    Row(
                        modifier = Modifier
                            .height(28.dp)
                            .background(Color(0xFF131A2E), RoundedCornerShape(6.dp))
                    ) {
                        if (project.audioClips.isNotEmpty()) {
                            project.audioClips.forEach { audio ->
                                val audioWidth = (audio.effectiveDurationMs / msPerDp).dp.coerceAtLeast(40.dp)
                                AudioTrackItem(audio = audio, widthDp = audioWidth)
                            }
                        } else {
                            // Video original audio waveform
                            val totalWidth = (totalDurationMs / msPerDp).dp.coerceAtLeast(100.dp)
                            DefaultAudioWaveform(widthDp = totalWidth)
                        }
                    }
                }
            }
        }

        // Fixed Playhead Line (Center)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(2.dp)
                .fillMaxHeight()
                .background(ElitePlayheadColor)
        ) {
            // Playhead Top Pointer Cap
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(width = 8.dp, height = 8.dp)
                    .background(ElitePrimaryLight, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
private fun ClipItemView(
    clip: VideoClip,
    isSelected: Boolean,
    widthDp: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) ElitePrimaryLight else EliteBorderDark
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(
        modifier = Modifier
            .width(widthDp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(6.dp))
            .background(EliteCardDark)
            .border(borderWidth, borderColor, RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        // Thumbnail strip
        val context = LocalContext.current
        val thumbFile = clip.thumbnailPath?.let { File(it) } ?: File(clip.uri)
        val imageModel = if (thumbFile.exists()) thumbFile else (clip.thumbnailResId ?: R.drawable.img_app_icon)
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(imageModel)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = clip.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Clip label
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (clip.isVideo) "VIDEO" else "PHOTO",
                color = if (clip.isVideo) Color(0xFF6C8CFF) else Color(0xFF00D26A),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = clip.name,
                color = EliteTextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }

        // Trim handles if selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(ElitePrimaryLight)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(ElitePrimaryLight)
            )
        }
    }
}

@Composable
private fun AudioTrackItem(audio: AudioClip, widthDp: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(widthDp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF1B233D))
            .border(0.5.dp, Color(0xFF3B487A), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 4.dp.toPx()
            var x = 2f
            val midY = size.height / 2f
            val amps = audio.waveformAmplitudes.ifEmpty { listOf(0.3f, 0.6f, 0.8f, 0.4f, 0.9f, 0.5f, 0.7f, 0.3f) }
            var idx = 0
            while (x < size.width) {
                val amp = amps[idx % amps.size]
                val h = (size.height * 0.75f * amp).coerceAtLeast(4f)
                drawLine(
                    color = Color(0xFF6C8CFF),
                    start = Offset(x, midY - h / 2f),
                    end = Offset(x, midY + h / 2f),
                    strokeWidth = 2.dp.toPx()
                )
                x += step
                idx++
            }
        }
    }
}

@Composable
private fun DefaultAudioWaveform(widthDp: androidx.compose.ui.unit.Dp) {
    Canvas(
        modifier = Modifier
            .width(widthDp)
            .fillMaxHeight()
            .padding(horizontal = 4.dp)
    ) {
        val step = 4.dp.toPx()
        var x = 2f
        val midY = size.height / 2f
        val sampleAmps = floatArrayOf(0.2f, 0.5f, 0.8f, 0.6f, 0.3f, 0.7f, 0.9f, 0.4f, 0.8f, 0.5f, 0.3f, 0.6f, 0.7f)
        var i = 0
        while (x < size.width) {
            val amp = sampleAmps[i % sampleAmps.size]
            val h = (size.height * 0.7f * amp).coerceAtLeast(4f)
            drawLine(
                color = Color(0xFF4C66C9),
                start = Offset(x, midY - h / 2f),
                end = Offset(x, midY + h / 2f),
                strokeWidth = 1.8.dp.toPx()
            )
            x += step
            i++
        }
    }
}

@Composable
private fun TimeRulerCanvas(
    currentPositionMs: Long,
    totalDurationMs: Long,
    msPerDp: Float,
    centerX: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val density = this.density
        val shift = centerX - (currentPositionMs / msPerDp * density)
        val stepMs = 2000L // Mark every 2 seconds
        val totalMarks = (totalDurationMs / stepMs) + 4

        for (i in 0..totalMarks) {
            val markMs = i * stepMs
            val x = shift + (markMs / msPerDp * density)
            if (x in -50f..(size.width + 50f)) {
                // Tick mark
                drawLine(
                    color = Color(0xFF4B4E63),
                    start = Offset(x, size.height - 6.dp.toPx()),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}
