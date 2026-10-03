package com.example.ui.editor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ProjectRepository
import com.example.data.model.AudioClip
import com.example.data.model.CanvasAspectRatio
import com.example.data.model.ColorAdjustment
import com.example.data.model.ExportSettings
import com.example.data.model.Project
import com.example.data.model.StickerLayer
import com.example.data.model.TextLayer
import com.example.data.model.VideoClip
import com.example.domain.command.AddAudioCommand
import com.example.domain.command.AddClipCommand
import com.example.domain.command.AddStickerCommand
import com.example.domain.command.AddTextCommand
import com.example.domain.command.CommandHistory
import com.example.domain.command.DeleteAudioCommand
import com.example.domain.command.DeleteClipCommand
import com.example.domain.command.DeleteStickerCommand
import com.example.domain.command.DeleteTextCommand
import com.example.domain.command.DuplicateClipCommand
import com.example.domain.command.SplitClipCommand
import com.example.domain.command.TrimClipCommand
import com.example.domain.command.UpdateColorAdjustmentCommand
import com.example.domain.command.UpdateTextCommand
import com.example.media.engine.ExportEngine
import com.example.media.engine.ExportState
import com.example.media.engine.PlaybackController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class EditorActiveSheet {
    NONE,
    TEXT_EDITOR,
    STICKERS,
    ADJUST,
    AUDIO,
    FONTS,
    EXPORT,
    CANVAS_SETTINGS,
    SPEED,
    IMPORT_MEDIA
}

data class EditorUiState(
    val project: Project = Project(),
    val currentPositionMs: Long = 0L,
    val isPlaying: Boolean = false,
    val selectedClipIndex: Int = 0,
    val selectedTextLayerId: String? = null,
    val selectedStickerLayerId: String? = null,
    val selectedAudioClipId: String? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val activeSheet: EditorActiveSheet = EditorActiveSheet.NONE,
    val showBeforeAfter: Boolean = false, // for Color Grading Before/After toggle
    val copiedColorAdjustment: ColorAdjustment? = null,
    val exportState: ExportState = ExportState.Idle,
    val timelineZoom: Float = 1.0f
)

