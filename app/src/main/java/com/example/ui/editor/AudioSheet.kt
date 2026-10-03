package com.example.ui.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioClip
import com.example.data.model.VideoClip
import com.example.media.engine.MediaStorageHelper
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary
import kotlinx.coroutines.launch

@Composable
fun AudioSheet(
    activeClip: VideoClip?,
    onAddAudio: (AudioClip) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf("Device") }
    var isExtracting by remember { mutableStateOf(false) }
    var isImportingAudio by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }
    var importedAudioTracks by remember { mutableStateOf<List<AudioClip>>(emptyList()) }

    // Real system audio picker
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isImportingAudio = true
                statusText = "Importing audio file..."
                try {
                    val clip = MediaStorageHelper.importAudio(context, uri)
                    importedAudioTracks = importedAudioTracks + clip
                    onAddAudio(clip)
                    onClose()
                } catch (e: Exception) {
                    statusText = "Failed to import audio: ${e.message}"
                } finally {
                    isImportingAudio = false
                }
            }
        }
    }

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
            Text(text = "Audio Studio", color = EliteTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        // Tabs: Device | Extract
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
                    listOf("Device", "Extract").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ElitePrimary else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 24.dp, vertical = 6.dp)
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
            if (selectedTab == "Device") {
                // Select Audio from device
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EliteCardDark)
                        .border(1.dp, EliteBorderDark, RoundedCornerShape(16.dp))
                        .clickable { audioPickerLauncher.launch("audio/*") }
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Audio Files",
                            tint = ElitePrimaryLight,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Select Audio from Device",
                            color = EliteTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Import MP3, WAV, AAC from device storage",
                            color = EliteTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { audioPickerLauncher.launch("audio/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Browse", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Browse Device Audio", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                // Audio Extraction Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EliteCardDark)
                        .border(1.dp, EliteBorderDark, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
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
                            text = if (activeClip != null && activeClip.isVideo)
                                "Extract soundtrack from: ${activeClip.name}"
                            else
                                "No video clip currently selected",
                            color = EliteTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (activeClip != null && activeClip.isVideo) {
                                    scope.launch {
                                        isExtracting = true
                                        statusText = "Extracting audio track..."
                                        try {
                                            val extracted = MediaStorageHelper.extractAudioFromVideoFile(context, activeClip.uri)
                                            onAddAudio(extracted)
                                            onClose()
                                        } catch (e: Exception) {
                                            statusText = "Extraction failed: ${e.message}"
                                        } finally {
                                            isExtracting = false
                                        }
                                    }
                                }
                            },
                            enabled = activeClip != null && activeClip.isVideo && !isExtracting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            if (isExtracting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text(text = "Extract Real Audio", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            if (isImportingAudio || isExtracting) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EliteCardDark)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ElitePrimaryLight)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = statusText, color = EliteTextPrimary, fontSize = 12.sp)
                }
            }

            if (importedAudioTracks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Recently Added Audio",
                    color = EliteTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                importedAudioTracks.forEach { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EliteCardDark)
                            .border(1.dp, EliteBorderDark, RoundedCornerShape(12.dp))
                            .clickable {
                                onAddAudio(track)
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
                            Text(text = track.title, color = EliteTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            val sec = track.durationMs / 1000L
                            Text(text = String.format(java.util.Locale.US, "%02d:%02d", sec / 60, sec % 60), color = EliteTextSecondary, fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Select", tint = ElitePrimaryLight)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
