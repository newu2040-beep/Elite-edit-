package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CanvasAspectRatio
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

@Composable
fun CanvasSheet(
    currentRatio: CanvasAspectRatio,
    onSelectRatio: (CanvasAspectRatio) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EliteSurfaceDark)
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = EliteTextPrimary)
            }
            Text(text = "Canvas Aspect Ratio", color = EliteTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = ElitePrimaryLight)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CanvasAspectRatio.values().filterNot { it == CanvasAspectRatio.CUSTOM }.forEach { ratio ->
                val isSelected = currentRatio == ratio
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectRatio(ratio) }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ElitePrimary else EliteCardDark)
                            .border(1.dp, if (isSelected) ElitePrimaryLight else EliteBorderDark, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ratio.label,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (ratio) {
                            CanvasAspectRatio.RATIO_9_16 -> "Reels/TikTok"
                            CanvasAspectRatio.RATIO_16_9 -> "YouTube"
                            CanvasAspectRatio.RATIO_4_5 -> "Instagram"
                            CanvasAspectRatio.RATIO_1_1 -> "Square"
                            CanvasAspectRatio.RATIO_4_3 -> "Standard"
                            else -> ""
                        },
                        color = EliteTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
