package com.example.ui.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ColorAdjustment
import com.example.data.model.Project
import com.example.data.model.StickerLayer
import com.example.data.model.TextLayer
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun VideoPreviewView(
    project: Project,
    currentPositionMs: Long,
    isPlaying: Boolean,
    showBeforeAfter: Boolean,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    onSelectTextLayer: (String?) -> Unit = {},
    onSelectStickerLayer: (String?) -> Unit = {}
) {
    val activeClip = project.clips.firstOrNull()
    val adjustment = if (showBeforeAfter) ColorAdjustment() else (activeClip?.colorAdjustment ?: ColorAdjustment())

    // Build color matrix representing real-time color grading
    val colorFilter = remember(adjustment) {
        val cm = ColorMatrix()
        // Brightness & Exposure:
        val brightnessShift = (adjustment.brightness * 100f) + (adjustment.exposure * 80f)
        val contrastScale = (1.0f + adjustment.contrast).coerceIn(0.2f, 3.0f)
        val satScale = (1.0f + adjustment.saturation).coerceIn(0.0f, 3.0f)

        cm.setToSaturation(satScale)

        // Apply contrast & brightness
        val t = (1.0f - contrastScale) * 128f + brightnessShift
        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                contrastScale, 0f, 0f, 0f, t,
                0f, contrastScale, 0f, 0f, t,
                0f, 0f, contrastScale, 0f, t,
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
        // Container with Project Aspect Ratio
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

            // Video preview frame
            val previewRes = activeClip?.thumbnailResId ?: project.previewDrawableResId ?: R.drawable.img_city_vibes
            Image(
                painter = painterResource(id = previewRes),
                contentDescription = "Video Preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = colorFilter
            )

            // Before / After Indicator Badge
            if (showBeforeAfter) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
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
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timecode: e.g. 00:08 / 00:24
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
                        .background(Color.White.copy(alpha = 0.15f))
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
