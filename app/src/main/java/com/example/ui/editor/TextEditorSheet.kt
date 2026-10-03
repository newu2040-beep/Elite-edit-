package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TextAnimationType
import com.example.data.model.TextLayer
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

enum class TextTab {
    FONT, STYLE, ANIMATION
}

@Composable
fun TextEditorSheet(
    initialTextLayer: TextLayer?,
    onSaveText: (TextLayer) -> Unit,
    onClose: () -> Unit
) {
    var textContent by remember { mutableStateOf(initialTextLayer?.text ?: "Good Vibes") }
    var selectedFont by remember { mutableStateOf(initialTextLayer?.fontFamilyName ?: "Inter") }
    var fontSize by remember { mutableStateOf(initialTextLayer?.fontSizeSp ?: 28f) }
    var isBold by remember { mutableStateOf(initialTextLayer?.isBold ?: true) }
    var isItalic by remember { mutableStateOf(initialTextLayer?.isItalic ?: false) }
    var selectedColorHex by remember { mutableStateOf(initialTextLayer?.textColorHex ?: "#FFFFFF") }
    var selectedAnimation by remember { mutableStateOf(initialTextLayer?.animationType ?: TextAnimationType.POP) }
    var activeTab by remember { mutableStateOf(TextTab.FONT) }

    val fonts = listOf("Inter", "Montserrat", "Poppins", "Playfair Display", "Roboto", "Nunito")
    val colors = listOf("#FFFFFF", "#FF5252", "#FFD700", "#4CAF50", "#2196F3", "#9C27B0", "#00E5FF")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteSurfaceDark)
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = EliteTextPrimary)
            }
            Text(text = "Text", color = EliteTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            IconButton(
                onClick = {
                    val layer = (initialTextLayer ?: TextLayer()).copy(
                        text = textContent,
                        fontFamilyName = selectedFont,
                        fontSizeSp = fontSize,
                        isBold = isBold,
                        isItalic = isItalic,
                        textColorHex = selectedColorHex,
                        animationType = selectedAnimation
                    )
                    onSaveText(layer)
                    onClose()
                },
                modifier = Modifier.testTag("save_text_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = ElitePrimaryLight)
            }
        }

        // Live Text Input Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = textContent,
                onValueChange = { textContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("text_input_field"),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = EliteTextPrimary,
                    unfocusedTextColor = EliteTextPrimary,
                    focusedContainerColor = EliteCardDark,
                    unfocusedContainerColor = EliteCardDark,
                    focusedIndicatorColor = ElitePrimary,
                    unfocusedIndicatorColor = EliteBorderDark
                ),
                placeholder = { Text("Enter your text...", color = EliteTextSecondary) }
            )
        }

        // Tabs: Font | Style | Animation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(EliteCardDark)
                    .padding(4.dp)
            ) {
                Row {
                    TextTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ElitePrimary else Color.Transparent)
                                .clickable { activeTab = tab }
                                .padding(horizontal = 18.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (isSelected) Color.White else EliteTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (activeTab) {
                TextTab.FONT -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fonts.forEach { fontName ->
                            val isSelected = selectedFont == fontName
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) ElitePrimary.copy(alpha = 0.2f) else EliteCardDark)
                                    .border(1.dp, if (isSelected) ElitePrimary else EliteBorderDark, RoundedCornerShape(12.dp))
                                    .clickable { selectedFont = fontName }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = fontName,
                                    color = if (isSelected) ElitePrimaryLight else EliteTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = ElitePrimaryLight)
                                }
                            }
                        }
                    }
                }
                TextTab.STYLE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "Font Size (${fontSize.toInt()}sp)", color = EliteTextPrimary, fontSize = 13.sp)
                        Slider(
                            value = fontSize,
                            onValueChange = { fontSize = it },
                            valueRange = 14f..64f,
                            colors = SliderDefaults.colors(thumbColor = ElitePrimaryLight, activeTrackColor = ElitePrimary)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Colors", color = EliteTextPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            colors.forEach { hex ->
                                val color = Color(android.graphics.Color.parseColor(hex))
                                val isSelected = selectedColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(if (isSelected) 3.dp else 1.dp, if (isSelected) ElitePrimaryLight else Color.Transparent, CircleShape)
                                        .clickable { selectedColorHex = hex }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isBold) ElitePrimary else EliteCardDark)
                                    .clickable { isBold = !isBold }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Bold", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isItalic) ElitePrimary else EliteCardDark)
                                    .clickable { isItalic = !isItalic }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Italic", color = Color.White)
                            }
                        }
                    }
                }
                TextTab.ANIMATION -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextAnimationType.values().forEach { anim ->
                            val isSelected = selectedAnimation == anim
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) ElitePrimary.copy(alpha = 0.2f) else EliteCardDark)
                                    .border(1.dp, if (isSelected) ElitePrimary else EliteBorderDark, RoundedCornerShape(12.dp))
                                    .clickable { selectedAnimation = anim }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = anim.label, color = Color.White, fontSize = 15.sp)
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = ElitePrimaryLight)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
