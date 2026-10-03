package com.example.domain.command

import com.example.data.model.AudioClip
import com.example.data.model.ColorAdjustment
import com.example.data.model.Project
import com.example.data.model.StickerLayer
import com.example.data.model.TextLayer
import com.example.data.model.VideoClip

data class AddClipCommand(val clip: VideoClip) : EditorCommand {
    override val description = "Add Clip"
    override fun execute(current: Project): Project = current.copy(clips = current.clips + clip)
    override fun undo(current: Project): Project = current.copy(clips = current.clips.filterNot { it.id == clip.id })
}

data class DeleteClipCommand(val clipIndex: Int, val clip: VideoClip) : EditorCommand {
    override val description = "Delete Clip"
    override fun execute(current: Project): Project {
        val updated = current.clips.toMutableList()
        if (clipIndex in updated.indices) {
            updated.removeAt(clipIndex)
        }
        return current.copy(clips = updated)
    }
    override fun undo(current: Project): Project {
        val updated = current.clips.toMutableList()
        val insertAt = clipIndex.coerceIn(0, updated.size)
        updated.add(insertAt, clip)
        return current.copy(clips = updated)
    }
}

data class SplitClipCommand(
    val clipIndex: Int,
    val originalClip: VideoClip,
    val splitTimeWithinClipMs: Long
) : EditorCommand {
    override val description = "Split Clip"
    private val firstPart: VideoClip
    private val secondPart: VideoClip

    init {
        val splitPoint = originalClip.trimStartMs + splitTimeWithinClipMs
        firstPart = originalClip.copy(
            id = java.util.UUID.randomUUID().toString(),
            trimEndMs = splitPoint
        )
        secondPart = originalClip.copy(
            id = java.util.UUID.randomUUID().toString(),
            trimStartMs = splitPoint
        )
    }

    override fun execute(current: Project): Project {
        val updated = current.clips.toMutableList()
        if (clipIndex in updated.indices) {
            updated.removeAt(clipIndex)
            updated.add(clipIndex, secondPart)
            updated.add(clipIndex, firstPart)
        }
        return current.copy(clips = updated)
    }

    override fun undo(current: Project): Project {
        val updated = current.clips.toMutableList()
        val idx = updated.indexOfFirst { it.id == firstPart.id }
        if (idx != -1 && idx + 1 < updated.size && updated[idx + 1].id == secondPart.id) {
            updated.removeAt(idx + 1)
            updated.removeAt(idx)
            updated.add(idx, originalClip)
        }
        return current.copy(clips = updated)
    }
}

data class TrimClipCommand(
    val clipId: String,
    val oldTrimStartMs: Long,
    val oldTrimEndMs: Long,
    val newTrimStartMs: Long,
    val newTrimEndMs: Long
) : EditorCommand {
    override val description = "Trim Clip"
    override fun execute(current: Project): Project = current.copy(
        clips = current.clips.map {
            if (it.id == clipId) it.copy(trimStartMs = newTrimStartMs, trimEndMs = newTrimEndMs) else it
        }
    )
    override fun undo(current: Project): Project = current.copy(
        clips = current.clips.map {
            if (it.id == clipId) it.copy(trimStartMs = oldTrimStartMs, trimEndMs = oldTrimEndMs) else it
        }
    )
}

data class DuplicateClipCommand(val clip: VideoClip) : EditorCommand {
    override val description = "Duplicate Clip"
    private val duplicate = clip.copy(id = java.util.UUID.randomUUID().toString(), name = "${clip.name} (Copy)")

    override fun execute(current: Project): Project {
        val idx = current.clips.indexOfFirst { it.id == clip.id }
        val updated = current.clips.toMutableList()
        if (idx != -1) {
            updated.add(idx + 1, duplicate)
        } else {
            updated.add(duplicate)
        }
        return current.copy(clips = updated)
    }

    override fun undo(current: Project): Project = current.copy(
        clips = current.clips.filterNot { it.id == duplicate.id }
    )
}

data class AddTextCommand(val textLayer: TextLayer) : EditorCommand {
    override val description = "Add Text"
    override fun execute(current: Project): Project = current.copy(textLayers = current.textLayers + textLayer)
    override fun undo(current: Project): Project = current.copy(textLayers = current.textLayers.filterNot { it.id == textLayer.id })
}

data class UpdateTextCommand(val oldText: TextLayer, val newText: TextLayer) : EditorCommand {
    override val description = "Edit Text"
    override fun execute(current: Project): Project = current.copy(
        textLayers = current.textLayers.map { if (it.id == newText.id) newText else it }
    )
    override fun undo(current: Project): Project = current.copy(
        textLayers = current.textLayers.map { if (it.id == oldText.id) oldText else it }
    )
}

data class DeleteTextCommand(val textLayer: TextLayer) : EditorCommand {
    override val description = "Delete Text"
    override fun execute(current: Project): Project = current.copy(
        textLayers = current.textLayers.filterNot { it.id == textLayer.id }
    )
    override fun undo(current: Project): Project = current.copy(
        textLayers = current.textLayers + textLayer
    )
}

data class AddStickerCommand(val sticker: StickerLayer) : EditorCommand {
    override val description = "Add Sticker"
    override fun execute(current: Project): Project = current.copy(stickerLayers = current.stickerLayers + sticker)
    override fun undo(current: Project): Project = current.copy(stickerLayers = current.stickerLayers.filterNot { it.id == sticker.id })
}

data class DeleteStickerCommand(val sticker: StickerLayer) : EditorCommand {
    override val description = "Delete Sticker"
    override fun execute(current: Project): Project = current.copy(
        stickerLayers = current.stickerLayers.filterNot { it.id == sticker.id }
    )
    override fun undo(current: Project): Project = current.copy(
        stickerLayers = current.stickerLayers + sticker
    )
}

data class UpdateColorAdjustmentCommand(
    val clipId: String,
    val oldAdjustment: ColorAdjustment,
    val newAdjustment: ColorAdjustment
) : EditorCommand {
    override val description = "Color Adjustment"
    override fun execute(current: Project): Project = current.copy(
        clips = current.clips.map { if (it.id == clipId) it.copy(colorAdjustment = newAdjustment) else it }
    )
    override fun undo(current: Project): Project = current.copy(
        clips = current.clips.map { if (it.id == clipId) it.copy(colorAdjustment = oldAdjustment) else it }
    )
}

data class AddAudioCommand(val audioClip: AudioClip) : EditorCommand {
    override val description = "Add Audio"
    override fun execute(current: Project): Project = current.copy(audioClips = current.audioClips + audioClip)
    override fun undo(current: Project): Project = current.copy(audioClips = current.audioClips.filterNot { it.id == audioClip.id })
}

data class DeleteAudioCommand(val audioClip: AudioClip) : EditorCommand {
    override val description = "Delete Audio"
    override fun execute(current: Project): Project = current.copy(
        audioClips = current.audioClips.filterNot { it.id == audioClip.id }
    )
    override fun undo(current: Project): Project = current.copy(
        audioClips = current.audioClips + audioClip
    )
}
