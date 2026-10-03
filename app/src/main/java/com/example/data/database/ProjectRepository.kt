package com.example.data.database

import android.content.Context
import com.example.data.model.CanvasAspectRatio
import com.example.data.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val context: Context
) {
    private val memoryProjects = mutableMapOf<String, Project>()

    val allProjects: Flow<List<Project>> = projectDao.getAllProjects().map { entities ->
        entities.map { entity ->
            memoryProjects[entity.id] ?: entity.toProject()
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

    private fun Project.toEntity(): ProjectEntity {
        return ProjectEntity(
            id = id,
            name = name,
            aspectRatio = aspectRatio.name,
            backgroundColorHex = backgroundColorHex,
            durationMs = totalDurationMs,
            thumbnailUri = thumbnailUri,
            previewDrawableResName = null,
            projectJson = ProjectJsonConverter.toJson(this),
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }

    private fun ProjectEntity.toProject(): Project {
        val parsed = ProjectJsonConverter.fromJson(projectJson)
        if (parsed != null) return parsed

        val aspect = try {
            CanvasAspectRatio.valueOf(aspectRatio)
        } catch (e: Exception) {
            CanvasAspectRatio.RATIO_9_16
        }
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
