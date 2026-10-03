package com.example.ui.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.data.model.VideoClip
import com.example.media.engine.MediaStorageHelper
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@Composable
fun MediaImportSheet(
    onImportMedia: (List<VideoClip>) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isProcessing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var importedClips by remember { mutableStateOf<List<VideoClip>>(emptyList()) }

    fun processUris(uris: List<Uri>, isVideoRequested: Boolean) {
        if (uris.isEmpty()) return
        scope.launch {
            isProcessing = true
            statusMessage = "Importing ${uris.size} item(s)..."
            val newClips = mutableListOf<VideoClip>()
            uris.forEachIndexed { index, uri ->
                statusMessage = "Processing file ${index + 1}/${uris.size}..."
                try {
                    val clip = MediaStorageHelper.importMedia(context, uri, isVideoRequested)
                    newClips.add(clip)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Could not import: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            if (newClips.isNotEmpty()) {
                importedClips = importedClips + newClips
            }
            isProcessing = false
        }
    }

    // Android Photo Picker for Videos
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        processUris(uris, isVideoRequested = true)
    }

    // Android Photo Picker for Photos
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        processUris(uris, isVideoRequested = false)
    }

    // System File Picker for All Media (Images and Videos)
    val allFilesPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        processUris(uris, isVideoRequested = true)
    }

    // Fallback Single Content Video Picker
    val fallbackVideoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { processUris(listOf(it), isVideoRequested = true) }
    }

    // Fallback Single Content Image Picker
    val fallbackImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { processUris(listOf(it), isVideoRequested = false) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteSurfaceDark)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (importedClips.isNotEmpty()) 80.dp else 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Import Device Media",
                    color = EliteTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EliteTextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select high-resolution videos or photos directly from your device storage or camera roll.",
                color = EliteTextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Option 1: Device Videos (Photo Picker)
            ImportActionCard(
                icon = Icons.Default.Videocam,
                iconTint = Color(0xFF6C8CFF),
                title = "Choose Videos from Device",
                subtitle = "MP4, MOV, MKV from camera roll or albums",
                onClick = {
                    try {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    } catch (e: Exception) {
                        fallbackVideoPickerLauncher.launch("video/*")
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2: Device Photos
            ImportActionCard(
                icon = Icons.Default.PhotoLibrary,
                iconTint = Color(0xFF00D26A),
                title = "Choose Photos from Device",
                subtitle = "JPG, PNG, WEBP photos for video clips or slideshows",
                onClick = {
                    try {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    } catch (e: Exception) {
                        fallbackImagePickerLauncher.launch("image/*")
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option 3: Browse Device Files & Storage (Universal)
            ImportActionCard(
                icon = Icons.Default.Folder,
                iconTint = Color(0xFFFD79A8),
                title = "Browse Device Files & Storage",
                subtitle = "Select any video or image file from storage / downloads",
                onClick = {
                    try {
                        allFilesPickerLauncher.launch("*/*")
                    } catch (e: Exception) {
                        fallbackVideoPickerLauncher.launch("*/*")
                    }
                }
            )

            // Loading / Processing State
            if (isProcessing) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EliteCardDark)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = ElitePrimaryLight,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = statusMessage,
                        color = EliteTextPrimary,
                        fontSize = 13.sp
                    )
                }
            }

            // Imported Clips List
            if (importedClips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected Clips (${importedClips.size})",
                        color = EliteTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Clear All",
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { importedClips = emptyList() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(importedClips) { clip ->
                        Box(
                            modifier = Modifier
                                .size(width = 110.dp, height = 160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EliteCardDark)
                                .border(1.5.dp, ElitePrimaryLight, RoundedCornerShape(12.dp))
                        ) {
                            val thumbFile = clip.thumbnailPath?.let { File(it) }
                            val imageModel: Any = if (thumbFile != null && thumbFile.exists()) {
                                thumbFile
                            } else {
                                File(clip.uri)
                            }

                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(context)
                                        .data(imageModel)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = clip.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Type badge (VIDEO or PHOTO)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (clip.isVideo) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                    }
                                    Text(
                                        text = if (clip.isVideo) "VIDEO" else "PHOTO",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Duration badge
                            val sec = clip.durationMs / 1000L
                            Text(
                                text = String.format(Locale.US, "%02d:%02d", sec / 60, sec % 60),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // STICKY BOTTOM ACTION BAR - Always visible when media is selected!
        if (importedClips.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(EliteSurfaceDark)
                    .border(width = 0.5.dp, color = EliteBorderDark)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        onImportMedia(importedClips)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("import_add_to_timeline"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Add", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open in Video Editor (${importedClips.size} items)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportActionCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EliteCardDark)
            .border(1.dp, EliteBorderDark, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = EliteTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = EliteTextSecondary, fontSize = 12.sp)
        }
    }
}
