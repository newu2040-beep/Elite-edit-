package com.example.ui.editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.media.engine.ExportState
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteBackgroundDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

@Composable
fun EditorScreen(
    projectId: String,
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        if (state.activeSheet != EditorActiveSheet.NONE) {
            viewModel.closeSheet()
        } else {
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteBackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // TOP HEADER BAR (Matches Mockup 5)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("editor_back_button")) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = EliteTextPrimary
                    )
                }

                // Undo & Redo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = state.canUndo,
                        modifier = Modifier.testTag("editor_undo_button")
                    ) {
                        Icon(
                            imageVector = EliteIcons.Undo,
                            contentDescription = "Undo",
                            tint = if (state.canUndo) EliteTextPrimary else EliteTextSecondary.copy(alpha = 0.35f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = state.canRedo,
                        modifier = Modifier.testTag("editor_redo_button")
                    ) {
                        Icon(
                            imageVector = EliteIcons.Redo,
                            contentDescription = "Redo",
                            tint = if (state.canRedo) EliteTextPrimary else EliteTextSecondary.copy(alpha = 0.35f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Resolution Dropdown Pill & Export Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(EliteCardDark)
                            .clickable { viewModel.openSheet(EditorActiveSheet.EXPORT) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.project.exportSettings.resolution.label,
                                color = EliteTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Resolution",
                                tint = EliteTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Export Primary Button
                    Button(
                        onClick = { viewModel.openSheet(EditorActiveSheet.EXPORT) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .testTag("editor_export_shortcut"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Export",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // CENTER DOMINANT PREVIEW
            VideoPreviewView(
                project = state.project,
                currentPositionMs = state.currentPositionMs,
                isPlaying = state.isPlaying,
                showBeforeAfter = state.showBeforeAfter,
                exoPlayer = viewModel.playbackController.getPlayer(),
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onSelectTextLayer = { viewModel.selectTextLayer(it) },
                onSelectStickerLayer = { },
                onAddClipClicked = { viewModel.openSheet(EditorActiveSheet.IMPORT_MEDIA) },
                modifier = Modifier.weight(1f)
            )

            // BOTTOM TIMELINE
            TimelineView(
                project = state.project,
                currentPositionMs = state.currentPositionMs,
                selectedClipIndex = state.selectedClipIndex,
                onSeek = { viewModel.seekTo(it) },
                onSelectClip = { viewModel.selectClip(it) }
            )

            // BOTTOM TOOLBAR
            EditorToolbar(
                onCut = { viewModel.splitSelectedClip() },
                onSplit = { viewModel.splitSelectedClip() },
                onCopy = { viewModel.copyColorAdjustment() },
                onPaste = { viewModel.pasteColorAdjustment() },
                onDuplicate = { viewModel.duplicateSelectedClip() },
                onDelete = { viewModel.deleteSelectedClip() },
                onOpenText = { viewModel.openSheet(EditorActiveSheet.TEXT_EDITOR) },
                onOpenStickers = { viewModel.openSheet(EditorActiveSheet.STICKERS) },
                onOpenAdjust = { viewModel.openSheet(EditorActiveSheet.ADJUST) },
                onOpenAudio = { viewModel.openSheet(EditorActiveSheet.AUDIO) },
                onOpenCanvas = { viewModel.openSheet(EditorActiveSheet.CANVAS_SETTINGS) },
                onOpenSpeed = { viewModel.updateSelectedClipSpeed(1.5f) },
                hasCopiedAdjustment = state.copiedColorAdjustment != null
            )
        }

        // OVERLAY SHEETS
        AnimatedVisibility(
            visible = state.activeSheet == EditorActiveSheet.ADJUST,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val activeClip = state.project.clips.getOrNull(state.selectedClipIndex)
            AdjustScreen(
                currentAdjustment = activeClip?.colorAdjustment ?: com.example.data.model.ColorAdjustment(),
                showBeforeAfter = state.showBeforeAfter,
                onToggleBeforeAfter = { viewModel.toggleBeforeAfter() },
                onApplyAdjustment = { viewModel.updateColorAdjustment(it) },
                onClose = { viewModel.closeSheet() },
                modifier = Modifier.height(380.dp)
            )
        }

        AnimatedVisibility(
            visible = state.activeSheet == EditorActiveSheet.TEXT_EDITOR,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            val selectedText = state.project.textLayers.firstOrNull { it.id == state.selectedTextLayerId }
            TextEditorSheet(
                initialTextLayer = selectedText,
                onSaveText = { layer ->
                    if (selectedText != null) {
                        viewModel.updateTextLayer(layer)
                    } else {
                        viewModel.addTextLayer(layer)
                    }
                },
                onClose = { viewModel.closeSheet() }
            )
        }

        AnimatedVisibility(
            visible = state.activeSheet == EditorActiveSheet.STICKERS,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            StickerSheet(
                onAddSticker = { viewModel.addSticker(it) },
                onClose = { viewModel.closeSheet() }
            )
        }

        AnimatedVisibility(
            visible = state.activeSheet == EditorActiveSheet.AUDIO,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            AudioSheet(
                activeClip = state.project.clips.getOrNull(state.selectedClipIndex),
                onAddAudio = { viewModel.addAudioClip(it) },
                onClose = { viewModel.closeSheet() }
            )
        }

        AnimatedVisibility(
            visible = state.activeSheet == EditorActiveSheet.CANVAS_SETTINGS,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            CanvasSheet(
                currentRatio = state.project.aspectRatio,
                onSelectRatio = { viewModel.setCanvasAspectRatio(it) },
                onClose = { viewModel.closeSheet() }
            )
        }

        AnimatedVisibility(
            visible = state.activeSheet == EditorActiveSheet.EXPORT,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            ExportScreen(
                currentSettings = state.project.exportSettings,
                onStartExport = { settings ->
                    viewModel.closeSheet()
                    viewModel.startExport(settings)
                },
                onClose = { viewModel.closeSheet() }
            )
        }

        AnimatedVisibility(
            visible = state.activeSheet == EditorActiveSheet.IMPORT_MEDIA,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            com.example.ui.home.MediaImportSheet(
                onImportMedia = { clips ->
                    clips.forEach { clip ->
                        viewModel.addClip(clip)
                    }
                    viewModel.closeSheet()
                },
                onClose = { viewModel.closeSheet() }
            )
        }

        // Exporting Progress Screen
        val exportState = state.exportState
        if (exportState is ExportState.Progress) {
            ExportingScreen(
                state = exportState,
                onCancel = { viewModel.cancelExport() }
            )
        } else if (exportState is ExportState.Success) {
            ExportCompleteScreen(
                outputPath = exportState.outputPath,
                onViewVideo = { viewModel.resetExportState() },
                onShare = { /* Intent share */ },
                onBackToHome = {
                    viewModel.resetExportState()
                    onNavigateBack()
                }
            )
        }
    }
}
