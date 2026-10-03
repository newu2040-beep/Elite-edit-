package com.example.data.database

import android.content.Context
import com.example.R
import com.example.data.model.AudioClip
import com.example.data.model.CanvasAspectRatio
import com.example.data.model.ColorAdjustment
import com.example.data.model.Project
import com.example.data.model.TextLayer
import com.example.data.model.VideoClip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val context: Context
) {
    private val memoryProjects = mutableMapOf<String, Project>()

    init {
        // Pre-populate with default projects if empty
        CoroutineScope(Dispatchers.IO).launch {
            val initial = getInitialSampleProjects()
            initial.forEach { project ->
                memoryProjects[project.id] = project
                projectDao.insertProject(project.toEntity())
            }
        }
    }

    val allProjects: Flow<List<Project>> = projectDao.getAllProjects().map { entities ->
        if (entities.isEmpty()) {
            getInitialSampleProjects()
        } else {
            entities.map { entity ->
                memoryProjects[entity.id] ?: entity.toProject()
            }
        }
    }

    suspend fun getProject(id: String): Project? {
        memoryProjects[id]?.let { return it }
        val entity = projectDao.getProjectById(id)
        val project = entity?.toProject()
        if (project != null) {
            memoryProjects[project.id] = project
        }
        return project
    }

    suspend fun saveProject(project: Project) {
        val updated = project.copy(modifiedAt = System.currentTimeMillis())
        memoryProjects[updated.id] = updated
        projectDao.insertProject(updated.toEntity())
    }

    suspend fun deleteProject(id: String) {
        memoryProjects.remove(id)
        projectDao.deleteProjectById(id)
    }

    suspend fun duplicateProject(id: String): Project? {
        val original = getProject(id) ?: return null
        val copy = original.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${original.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        saveProject(copy)
        return copy
    }

    private fun getInitialSampleProjects(): List<Project> {
        val cityVibesClip = VideoClip(
            name = "City Vibes Sunset",
            uri = "android.resource://${context.packageName}/${R.drawable.img_city_vibes}",
            durationMs = 24000L,
            thumbnailResId = R.drawable.img_city_vibes,
            colorAdjustment = ColorAdjustment(exposure = 0.20f, contrast = 0.12f, highlights = -0.10f, saturation = 0.06f)
        )
        val cityAudio = AudioClip(
            title = "Chill Vibes",
            uri = "asset:///audio/chill_vibes.mp3",
            durationMs = 24000L,
            waveformAmplitudes = listOf(0.2f, 0.4f, 0.6f, 0.9f, 0.7f, 0.5f, 0.8f, 1.0f, 0.6f, 0.4f, 0.7f, 0.5f, 0.8f, 0.3f, 0.6f, 0.9f, 0.5f, 0.7f, 0.4f, 0.2f)
        )
        val cityText = TextLayer(
            text = "Good Vibes",
            fontFamilyName = "Inter",
            fontSizeSp = 32f,
            posY = 0.45f
        )

        val coastalClip = VideoClip(
            name = "Coastal Glow",
            uri = "android.resource://${context.packageName}/${R.drawable.img_coastal_sunset}",
            durationMs = 45000L,
            thumbnailResId = R.drawable.img_coastal_sunset
        )

        val mountainClip = VideoClip(
            name = "Alpine Lake Morning",
            uri = "android.resource://${context.packageName}/${R.drawable.img_mountain_lake}",
            durationMs = 32000L,
            thumbnailResId = R.drawable.img_mountain_lake
        )

        return listOf(
            Project(
                id = "proj_city_vibes",
                name = "City Vibes",
                aspectRatio = CanvasAspectRatio.RATIO_9_16,
                clips = listOf(cityVibesClip),
                audioClips = listOf(cityAudio),
                textLayers = listOf(cityText),
                previewDrawableResId = R.drawable.img_city_vibes
            ),
            Project(
                id = "proj_morning",
                name = "Morning",
                aspectRatio = CanvasAspectRatio.RATIO_9_16,
                clips = listOf(mountainClip),
                previewDrawableResId = R.drawable.img_mountain_lake
            ),
            Project(
                id = "proj_nature",
                name = "Nature",
                aspectRatio = CanvasAspectRatio.RATIO_9_16,
                clips = listOf(mountainClip),
                previewDrawableResId = R.drawable.img_mountain_lake
            ),
            Project(
                id = "proj_travel",
                name = "Travel",
                aspectRatio = CanvasAspectRatio.RATIO_9_16,
                clips = listOf(coastalClip),
                previewDrawableResId = R.drawable.img_coastal_sunset
            )
        )
    }

    private fun Project.toEntity(): ProjectEntity {
        return ProjectEntity(
            id = id,
            name = name,
            aspectRatio = aspectRatio.label,
            backgroundColorHex = backgroundColorHex,
            durationMs = totalDurationMs,
            thumbnailUri = thumbnailUri,
            previewDrawableResName = previewDrawableResId?.let { "res_$it" },
            projectJson = "",
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }

    private fun ProjectEntity.toProject(): Project {
        val aspect = CanvasAspectRatio.values().firstOrNull { it.label == aspectRatio } ?: CanvasAspectRatio.RATIO_9_16
        return Project(
            id = id,
            name = name,
            aspectRatio = aspect,
            backgroundColorHex = backgroundColorHex,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
}
