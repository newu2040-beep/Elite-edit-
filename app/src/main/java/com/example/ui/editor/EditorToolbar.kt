package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

@Composable
fun EditorToolbar(
    onCut: () -> Unit,
    onSplit: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onOpenText: () -> Unit,
    onOpenStickers: () -> Unit,
    onOpenAdjust: () -> Unit,
    onOpenAudio: () -> Unit,
    onOpenCanvas: () -> Unit,
    onOpenSpeed: () -> Unit,
    hasCopiedAdjustment: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(EliteSurfaceDark)
            .navigationBarsPadding()
    ) {
        // Quick clip action bar (Matches mockup 5: Cut, Split, Copy, Paste, Delete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolItem(
                icon = EliteIcons.Cut,
                label = "Cut",
                testTag = "tool_cut",
                onClick = onCut
            )
            ToolItem(
                icon = EliteIcons.Split,
                label = "Split",
                testTag = "tool_split",
                onClick = onSplit
            )
            ToolItem(
                icon = EliteIcons.Copy,
                label = "Copy",
                testTag = "tool_copy",
                onClick = onCopy
            )
            ToolItem(
                icon = EliteIcons.Paste,
                label = "Paste",
                testTag = "tool_paste",
                isEnabled = hasCopiedAdjustment,
                onClick = onPaste
            )
            ToolItem(
                icon = EliteIcons.Duplicate,
                label = "Duplicate",
                testTag = "tool_duplicate",
                onClick = onDuplicate
            )
            ToolItem(
                icon = EliteIcons.Delete,
                label = "Delete",
                testTag = "tool_delete",
                onClick = onDelete
            )
        }

        // Secondary feature tools row (Text, Stickers, Adjust, Audio, Canvas, Speed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EliteCardDark)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FeatureToolButton(
                icon = EliteIcons.Text,
                label = "Text",
                testTag = "tool_text",
                onClick = onOpenText
            )
            FeatureToolButton(
                icon = EliteIcons.Sticker,
                label = "Stickers",
                testTag = "tool_stickers",
                onClick = onOpenStickers
            )
            FeatureToolButton(
                icon = EliteIcons.Adjust,
                label = "Adjust",
                testTag = "tool_adjust",
                onClick = onOpenAdjust
            )
            FeatureToolButton(
                icon = EliteIcons.Audio,
                label = "Audio",
                testTag = "tool_audio",
                onClick = onOpenAudio
            )
            FeatureToolButton(
                icon = EliteIcons.CanvasRatio,
                label = "Canvas",
                testTag = "tool_canvas",
                onClick = onOpenCanvas
            )
            FeatureToolButton(
                icon = EliteIcons.Speed,
                label = "Speed",
                testTag = "tool_speed",
                onClick = onOpenSpeed
            )
        }
    }
}

@Composable
private fun ToolItem(
    icon: ImageVector,
    label: String,
    testTag: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled) { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isEnabled) EliteTextPrimary else EliteTextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isEnabled) EliteTextSecondary else EliteTextSecondary.copy(alpha = 0.4f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun FeatureToolButton(
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = EliteTextPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = label,
            color = EliteTextPrimary,
            fontSize = 12.sp
        )
    }
}
