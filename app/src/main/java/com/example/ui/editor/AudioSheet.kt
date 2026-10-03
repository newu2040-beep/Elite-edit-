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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.AudioClip
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

@Composable
fun AudioSheet(
    onAddAudio: (AudioClip) -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Music") }

    val sampleTracks = listOf(
        Triple("Chill Vibes", "03:24", listOf(0.3f, 0.6f, 0.8f, 0.4f, 0.7f, 0.9f, 0.5f, 0.2f)),
        Triple("Better Days", "02:48", listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.4f, 0.8f, 0.3f)),
        Triple("Midnight", "03:12", listOf(0.2f, 0.5f, 0.9f, 0.7f, 0.3f, 0.8f, 0.6f, 0.4f))
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = EliteTextPrimary)
            }
            Text(text = "Audio", color = EliteTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        // Tabs: Music | Extract | Device
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
                    listOf("Music", "Extract", "Device").forEach { tab ->
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Audio Extraction Card (Matches Mockup 9)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EliteCardDark)
                    .border(1.dp, EliteBorderDark, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = EliteIcons.ExtractAudio,
                        contentDescription = "Waveform",
                        tint = ElitePrimaryLight,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Extract audio from video",
                        color = EliteTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Get audio from your video file.",
                        color = EliteTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            onAddAudio(
                                AudioClip(
                                    title = "Extracted Audio Track",
                                    uri = "extracted://current_video",
                                    durationMs = 24000L,
                                    isExtracted = true,
                                    waveformAmplitudes = listOf(0.4f, 0.7f, 0.9f, 0.6f, 0.8f, 0.5f, 0.7f, 0.3f)
                                )
                            )
                            onClose()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text(text = "Extract", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Downloaded Tracks",
                color = EliteTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sample Tracks list
            sampleTracks.forEach { (title, dur, amps) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EliteCardDark)
                        .border(1.dp, EliteBorderDark, RoundedCornerShape(12.dp))
                        .clickable {
                            onAddAudio(
                                AudioClip(
                                    title = title,
                                    uri = "sample://$title",
                                    durationMs = 30000L,
                                    waveformAmplitudes = amps
                                )
                            )
                            onClose()
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ElitePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = EliteIcons.Audio, contentDescription = "Audio", tint = ElitePrimaryLight, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, color = EliteTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(text = dur, color = EliteTextSecondary, fontSize = 12.sp)
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = EliteTextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
