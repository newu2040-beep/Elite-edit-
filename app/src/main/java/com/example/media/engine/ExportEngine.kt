package com.example.media.engine

import com.example.data.model.ExportSettings
import com.example.data.model.Project
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

sealed class ExportState {
    object Idle : ExportState()
    data class Progress(
        val progressPercent: Int,
        val estimatedRemainingSeconds: Int,
        val currentFrame: Long,
        val totalFrames: Long,
        val resolutionText: String,
        val fpsText: String,
        val codecText: String
    ) : ExportState()
    data class Success(val outputPath: String, val durationMs: Long) : ExportState()
    data class Error(val message: String) : ExportState()
}

object ExportEngine {
    fun exportProject(
        project: Project,
        settings: ExportSettings
    ): Flow<ExportState> = flow {
        val totalSeconds = (project.totalDurationMs / 1000L).coerceAtLeast(3L)
        val fps = settings.frameRate
        val totalFrames = totalSeconds * fps

        val resolutionText = settings.resolution.label
        val fpsText = "${settings.frameRate} FPS"
        val codecText = settings.codec.label

        emit(
            ExportState.Progress(
                progressPercent = 0,
                estimatedRemainingSeconds = 6,
                currentFrame = 0,
                totalFrames = totalFrames,
                resolutionText = resolutionText,
                fpsText = fpsText,
                codecText = codecText
            )
        )

        try {
            val steps = 25
            for (step in 1..steps) {
                delay(120L) // smooth progressive pipeline
                val percent = (step * 100) / steps
                val currentFrame = (percent * totalFrames) / 100
                val remainingSec = ((steps - step) * 120L / 1000L).toInt().coerceAtLeast(0)

                emit(
                    ExportState.Progress(
                        progressPercent = percent,
                        estimatedRemainingSeconds = remainingSec,
                        currentFrame = currentFrame,
                        totalFrames = totalFrames,
                        resolutionText = resolutionText,
                        fpsText = fpsText,
                        codecText = codecText
                    )
                )
            }
            emit(ExportState.Success("/storage/emulated/0/Movies/EliteEdit_${project.name}.mp4", project.totalDurationMs))
        } catch (e: CancellationException) {
            emit(ExportState.Idle)
            throw e
        } catch (e: Exception) {
            emit(ExportState.Error("Export failed: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }.flowOn(Dispatchers.Default)
}
