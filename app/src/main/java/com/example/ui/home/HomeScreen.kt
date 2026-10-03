package com.example.ui.home

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File
import com.example.R
import com.example.data.model.Project
import com.example.ui.icons.EliteIcons
import com.example.ui.theme.EliteBackgroundDark
import com.example.ui.theme.EliteBorderDark
import com.example.ui.theme.EliteCardDark
import com.example.ui.theme.ElitePrimary
import com.example.ui.theme.ElitePrimaryLight
import com.example.ui.theme.EliteSurfaceDark
import com.example.ui.theme.EliteTextPrimary
import com.example.ui.theme.EliteTextSecondary

enum class HomeBottomTab {
    HOME, PROJECTS, TEMPLATES, SETTINGS
}

@Composable
fun HomeScreen(
    projects: List<Project>,
    onOpenProject: (String) -> Unit,
    onNewProject: () -> Unit,
    onDuplicateProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    var selectedBottomTab by remember { mutableStateOf(HomeBottomTab.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EliteBackgroundDark)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp) // space for bottom nav
        ) {
            // TOP BAR (Matches Mockup 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ELITE EDIT",
                    color = EliteTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = EliteIcons.Audio,
                            contentDescription = "Audio tools",
                            tint = EliteTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onOpenSettings, modifier = Modifier.testTag("home_settings_button")) {
                        Icon(
                            imageVector = EliteIcons.Settings,
                            contentDescription = "Settings",
                            tint = EliteTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Projects Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Projects",
                    color = EliteTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "See all",
                    color = EliteTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ElitePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Project",
                                tint = ElitePrimaryLight,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Projects Yet",
                            color = EliteTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Import video clips and photos from your device to start creating.",
                            color = EliteTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onNewProject,
                            colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Import Media & Start", color = Color.White)
                        }
                    }
                }
            } else {
                // 2x2 Grid of Project Cards (Matches Mockup 3)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(projects) { project ->
                        ProjectCardItem(
                            project = project,
                            onOpen = { onOpenProject(project.id) },
                            onDuplicate = { onDuplicateProject(project.id) },
                            onDelete = { onDeleteProject(project.id) }
                        )
                    }
                }
            }
        }

        // Primary Floating "+ New Project" Button (Matches Mockup 3)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp)
                .padding(horizontal = 24.dp)
        ) {
            Button(
                onClick = onNewProject,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("home_new_project_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElitePrimary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "New Project", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // BOTTOM NAVIGATION BAR (Matches Mockup 3)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(EliteSurfaceDark)
                .border(width = 0.5.dp, color = EliteBorderDark)
                .navigationBarsPadding()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeBottomNavItem(
                icon = EliteIcons.Play,
                label = "Home",
                isSelected = selectedBottomTab == HomeBottomTab.HOME,
                onClick = { selectedBottomTab = HomeBottomTab.HOME }
            )
            HomeBottomNavItem(
                icon = EliteIcons.Video,
                label = "Projects",
                isSelected = selectedBottomTab == HomeBottomTab.PROJECTS,
                onClick = { selectedBottomTab = HomeBottomTab.PROJECTS }
            )
            HomeBottomNavItem(
                icon = EliteIcons.Sticker,
                label = "Templates",
                isSelected = selectedBottomTab == HomeBottomTab.TEMPLATES,
                onClick = { selectedBottomTab = HomeBottomTab.TEMPLATES }
            )
            HomeBottomNavItem(
                icon = EliteIcons.Settings,
                label = "Settings",
                isSelected = selectedBottomTab == HomeBottomTab.SETTINGS,
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
private fun ProjectCardItem(
    project: Project,
    onOpen: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EliteCardDark)
            .border(1.dp, EliteBorderDark, RoundedCornerShape(14.dp))
            .clickable { onOpen() }
    ) {
        // Thumbnail Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
        ) {
            val context = LocalContext.current
            val thumbPath = project.thumbnailPath ?: project.clips.firstOrNull()?.thumbnailPath
            val thumbFile = thumbPath?.let { File(it) }
            val imageModel: Any = if (thumbFile != null && thumbFile.exists()) {
                thumbFile
            } else if (project.clips.isNotEmpty() && File(project.clips[0].uri).exists()) {
                File(project.clips[0].uri)
            } else {
                R.drawable.ic_app_icon_circle
            }

            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = project.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // More Options Icon
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            ) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(EliteCardDark)
                ) {
                    DropdownMenuItem(
                        text = { Text("Open", color = EliteTextPrimary) },
                        onClick = { showMenu = false; onOpen() }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicate", color = EliteTextPrimary) },
                        onClick = { showMenu = false; onDuplicate() }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color(0xFFFF5252)) },
                        onClick = { showMenu = false; onDelete() }
                    )
                }
            }
        }

        // Project Info (Name & Duration)
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = project.name,
                color = EliteTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            val sec = (project.totalDurationMs / 1000L).coerceAtLeast(18L)
            Text(
                text = String.format(java.util.Locale.US, "00:%02d · %s", sec, project.exportSettings.resolution.label),
                color = EliteTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun HomeBottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ElitePrimaryLight else EliteTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isSelected) ElitePrimaryLight else EliteTextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
