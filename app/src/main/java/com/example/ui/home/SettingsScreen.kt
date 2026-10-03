package com.example.ui.home

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteBackgroundDark
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var previewQuality by remember { mutableStateOf("Auto") }
    var loopPreview by remember { mutableStateOf(true) }
    var hardwareAccel by remember { mutableStateOf(true) }
    var cacheCleared by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteBackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("settings_back_button")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = EliteTextPrimary)
            }
            Text(
                text = "Settings",
                color = EliteTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Playback Category
            SettingsSectionHeader(title = "Playback & Preview")
            SettingsCard {
                SettingsDropdownRow(
                    label = "Preview Quality",
                    value = previewQuality,
                    options = listOf("Auto", "Low Preview", "Medium Preview", "High Preview"),
                    onSelect = { previewQuality = it }
                )
                SettingsDivider()
                SettingsToggleRow(
                    label = "Loop Preview",
                    checked = loopPreview,
                    onCheckedChange = { loopPreview = it }
                )
                SettingsDivider()
                SettingsToggleRow(
                    label = "Hardware Acceleration",
                    checked = hardwareAccel,
                    onCheckedChange = { hardwareAccel = it }
                )
            }

            // Export Defaults
            SettingsSectionHeader(title = "Export Defaults")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Default Resolution", color = EliteTextPrimary, fontSize = 14.sp)
                    Text(text = "1080p (FHD)", color = ElitePrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                SettingsDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Default Codec", color = EliteTextPrimary, fontSize = 14.sp)
                    Text(text = "H.264 High Profile", color = ElitePrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Storage Category
            SettingsSectionHeader(title = "Storage & Cache")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Timeline Cache Size", color = EliteTextPrimary, fontSize = 14.sp)
                        Text(text = if (cacheCleared) "0.0 MB" else "142.8 MB", color = EliteTextSecondary, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { cacheCleared = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EliteCardDark),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(EliteBorderDark)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = if (cacheCleared) "Cleared" else "Clear Cache", color = EliteTextPrimary, fontSize = 12.sp)
                    }
                }
            }

            // Permissions Category
            SettingsSectionHeader(title = "System Permissions")
            SettingsCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Media, Storage & Alerts", color = EliteTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Gallery, Notifications, Full Storage", color = EliteTextSecondary, fontSize = 12.sp)
                        }
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Button(
                            onClick = {
                                val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = android.net.Uri.fromParts("package", context.packageName, null)
                                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EliteCardDark),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(EliteBorderDark)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "Manage", color = ElitePrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // About Category
            SettingsSectionHeader(title = "About")
            SettingsCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "ELITE EDIT", color = EliteTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Version 1.0.0 (Production Build)", color = EliteTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Made with love by Rahul Shah for creatives who demand excellence in every frame.",
                        color = EliteTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = EliteTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EliteSurfaceDark)
            .border(1.dp, EliteBorderDark, RoundedCornerShape(14.dp))
    ) {
        content()
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(EliteBorderDark)
    )
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = EliteTextPrimary, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ElitePrimary,
                uncheckedTrackColor = EliteBorderDark
            )
        )
    }
}

@Composable
private fun SettingsDropdownRow(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = EliteTextPrimary, fontSize = 14.sp)
        Box {
            Text(text = value, color = ElitePrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(EliteCardDark)
            ) {
                options.forEach { opt ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(opt, color = EliteTextPrimary) },
                        onClick = {
                            onSelect(opt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
