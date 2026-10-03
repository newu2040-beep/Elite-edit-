package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StickerLayer
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

@Composable
fun StickerSheet(
    onAddSticker: (StickerLayer) -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Built-in") }

    val builtInStickers = listOf(
        Triple("Good Vibes", "badge", "✨ Good Vibes"),
        Triple("Smiley", "smiley", "😊"),
        Triple("Sparkles", "sparkles", "✨"),
        Triple("Palm Tree", "tree", "🌴"),
        Triple("Heart", "heart", "❤️"),
        Triple("Wave", "wave", "🌊"),
        Triple("Moon", "moon", "🌙"),
        Triple("Camera", "camera", "📷"),
        Triple("Sun", "sun", "☀️"),
        Triple("Fire", "fire", "🔥"),
        Triple("Star", "star", "⭐"),
        Triple("Rocket", "rocket", "🚀")
    )

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
            Text(text = "Stickers", color = EliteTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = ElitePrimaryLight)
            }
        }

        // Tabs: Built-in | My Stickers
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
                    listOf("Built-in", "My Stickers").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ElitePrimary else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.White else EliteTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Stickers Grid (Matches Mockup 7)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(builtInStickers) { (name, key, display) ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(EliteCardDark)
                        .border(1.dp, EliteBorderDark, RoundedCornerShape(14.dp))
                        .clickable {
                            onAddSticker(
                                StickerLayer(
                                    name = name,
                                    iconKey = key
                                )
                            )
                            onClose()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = display,
                        fontSize = if (display.length > 2) 16.sp else 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
