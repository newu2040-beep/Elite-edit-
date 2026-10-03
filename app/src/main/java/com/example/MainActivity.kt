package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.example.core.permissions.PermissionHelper
import com.example.core.permissions.PermissionsDialog
import com.example.data.database.EliteEditDatabase
import com.example.data.database.ProjectRepository
import com.example.data.model.CanvasAspectRatio
import com.example.data.model.Project
import com.example.ui.editor.EditorScreen
import com.example.ui.editor.EditorViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.MediaImportSheet
import com.example.ui.home.OnboardingScreen
import com.example.ui.home.SettingsScreen
import com.example.ui.home.SplashScreen
import com.example.ui.theme.EliteBackgroundDark
import com.example.ui.theme.EliteEditTheme
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AppDestination {
    object Splash : AppDestination()
    object Onboarding : AppDestination()
    object Home : AppDestination()
    data class Editor(val projectId: String) : AppDestination()
    object ImportMedia : AppDestination()
    object Settings : AppDestination()
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: ProjectRepository
    private lateinit var editorViewModel: EditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = EliteEditDatabase.getInstance(applicationContext)
        repository = ProjectRepository(db.projectDao(), applicationContext)
        editorViewModel = EditorViewModel(repository)

        setContent {
            EliteEditTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = EliteBackgroundDark
                ) {
                    EliteEditApp(
                        repository = repository,
                        editorViewModel = editorViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun EliteEditApp(
    repository: ProjectRepository,
    editorViewModel: EditorViewModel
) {
    var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Splash) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val allProjects by repository.allProjects.collectAsState(initial = emptyList())

    if (showPermissionDialog) {
        Dialog(onDismissRequest = { showPermissionDialog = false }) {
            PermissionsDialog(
                onDismiss = { showPermissionDialog = false },
                onPermissionsHandled = { showPermissionDialog = false }
            )
        }
    }

    AnimatedContent(
        targetState = currentDestination,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "AppNavigation"
    ) { destination ->
        when (destination) {
            is AppDestination.Splash -> {
                SplashScreen(
                    onFinish = {
                        currentDestination = AppDestination.Onboarding
                    }
                )
            }
            is AppDestination.Onboarding -> {
                OnboardingScreen(
                    onStartEditing = {
                        currentDestination = AppDestination.Home
                        showPermissionDialog = true
                    }
                )
            }
            is AppDestination.Home -> {
                HomeScreen(
                    projects = allProjects,
                    onOpenProject = { projectId ->
                        currentDestination = AppDestination.Editor(projectId)
                    },
                    onNewProject = {
                        showPermissionDialog = true
                        currentDestination = AppDestination.ImportMedia
                    },
                    onDuplicateProject = { id ->
                        scope.launch {
                            repository.duplicateProject(id)
                        }
                    },
                    onDeleteProject = { id ->
                        scope.launch {
                            repository.deleteProject(id)
                        }
                    },
                    onOpenSettings = {
                        currentDestination = AppDestination.Settings
                    }
                )
            }
            is AppDestination.Editor -> {
                EditorScreen(
                    projectId = destination.projectId,
                    viewModel = editorViewModel,
                    onNavigateBack = {
                        currentDestination = AppDestination.Home
                    }
                )
            }
            is AppDestination.ImportMedia -> {
                BackHandler { currentDestination = AppDestination.Home }
                MediaImportSheet(
                    onImportMedia = { clips ->
                        val newProj = Project(
                            id = UUID.randomUUID().toString(),
                            name = "Project ${allProjects.size + 1}",
                            aspectRatio = CanvasAspectRatio.RATIO_9_16,
                            clips = clips,
                            previewDrawableResId = clips.firstOrNull()?.thumbnailResId
                        )
                        scope.launch {
                            repository.saveProject(newProj)
                            currentDestination = AppDestination.Editor(newProj.id)
                        }
                    },
                    onClose = {
                        currentDestination = AppDestination.Home
                    }
                )
            }
            is AppDestination.Settings -> {
                BackHandler { currentDestination = AppDestination.Home }
                SettingsScreen(
                    onNavigateBack = {
                        currentDestination = AppDestination.Home
                    }
                )
            }
        }
    }
}
