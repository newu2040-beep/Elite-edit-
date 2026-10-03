package com.example.ui.editor

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.data.model.ColorAdjustment
import com.example.data.model.Project
import com.example.data.model.StickerLayer
import com.example.data.model.TextLayer
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary
import java.io.File
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(UnstableApi::class)
@Composable
fun VideoPreviewView(
    project: Project,
    currentPositionMs: Long,
    isPlaying: Boolean,
    showBeforeAfter: Boolean,
    exoPlayer: ExoPlayer?,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    onSelectTextLayer: (String?) -> Unit = {},
    onSelectStickerLayer: (String?) -> Unit = {},
    onAddClipClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val activeInfo = project.getActiveClipInfo(currentPositionMs)
    val activeClip = activeInfo?.clip ?: project.clips.firstOrNull()
    val adjustment = if (showBeforeAfter) ColorAdjustment() else (activeClip?.colorAdjustment ?: ColorAdjustment())

    // Real-time color matrix for live color adjustments
    val colorFilter = remember(adjustment, showBeforeAfter) {
        if (showBeforeAfter) return@remember null
        val cm = ColorMatrix()
        val brightnessShift = (adjustment.brightness * 100f) + (adjustment.exposure * 80f)
        val contrastScale = (1.0f + adjustment.contrast).coerceIn(0.2f, 3.0f)
        val satScale = (1.0f + adjustment.saturation).coerceIn(0.0f, 3.0f)

        cm.setToSaturation(satScale)

        val t = (1.0f - contrastScale) * 128f + brightnessShift
        val tempR = adjustment.temperature * 30f
        val tempB = -adjustment.temperature * 30f
        val tintG = adjustment.tint * 25f

        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                contrastScale, 0f, 0f, 0f, t + tempR,
                0f, contrastScale, 0f, 0f, t + tintG,
                0f, 0f, contrastScale, 0f, t + tempB,
                0f, 0f, 0f, 1f, 0f
            )
        )
        cm.timesAssign(contrastMatrix)
        ColorFilter.colorMatrix(cm)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .aspectRatio(project.aspectRatio.ratio)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(1.dp, EliteBorderDark, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            val previewWidth = maxWidth
            val previewHeight = maxHeight

            if (activeClip != null) {
                if (activeClip.isVideo && exoPlayer != null) {
                    // REAL EXOPLAYER SURFACE VIEW
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            }
                        },
                        update = { playerView ->
                            playerView.player = exoPlayer
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // REAL PHOTO IMAGE RENDERING
                    val file = File(activeClip.uri)
                    val model = if (file.exists()) file else activeClip.uri
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data(model)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Photo Clip",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = colorFilter
                    )
                }
            } else {
                // Empty state prompting to import
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "No Video Clips Added",
                        color = EliteTextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onAddClipClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Add Video or Photo", color = Color.White, fontSize = 13.sp)
                    }
                }
            }

            // Before / After Indicator Badge
            if (showBeforeAfter) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "BEFORE (ORIGINAL)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Render Text Layers
            project.textLayers.forEach { layer ->
                TextOverlayItem(
                    layer = layer,
                    parentWidthPx = previewWidth.value,
                    parentHeightPx = previewHeight.value,
                    onClick = { onSelectTextLayer(layer.id) }
                )
            }

            // Render Sticker Layers
            project.stickerLayers.forEach { sticker ->
                StickerOverlayItem(
                    sticker = sticker,
                    parentWidthPx = previewWidth.value,
                    parentHeightPx = previewHeight.value,
                    onClick = { onSelectStickerLayer(sticker.id) }
                )
            }

            // Bottom overlay bar: Timecode, Play/Pause button, Fullscreen
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentSec = currentPositionMs / 1000L
                val totalSec = project.totalDurationMs / 1000L
                val timeString = String.format(
                    Locale.US,
                    "%02d:%02d / %02d:%02d",
                    currentSec / 60, currentSec % 60,
                    totalSec / 60, totalSec % 60
                )
                Text(
                    text = timeString,
                    color = EliteTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                // Play / Pause Button
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("preview_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) EliteIcons.Pause else EliteIcons.Play,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = EliteTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Fullscreen Icon
                IconButton(
                    onClick = { /* Fullscreen toggle */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = EliteIcons.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = EliteTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TextOverlayItem(
    layer: TextLayer,
    parentWidthPx: Float,
    parentHeightPx: Float,
    onClick: () -> Unit
) {
    var posX by remember { mutableFloatStateOf(layer.posX) }
    var posY by remember { mutableFloatStateOf(layer.posY) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = ((posX * parentWidthPx * 2.5f) - 60f).roundToInt(),
                    y = ((posY * parentHeightPx * 2.5f) - 40f).roundToInt()
                )
            }
            .pointerInput(layer.id) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    posX += dragAmount.x / (parentWidthPx * 2.5f)
                    posY += dragAmount.y / (parentHeightPx * 2.5f)
                }
            }
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text = layer.text,
            color = Color.White,
            fontSize = layer.fontSizeSp.sp,
            fontWeight = if (layer.isBold) FontWeight.Bold else FontWeight.Normal,
            fontFamily = when (layer.fontFamilyName) {
                "Montserrat" -> FontFamily.SansSerif
                "Poppins" -> FontFamily.SansSerif
                "Playfair Display" -> FontFamily.Serif
                else -> FontFamily.SansSerif
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StickerOverlayItem(
    sticker: StickerLayer,
    parentWidthPx: Float,
    parentHeightPx: Float,
    onClick: () -> Unit
) {
    var posX by remember { mutableFloatStateOf(sticker.posX) }
    var posY by remember { mutableFloatStateOf(sticker.posY) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = ((posX * parentWidthPx * 2.5f) - 30f).roundToInt(),
                    y = ((posY * parentHeightPx * 2.5f) - 30f).roundToInt()
                )
            }
            .pointerInput(sticker.id) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    posX += dragAmount.x / (parentWidthPx * 2.5f)
                    posY += dragAmount.y / (parentHeightPx * 2.5f)
                }
            }
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text = when (sticker.iconKey) {
                "smiley" -> "😊"
                "sparkles" -> "✨"
                "heart" -> "❤️"
                "fire" -> "🔥"
                "sun" -> "☀️"
                "moon" -> "🌙"
                "wave" -> "🌊"
                else -> "⭐"
            },
            fontSize = 36.sp
        )
    }
}
