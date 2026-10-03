package com.example.ui.editor

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExportBitrate
import com.example.data.model.ExportCodec
import com.example.data.model.ExportResolution
import com.example.data.model.ExportSettings
import com.example.media.engine.ExportState
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryGradient
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

@Composable
fun ExportScreen(
    currentSettings: ExportSettings,
    onStartExport: (ExportSettings) -> Unit,
    onClose: () -> Unit
) {
    var resolution by remember { mutableStateOf(currentSettings.resolution) }
    var fps by remember { mutableStateOf(currentSettings.frameRate) }
    var bitrate by remember { mutableStateOf(currentSettings.bitrateMode) }
    var codec by remember { mutableStateOf(currentSettings.codec) }
    var isHighQuality by remember { mutableStateOf(currentSettings.highQuality) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteSurfaceDark)
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = EliteTextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Export", color = EliteTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Resolution
            Text(text = "Resolution", color = EliteTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(EliteCardDark)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExportResolution.values().forEach { res ->
                    val isSelected = resolution == res
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ElitePrimary else Color.Transparent)
                            .clickable { resolution = res }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = res.label, color = if (isSelected) Color.White else EliteTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Frame Rate (FPS)
            Text(text = "Frame Rate (FPS)", color = EliteTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(EliteCardDark)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(24, 25, 30, 50, 60).forEach { frameRate ->
                    val isSelected = fps == frameRate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ElitePrimary else Color.Transparent)
                            .clickable { fps = frameRate }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$frameRate", color = if (isSelected) Color.White else EliteTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Bitrate (Mbps)
            Text(text = "Bitrate (Mbps)", color = EliteTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(EliteCardDark)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(ExportBitrate.LOW, ExportBitrate.MEDIUM, ExportBitrate.HIGH).forEach { mode ->
                    val isSelected = bitrate == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ElitePrimary else Color.Transparent)
                            .clickable { bitrate = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = mode.label, color = if (isSelected) Color.White else EliteTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Format (H.264 / H.265)
            Text(text = "Format", color = EliteTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(EliteCardDark)
                    .padding(3.dp)
            ) {
                ExportCodec.values().forEach { c ->
                    val isSelected = codec == c
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ElitePrimary else Color.Transparent)
                            .clickable { codec = c }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = c.label, color = if (isSelected) Color.White else EliteTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // High Quality Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(EliteCardDark)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "High Quality", color = EliteTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Switch(
                    checked = isHighQuality,
                    onCheckedChange = { isHighQuality = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ElitePrimary,
                        uncheckedTrackColor = EliteBorderDark
                    )
                )
            }
        }

        // Export CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Button(
                onClick = {
                    onStartExport(
                        ExportSettings(
                            resolution = resolution,
                            frameRate = fps,
                            bitrateMode = bitrate,
                            codec = codec,
                            highQuality = isHighQuality
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("export_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(text = "Export", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ExportingScreen(
    state: ExportState.Progress,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteSurfaceDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Exporting...",
                color = EliteTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "This may take a few moments.",
                color = EliteTextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Large Circular Progress (Matches Mockup 12)
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { state.progressPercent / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = ElitePrimaryLight,
                    strokeWidth = 8.dp,
                    trackColor = EliteCardDark
                )
                Text(
                    text = "${state.progressPercent}%",
                    color = EliteTextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "${state.resolutionText} · ${state.fpsText} · ${state.codecText}",
                color = EliteTextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .width(180.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(EliteBorderDark))
            ) {
                Text(text = "Cancel", color = EliteTextSecondary)
            }
        }
    }
}

@Composable
fun ExportCompleteScreen(
    outputPath: String,
    onViewVideo: () -> Unit,
    onShare: () -> Unit,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteSurfaceDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Checkmark Circle (Matches Mockup 13)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2837))
                    .border(2.dp, ElitePrimaryLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = ElitePrimaryLight,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Export Complete",
                color = EliteTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your video has been saved to gallery.",
                color = EliteTextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Primary View Button
            Button(
                onClick = onViewVideo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary)
            ) {
                Text(text = "View", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Share Button
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(EliteBorderDark))
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = EliteTextPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Share", color = EliteTextPrimary, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Back to Home
            TextButton(onClick = onBackToHome) {
                Text(text = "Back to Home", color = EliteTextSecondary, fontSize = 14.sp)
            }
        }
    }
}