class EditorViewModel(
    private val repository: ProjectRepository,
    private val context: Context
) : ViewModel() {

    val playbackController = PlaybackController(context.applicationContext)

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val commandHistory = CommandHistory()
    private var autosaveJob: Job? = null
    private var exportJob: Job? = null

    init {
        viewModelScope.launch {
            playbackController.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlaying = playing) }
            }
        }
        viewModelScope.launch {
            playbackController.currentPositionMs.collect { pos ->
                _uiState.update { it.copy(currentPositionMs = pos) }
            }
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val project = repository.getProject(projectId) ?: Project(id = projectId)
            _uiState.update {
                it.copy(
                    project = project,
                    selectedClipIndex = if (project.clips.isNotEmpty()) 0 else -1,
                    currentPositionMs = 0L,
                    canUndo = commandHistory.canUndo,
                    canRedo = commandHistory.canRedo
                )
            }
            playbackController.syncProject(project)
        }
    }

    fun seekTo(positionMs: Long) {
        val project = _uiState.value.project
        val total = project.totalDurationMs
        val clamped = positionMs.coerceIn(0L, total)
        _uiState.update { it.copy(currentPositionMs = clamped) }
        playbackController.seekTo(clamped, project)
        project.getActiveClipInfo(clamped)?.let { info ->
            _uiState.update { it.copy(selectedClipIndex = info.clipIndex) }
        }
    }

    fun togglePlayPause() {
        playbackController.togglePlayPause(_uiState.value.project)
    }

    fun selectClip(index: Int) {
        val project = _uiState.value.project
        val clips = project.clips
        if (index in clips.indices) {
            _uiState.update { it.copy(selectedClipIndex = index) }
            var startMs = 0L
            for (i in 0 until index) {
                startMs += clips[i].effectiveDurationMs
            }
            seekTo(startMs)
        }
    }

    fun openSheet(sheet: EditorActiveSheet) {
        _uiState.update { it.copy(activeSheet = sheet) }
    }

    fun closeSheet() {
        _uiState.update { it.copy(activeSheet = EditorActiveSheet.NONE) }
    }

    fun toggleBeforeAfter() {
        _uiState.update { it.copy(showBeforeAfter = !it.showBeforeAfter) }
    }

    // Command-based editing
    fun addClip(clip: VideoClip) {
        val cmd = AddClipCommand(clip)
        applyCommand(cmd)
    }

    fun deleteSelectedClip() {
        val state = _uiState.value
        val idx = state.selectedClipIndex
        if (idx in state.project.clips.indices) {
            val clip = state.project.clips[idx]
            applyCommand(DeleteClipCommand(idx, clip))
            _uiState.update {
                it.copy(selectedClipIndex = (idx - 1).coerceAtLeast(0).takeIf { i -> i < it.project.clips.size } ?: -1)
            }
        }
    }

    fun splitSelectedClip() {
        val state = _uiState.value
        val idx = state.selectedClipIndex
        if (idx in state.project.clips.indices) {
            val clip = state.project.clips[idx]
            // Calculate relative offset within clip
            var accumulatedMs = 0L
            for (i in 0 until idx) {
                accumulatedMs += state.project.clips[i].effectiveDurationMs
            }
            val relativeTimeMs = (state.currentPositionMs - accumulatedMs).coerceIn(500L, clip.effectiveDurationMs - 500L)
            applyCommand(SplitClipCommand(idx, clip, relativeTimeMs))
        }
    }

    fun trimSelectedClip(newTrimStartMs: Long, newTrimEndMs: Long) {
        val state = _uiState.value
        val idx = state.selectedClipIndex
        if (idx in state.project.clips.indices) {
            val clip = state.project.clips[idx]
            applyCommand(
                TrimClipCommand(
                    clipId = clip.id,
                    oldTrimStartMs = clip.trimStartMs,
                    oldTrimEndMs = clip.trimEndMs,
                    newTrimStartMs = newTrimStartMs,
                    newTrimEndMs = newTrimEndMs
                )
            )
        }
    }

    fun duplicateSelectedClip() {
        val state = _uiState.value
        val idx = state.selectedClipIndex
        if (idx in state.project.clips.indices) {
            val clip = state.project.clips[idx]
            applyCommand(DuplicateClipCommand(clip))
        }
    }

    fun updateSelectedClipSpeed(speed: Float) {
        val state = _uiState.value
        val idx = state.selectedClipIndex
        if (idx in state.project.clips.indices) {
            val updated = state.project.clips.toMutableList()
            updated[idx] = updated[idx].copy(speed = speed)
            updateProject(state.project.copy(clips = updated))
        }
    }

    fun copyColorAdjustment() {
        val state = _uiState.value
        val idx = state.selectedClipIndex
        if (idx in state.project.clips.indices) {
            _uiState.update { it.copy(copiedColorAdjustment = state.project.clips[idx].colorAdjustment) }
        }
    }

    fun pasteColorAdjustment() {
        val state = _uiState.value
        val adj = state.copiedColorAdjustment ?: return
        val idx = state.selectedClipIndex
        if (idx in state.project.clips.indices) {
            val clip = state.project.clips[idx]
            applyCommand(UpdateColorAdjustmentCommand(clip.id, clip.colorAdjustment, adj))
        }
    }

    fun updateColorAdjustment(newAdjustment: ColorAdjustment) {
        val state = _uiState.value
        val idx = state.selectedClipIndex
        if (idx in state.project.clips.indices) {
            val clip = state.project.clips[idx]
            val updated = state.project.clips.toMutableList()
            updated[idx] = clip.copy(colorAdjustment = newAdjustment)
            updateProject(state.project.copy(clips = updated))
        }
    }

    fun setCanvasAspectRatio(ratio: CanvasAspectRatio) {
        updateProject(_uiState.value.project.copy(aspectRatio = ratio))
    }

    // Text Layers
    fun addTextLayer(textLayer: TextLayer) {
        applyCommand(AddTextCommand(textLayer))
        _uiState.update { it.copy(selectedTextLayerId = textLayer.id) }
    }

    fun updateTextLayer(updated: TextLayer) {
        val old = _uiState.value.project.textLayers.firstOrNull { it.id == updated.id } ?: return
        applyCommand(UpdateTextCommand(old, updated))
    }

    fun deleteSelectedText() {
        val id = _uiState.value.selectedTextLayerId ?: return
        val text = _uiState.value.project.textLayers.firstOrNull { it.id == id } ?: return
        applyCommand(DeleteTextCommand(text))
        _uiState.update { it.copy(selectedTextLayerId = null) }
    }

    fun selectTextLayer(id: String?) {
        _uiState.update { it.copy(selectedTextLayerId = id) }
    }

    // Stickers
    fun addSticker(sticker: StickerLayer) {
        applyCommand(AddStickerCommand(sticker))
        _uiState.update { it.copy(selectedStickerLayerId = sticker.id) }
    }

    fun deleteSelectedSticker() {
        val id = _uiState.value.selectedStickerLayerId ?: return
        val sticker = _uiState.value.project.stickerLayers.firstOrNull { it.id == id } ?: return
        applyCommand(DeleteStickerCommand(sticker))
        _uiState.update { it.copy(selectedStickerLayerId = null) }
    }

    // Audio
    fun addAudioClip(audioClip: AudioClip) {
        applyCommand(AddAudioCommand(audioClip))
    }

    fun deleteSelectedAudio() {
        val id = _uiState.value.selectedAudioClipId ?: return
        val clip = _uiState.value.project.audioClips.firstOrNull { it.id == id } ?: return
        applyCommand(DeleteAudioCommand(clip))
        _uiState.update { it.copy(selectedAudioClipId = null) }
    }

    fun updateAudioVolume(audioId: String, volume: Float) {
        val updated = _uiState.value.project.audioClips.map {
            if (it.id == audioId) it.copy(volume = volume) else it
        }
        updateProject(_uiState.value.project.copy(audioClips = updated))
    }

    // Undo / Redo
    fun undo() {
        val current = _uiState.value.project
        val reverted = commandHistory.undo(current) ?: return
        updateProject(reverted)
    }

    fun redo() {
        val current = _uiState.value.project
        val restored = commandHistory.redo(current) ?: return
        updateProject(restored)
    }

    private fun applyCommand(cmd: com.example.domain.command.EditorCommand) {
        val newProj = commandHistory.executeCommand(cmd, _uiState.value.project)
        updateProject(newProj)
    }

    private fun updateProject(project: Project) {
        _uiState.update {
            it.copy(
                project = project,
                canUndo = commandHistory.canUndo,
                canRedo = commandHistory.canRedo
            )
        }
        playbackController.syncProject(project)
        // Debounced autosave
        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            delay(1000L)
            repository.saveProject(project)
        }
    }

    // Export Pipeline
    fun startExport(settings: ExportSettings) {
        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            ExportEngine.exportProject(context, _uiState.value.project, settings).collect { state ->
                _uiState.update { it.copy(exportState = state) }
            }
        }
    }

    fun cancelExport() {
        exportJob?.cancel()
        exportJob = null
        _uiState.update { it.copy(exportState = ExportState.Idle) }
    }

    fun resetExportState() {
        _uiState.update { it.copy(exportState = ExportState.Idle) }
    }

    override fun onCleared() {
        super.onCleared()
        playbackController.release()
    }
}
