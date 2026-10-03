package com.example.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.VideoClip
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

@Composable
fun MediaImportSheet(
    onImportMedia: (List<VideoClip>) -> Unit,
    onClose: () -> Unit
) {
    val recents = listOf(
        Pair("City Sunset", R.drawable.img_city_vibes),
        Pair("Coast View", R.drawable.img_coastal_sunset),
        Pair("Alpine Morning", R.drawable.img_mountain_lake)
    )

    var selectedMediaIds by remember { mutableStateOf(setOf(recents.first().second)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteSurfaceDark)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header (Matches Mockup 4)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Import", color = EliteTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EliteTextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Three Import Options (Videos, Photos, Audio)
        ImportOptionCard(
            icon = EliteIcons.Play,
            iconTint = Color(0xFF6C8CFF),
            title = "Videos",
            subtitle = "From gallery or files",
            onClick = {
                // Select and import sample video
                val clip = VideoClip(
                    name = "Imported Video",
                    uri = "imported://video",
                    durationMs = 20000L,
                    thumbnailResId = R.drawable.img_city_vibes
                )
                onImportMedia(listOf(clip))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ImportOptionCard(
            icon = EliteIcons.Video,
            iconTint = Color(0xFF00D26A),
            title = "Photos",
            subtitle = "Images for stickers or overlay",
            onClick = {
                val clip = VideoClip(
                    name = "Photo Story",
                    uri = "imported://photo",
                    durationMs = 8000L,
                    thumbnailResId = R.drawable.img_coastal_sunset
                )
                onImportMedia(listOf(clip))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ImportOptionCard(
            icon = EliteIcons.Audio,
            iconTint = Color(0xFF9C27B0),
            title = "Audio",
            subtitle = "From device or extract",
            onClick = {
                onClose()
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Recent Section
        Text(
            text = "Recent",
            color = EliteTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recents) { (name, resId) ->
                val isSelected = selectedMediaIds.contains(resId)
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) ElitePrimaryLight else EliteBorderDark, RoundedCornerShape(12.dp))
                        .clickable {
                            selectedMediaIds = if (isSelected) selectedMediaIds - resId else selectedMediaIds + resId
                        }
                ) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(ElitePrimaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // CTA: Add to Timeline
        Button(
            onClick = {
                val clips = selectedMediaIds.map { res ->
                    VideoClip(
                        name = "Clip ${clipsCounter++}",
                        uri = "res://$res",
                        durationMs = 15000L,
                        thumbnailResId = res
                    )
                }
                onImportMedia(clips)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("import_add_to_timeline"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary)
        ) {
            Text(text = "Add to Timeline", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private var clipsCounter = 1

@Composable
private fun ImportOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                .size(44.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, color = EliteTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = EliteTextSecondary, fontSize = 12.sp)
        }
    }
}
