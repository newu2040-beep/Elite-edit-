package com.example.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ColorAdjustment
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryGradient
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary
import com.example.ui.theme.EliteTimelineBg
import java.util.Locale

enum class AdjustCategoryTab {
    PRESETS,
    ADJUST,
    HSL,
    CURVES,
    LUT
}

@Composable
fun AdjustScreen(
    currentAdjustment: ColorAdjustment,
    showBeforeAfter: Boolean,
    onToggleBeforeAfter: () -> Unit,
    onApplyAdjustment: (ColorAdjustment) -> Unit,
    onClose: () -> Unit
) {
    var workingAdjustment by remember { mutableStateOf(currentAdjustment) }
    var selectedTab by remember { mutableStateOf(AdjustCategoryTab.ADJUST) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteSurfaceDark)
            .navigationBarsPadding()
    ) {
        // Header: Back, Title, Checkmark
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose, modifier = Modifier.testTag("adjust_back")) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = EliteTextPrimary
                )
            }
            Text(
                text = "Adjust",
                color = EliteTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(
                onClick = {
                    onApplyAdjustment(workingAdjustment)
                    onClose()
                },
                modifier = Modifier.testTag("adjust_apply")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Apply",
                    tint = ElitePrimaryLight
                )
            }
        }

        // Subtabs: Presets | Adjust | HSL | Curves | LUT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(EliteCardDark)
                    .padding(4.dp)
            ) {
                Row {
                    AdjustCategoryTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) ElitePrimary else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (isSelected) Color.White else EliteTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Before / After toggle button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (showBeforeAfter) ElitePrimaryLight else EliteCardDark)
                    .clickable { onToggleBeforeAfter() }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (showBeforeAfter) "Original Active" else "Hold Before/After",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }

        // Content Area depending on tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                AdjustCategoryTab.PRESETS -> PresetsTab(
                    activePreset = workingAdjustment.activePreset,
                    onSelectPreset = { preset, adj ->
                        workingAdjustment = adj.copy(activePreset = preset)
                    }
                )
                AdjustCategoryTab.ADJUST -> SlidersTab(
                    adjustment = workingAdjustment,
                    onAdjustmentChange = { workingAdjustment = it }
                )
                AdjustCategoryTab.HSL -> HslTab(
                    adjustment = workingAdjustment,
                    onAdjustmentChange = { workingAdjustment = it }
                )
                AdjustCategoryTab.CURVES -> CurvesTab(
                    adjustment = workingAdjustment,
                    onAdjustmentChange = { workingAdjustment = it }
                )
                AdjustCategoryTab.LUT -> LutTab(
                    adjustment = workingAdjustment,
                    onAdjustmentChange = { workingAdjustment = it }
                )
            }
        }

        // Bottom Reset & Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = EliteBorderDark)
                .background(EliteSurfaceDark)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { workingAdjustment = ColorAdjustment() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = EliteTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reset All",
                    color = EliteTextSecondary,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "Non-destructive · 32-bit",
                color = EliteTextSecondary.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun SlidersTab(
    adjustment: ColorAdjustment,
    onAdjustmentChange: (ColorAdjustment) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        AdjustSliderItem(
            label = "Exposure",
            value = adjustment.exposure,
            valueDisplay = String.format(Locale.US, "%+.2f", adjustment.exposure),
            range = -1f..1f,
            onValueChange = { onAdjustmentChange(adjustment.copy(exposure = it)) }
        )
        AdjustSliderItem(
            label = "Contrast",
            value = adjustment.contrast,
            valueDisplay = String.format(Locale.US, "%+d", (adjustment.contrast * 100).toInt()),
            range = -1f..1f,
            onValueChange = { onAdjustmentChange(adjustment.copy(contrast = it)) }
        )
        AdjustSliderItem(
            label = "Highlights",
            value = adjustment.highlights,
            valueDisplay = String.format(Locale.US, "%+d", (adjustment.highlights * 100).toInt()),
            range = -1f..1f,
            onValueChange = { onAdjustmentChange(adjustment.copy(highlights = it)) }
        )
        AdjustSliderItem(
            label = "Shadows",
            value = adjustment.shadows,
            valueDisplay = String.format(Locale.US, "%+d", (adjustment.shadows * 100).toInt()),
            range = -1f..1f,
            onValueChange = { onAdjustmentChange(adjustment.copy(shadows = it)) }
        )
        AdjustSliderItem(
            label = "Saturation",
            value = adjustment.saturation,
            valueDisplay = String.format(Locale.US, "%+d", (adjustment.saturation * 100).toInt()),
            range = -1f..1f,
            onValueChange = { onAdjustmentChange(adjustment.copy(saturation = it)) }
        )
        AdjustSliderItem(
            label = "Temperature",
            value = adjustment.temperature,
            valueDisplay = String.format(Locale.US, "%+d", (adjustment.temperature * 100).toInt()),
            range = -1f..1f,
            onValueChange = { onAdjustmentChange(adjustment.copy(temperature = it)) }
        )
        AdjustSliderItem(
            label = "Vignette",
            value = adjustment.vignette,
            valueDisplay = String.format(Locale.US, "%d%%", (adjustment.vignette * 100).toInt()),
            range = 0f..1f,
            onValueChange = { onAdjustmentChange(adjustment.copy(vignette = it)) }
        )
        AdjustSliderItem(
            label = "Sharpness",
            value = adjustment.sharpness,
            valueDisplay = String.format(Locale.US, "%d%%", (adjustment.sharpness * 100).toInt()),
            range = 0f..1f,
            onValueChange = { onAdjustmentChange(adjustment.copy(sharpness = it)) }
        )
    }
}

@Composable
private fun AdjustSliderItem(
    label: String,
    value: Float,
    valueDisplay: String,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = EliteTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(text = valueDisplay, color = ElitePrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = ElitePrimaryLight,
                activeTrackColor = ElitePrimary,
                inactiveTrackColor = EliteBorderDark
            )
        )
    }
}

@Composable
private fun PresetsTab(
    activePreset: String,
    onSelectPreset: (String, ColorAdjustment) -> Unit
) {
    val presets = listOf(
        "Normal" to ColorAdjustment(),
        "Cinematic" to ColorAdjustment(exposure = 0.15f, contrast = 0.20f, shadows = -0.1f, saturation = 0.12f, vignette = 0.2f),
        "Warm Glow" to ColorAdjustment(temperature = 0.35f, exposure = 0.10f, saturation = 0.15f),
        "Cool Moody" to ColorAdjustment(temperature = -0.30f, contrast = 0.18f, saturation = -0.1f),
        "Cyberpunk" to ColorAdjustment(contrast = 0.30f, saturation = 0.45f, tint = 0.25f),
        "Vintage" to ColorAdjustment(contrast = -0.15f, fade = 0.25f, vignette = 0.35f, temperature = 0.15f),
        "Noir" to ColorAdjustment(saturation = -1.0f, contrast = 0.35f, shadows = -0.2f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        presets.forEach { (name, adj) ->
            val isSelected = activePreset == name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) ElitePrimary.copy(alpha = 0.2f) else EliteCardDark)
                    .border(1.dp, if (isSelected) ElitePrimary else EliteBorderDark, RoundedCornerShape(12.dp))
                    .clickable { onSelectPreset(name, adj) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    color = if (isSelected) ElitePrimaryLight else EliteTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = ElitePrimaryLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HslTab(
    adjustment: ColorAdjustment,
    onAdjustmentChange: (ColorAdjustment) -> Unit
) {
    var selectedColor by remember { mutableStateOf("Red") }
    val channels = listOf("Red", "Orange", "Yellow", "Green", "Cyan", "Blue", "Purple", "Magenta")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Color Channel selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            channels.forEach { name ->
                val isSelected = selectedColor == name
                val dotColor = when (name) {
                    "Red" -> Color.Red
                    "Orange" -> Color(0xFFFF9800)
                    "Yellow" -> Color.Yellow
                    "Green" -> Color.Green
                    "Cyan" -> Color.Cyan
                    "Blue" -> Color.Blue
                    "Purple" -> Color(0xFF9C27B0)
                    else -> Color(0xFFE91E63)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) ElitePrimary else EliteCardDark)
                        .clickable { selectedColor = name }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = name, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val currentChannel = adjustment.hslValues[selectedColor] ?: com.example.data.model.HslChannel()

        AdjustSliderItem(
            label = "$selectedColor Hue",
            value = currentChannel.hue,
            valueDisplay = String.format(Locale.US, "%+.0f°", currentChannel.hue),
            range = -180f..180f,
            onValueChange = { newHue ->
                val updatedMap = adjustment.hslValues.toMutableMap()
                updatedMap[selectedColor] = currentChannel.copy(hue = newHue)
                onAdjustmentChange(adjustment.copy(hslValues = updatedMap))
            }
        )
        AdjustSliderItem(
            label = "$selectedColor Saturation",
            value = currentChannel.saturation,
            valueDisplay = String.format(Locale.US, "%+.0f", currentChannel.saturation),
            range = -100f..100f,
            onValueChange = { newSat ->
                val updatedMap = adjustment.hslValues.toMutableMap()
                updatedMap[selectedColor] = currentChannel.copy(saturation = newSat)
                onAdjustmentChange(adjustment.copy(hslValues = updatedMap))
            }
        )
        AdjustSliderItem(
            label = "$selectedColor Luminance",
            value = currentChannel.luminance,
            valueDisplay = String.format(Locale.US, "%+.0f", currentChannel.luminance),
            range = -100f..100f,
            onValueChange = { newLum ->
                val updatedMap = adjustment.hslValues.toMutableMap()
                updatedMap[selectedColor] = currentChannel.copy(luminance = newLum)
                onAdjustmentChange(adjustment.copy(hslValues = updatedMap))
            }
        )
    }
}

@Composable
private fun CurvesTab(
    adjustment: ColorAdjustment,
    onAdjustmentChange: (ColorAdjustment) -> Unit
) {
    var curveMode by remember { mutableStateOf("RGB") }
    var midPointY by remember { mutableFloatStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Curve channel pills (RGB, Red, Green, Blue)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf("RGB", "Red", "Green", "Blue").forEach { channel ->
                val isSelected = curveMode == channel
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) ElitePrimary else EliteCardDark)
                        .clickable { curveMode = channel }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = channel,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Graph Canvas
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(EliteTimelineBg)
                .border(1.dp, EliteBorderDark, RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        midPointY = (midPointY - dragAmount.y / 240f).coerceIn(0.1f, 0.9f)
                        onAdjustmentChange(adjustment.copy(contrast = (midPointY - 0.5f) * 1.5f))
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Grid lines
                for (i in 1..3) {
                    val lineX = size.width * (i / 4f)
                    val lineY = size.height * (i / 4f)
                    drawLine(Color(0xFF252838), Offset(lineX, 0f), Offset(lineX, size.height), 1f)
                    drawLine(Color(0xFF252838), Offset(0f, lineY), Offset(size.width, lineY), 1f)
                }

                val strokeColor = when (curveMode) {
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color(0xFF4C66C9)
                    else -> Color.White
                }

                // S-curve graph
                val path = Path().apply {
                    moveTo(0f, size.height)
                    cubicTo(
                        size.width * 0.25f, size.height * (1f - (midPointY * 0.5f)),
                        size.width * 0.75f, size.height * (1f - (midPointY * 1.5f).coerceIn(0f, 1f)),
                        size.width, 0f
                    )
                }
                drawPath(path, strokeColor, style = Stroke(width = 3.dp.toPx()))

                // Draggable point
                drawCircle(
                    color = ElitePrimaryLight,
                    radius = 7.dp.toPx(),
                    center = Offset(size.width * 0.5f, size.height * (1f - midPointY))
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Drag graph center to adjust curve response", color = EliteTextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun LutTab(
    adjustment: ColorAdjustment,
    onAdjustmentChange: (ColorAdjustment) -> Unit
) {
    val luts = listOf("None", "Teal & Orange", "Faded Film", "Golden Hour", "Monochrome 1970", "Neon Tokyo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        luts.forEach { name ->
            val isSelected = adjustment.lutName == name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) ElitePrimary.copy(alpha = 0.2f) else EliteCardDark)
                    .border(1.dp, if (isSelected) ElitePrimary else EliteBorderDark, RoundedCornerShape(12.dp))
                    .clickable { onAdjustmentChange(adjustment.copy(lutName = name)) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = name, color = Color.White, fontSize = 14.sp)
                if (isSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = ElitePrimaryLight)
                }
            }
        }
    }
}
