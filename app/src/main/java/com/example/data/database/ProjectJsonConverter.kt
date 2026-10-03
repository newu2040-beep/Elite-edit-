package com.example.data.database

import com.example.data.model.AudioClip
import com.example.data.model.CanvasAspectRatio
import com.example.data.model.ColorAdjustment
import com.example.data.model.ExportBitrate
import com.example.data.model.ExportCodec
import com.example.data.model.ExportResolution
import com.example.data.model.ExportSettings
import com.example.data.model.Project
import com.example.data.model.StickerLayer
import com.example.data.model.TextAnimationType
import com.example.data.model.TextLayer
import com.example.data.model.Transform
import com.example.data.model.VideoClip
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

object ProjectJsonConverter {

    fun toJson(project: Project): String {
        val root = JSONObject()
        root.put("id", project.id)
        root.put("name", project.name)
        root.put("aspectRatio", project.aspectRatio.name)
        root.put("backgroundColorHex", project.backgroundColorHex)
        root.put("thumbnailUri", project.thumbnailUri ?: "")
        root.put("thumbnailPath", project.thumbnailPath ?: "")
        root.put("createdAt", project.createdAt)
        root.put("modifiedAt", project.modifiedAt)

        // Clips
        val clipsArray = JSONArray()
        project.clips.forEach { clip ->
            val obj = JSONObject()
            obj.put("id", clip.id)
            obj.put("uri", clip.uri)
            obj.put("name", clip.name)
            obj.put("durationMs", clip.durationMs)
            obj.put("trimStartMs", clip.trimStartMs)
            obj.put("trimEndMs", clip.trimEndMs)
            obj.put("speed", clip.speed.toDouble())
            obj.put("volume", clip.volume.toDouble())
            obj.put("isMuted", clip.isMuted)
            obj.put("isVideo", clip.isVideo)
            obj.put("thumbnailPath", clip.thumbnailPath ?: "")

            // Color adjustments
            val adj = clip.colorAdjustment
            val adjObj = JSONObject()
            adjObj.put("exposure", adj.exposure.toDouble())
            adjObj.put("brightness", adj.brightness.toDouble())
            adjObj.put("contrast", adj.contrast.toDouble())
            adjObj.put("highlights", adj.highlights.toDouble())
            adjObj.put("shadows", adj.shadows.toDouble())
            adjObj.put("whites", adj.whites.toDouble())
            adjObj.put("blacks", adj.blacks.toDouble())
            adjObj.put("saturation", adj.saturation.toDouble())
            adjObj.put("temperature", adj.temperature.toDouble())
            adjObj.put("vignette", adj.vignette.toDouble())
            adjObj.put("sharpness", adj.sharpness.toDouble())
            adjObj.put("activePreset", adj.activePreset)
            obj.put("colorAdjustment", adjObj)

            clipsArray.put(obj)
        }
        root.put("clips", clipsArray)

        // Audio Clips
        val audioArray = JSONArray()
        project.audioClips.forEach { audio ->
            val obj = JSONObject()
            obj.put("id", audio.id)
            obj.put("uri", audio.uri)
            obj.put("title", audio.title)
            obj.put("durationMs", audio.durationMs)
            obj.put("startTimelineMs", audio.startTimelineMs)
            obj.put("trimStartMs", audio.trimStartMs)
            obj.put("trimEndMs", audio.trimEndMs)
            obj.put("volume", audio.volume.toDouble())
            obj.put("isMuted", audio.isMuted)
            obj.put("isExtracted", audio.isExtracted)

            val wf = JSONArray()
            audio.waveformAmplitudes.forEach { wf.put(it.toDouble()) }
            obj.put("waveform", wf)

            audioArray.put(obj)
        }
        root.put("audioClips", audioArray)

        // Text Layers
        val textArray = JSONArray()
        project.textLayers.forEach { text ->
            val obj = JSONObject()
            obj.put("id", text.id)
            obj.put("text", text.text)
            obj.put("fontFamilyName", text.fontFamilyName)
            obj.put("fontSizeSp", text.fontSizeSp.toDouble())
            obj.put("textColorHex", text.textColorHex)
            obj.put("isBold", text.isBold)
            obj.put("isItalic", text.isItalic)
            obj.put("posX", text.posX.toDouble())
            obj.put("posY", text.posY.toDouble())
            obj.put("scale", text.scale.toDouble())
            obj.put("rotation", text.rotation.toDouble())
            obj.put("animationType", text.animationType.name)
            textArray.put(obj)
        }
        root.put("textLayers", textArray)

        // Sticker Layers
        val stickerArray = JSONArray()
        project.stickerLayers.forEach { sticker ->
            val obj = JSONObject()
            obj.put("id", sticker.id)
            obj.put("name", sticker.name)
            obj.put("iconKey", sticker.iconKey)
            obj.put("posX", sticker.posX.toDouble())
            obj.put("posY", sticker.posY.toDouble())
            stickerArray.put(obj)
        }
        root.put("stickerLayers", stickerArray)

        return root.toString()
    }

    fun fromJson(jsonStr: String): Project? {
        if (jsonStr.isEmpty()) return null
        return try {
            val root = JSONObject(jsonStr)
            val id = root.optString("id", UUID.randomUUID().toString())
            val name = root.optString("name", "Project")
            val aspectStr = root.optString("aspectRatio", "RATIO_9_16")
            val aspect = try { CanvasAspectRatio.valueOf(aspectStr) } catch (e: Exception) { CanvasAspectRatio.RATIO_9_16 }
            val bg = root.optString("backgroundColorHex", "#000000")
            val thumbPath = root.optString("thumbnailPath", null)

            val clips = mutableListOf<VideoClip>()
            val clipsArray = root.optJSONArray("clips") ?: JSONArray()
            for (i in 0 until clipsArray.length()) {
                val obj = clipsArray.getJSONObject(i)
                val adjObj = obj.optJSONObject("colorAdjustment")
                val adj = if (adjObj != null) {
                    ColorAdjustment(
                        exposure = adjObj.optDouble("exposure", 0.0).toFloat(),
                        brightness = adjObj.optDouble("brightness", 0.0).toFloat(),
                        contrast = adjObj.optDouble("contrast", 0.0).toFloat(),
                        highlights = adjObj.optDouble("highlights", 0.0).toFloat(),
                        shadows = adjObj.optDouble("shadows", 0.0).toFloat(),
                        saturation = adjObj.optDouble("saturation", 0.0).toFloat(),
                        temperature = adjObj.optDouble("temperature", 0.0).toFloat(),
                        vignette = adjObj.optDouble("vignette", 0.0).toFloat(),
                        sharpness = adjObj.optDouble("sharpness", 0.0).toFloat(),
                        activePreset = adjObj.optString("activePreset", "Normal")
                    )
                } else ColorAdjustment()

                clips.add(
                    VideoClip(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        uri = obj.optString("uri", ""),
                        name = obj.optString("name", "Clip"),
                        durationMs = obj.optLong("durationMs", 5000L),
                        trimStartMs = obj.optLong("trimStartMs", 0L),
                        trimEndMs = obj.optLong("trimEndMs", obj.optLong("durationMs", 5000L)),
                        speed = obj.optDouble("speed", 1.0).toFloat(),
                        volume = obj.optDouble("volume", 1.0).toFloat(),
                        isMuted = obj.optBoolean("isMuted", false),
                        isVideo = obj.optBoolean("isVideo", true),
                        thumbnailPath = obj.optString("thumbnailPath", null),
                        colorAdjustment = adj
                    )
                )
            }

            val audioClips = mutableListOf<AudioClip>()
            val audioArray = root.optJSONArray("audioClips") ?: JSONArray()
            for (i in 0 until audioArray.length()) {
                val obj = audioArray.getJSONObject(i)
                val wfArray = obj.optJSONArray("waveform") ?: JSONArray()
                val wf = mutableListOf<Float>()
                for (j in 0 until wfArray.length()) {
                    wf.add(wfArray.optDouble(j, 0.5).toFloat())
                }
                audioClips.add(
                    AudioClip(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        uri = obj.optString("uri", ""),
                        title = obj.optString("title", "Audio"),
                        durationMs = obj.optLong("durationMs", 30000L),
                        startTimelineMs = obj.optLong("startTimelineMs", 0L),
                        trimStartMs = obj.optLong("trimStartMs", 0L),
                        trimEndMs = obj.optLong("trimEndMs", obj.optLong("durationMs", 30000L)),
                        volume = obj.optDouble("volume", 1.0).toFloat(),
                        isMuted = obj.optBoolean("isMuted", false),
                        isExtracted = obj.optBoolean("isExtracted", false),
                        waveformAmplitudes = wf
                    )
                )
            }

            val textLayers = mutableListOf<TextLayer>()
            val textArray = root.optJSONArray("textLayers") ?: JSONArray()
            for (i in 0 until textArray.length()) {
                val obj = textArray.getJSONObject(i)
                val animStr = obj.optString("animationType", "POP")
                val anim = try { TextAnimationType.valueOf(animStr) } catch (e: Exception) { TextAnimationType.POP }
                textLayers.add(
                    TextLayer(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        text = obj.optString("text", "Text"),
                        fontFamilyName = obj.optString("fontFamilyName", "Inter"),
                        fontSizeSp = obj.optDouble("fontSizeSp", 28.0).toFloat(),
                        textColorHex = obj.optString("textColorHex", "#FFFFFF"),
                        isBold = obj.optBoolean("isBold", true),
                        isItalic = obj.optBoolean("isItalic", false),
                        posX = obj.optDouble("posX", 0.5).toFloat(),
                        posY = obj.optDouble("posY", 0.5).toFloat(),
                        animationType = anim
                    )
                )
            }

            val stickerLayers = mutableListOf<StickerLayer>()
            val stickerArray = root.optJSONArray("stickerLayers") ?: JSONArray()
            for (i in 0 until stickerArray.length()) {
                val obj = stickerArray.getJSONObject(i)
                stickerLayers.add(
                    StickerLayer(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        name = obj.optString("name", "Sticker"),
                        iconKey = obj.optString("iconKey", "star"),
                        posX = obj.optDouble("posX", 0.5).toFloat(),
                        posY = obj.optDouble("posY", 0.4).toFloat()
                    )
                )
            }

            Project(
                id = id,
                name = name,
                aspectRatio = aspect,
                backgroundColorHex = bg,
                clips = clips,
                audioClips = audioClips,
                textLayers = textLayers,
                stickerLayers = stickerLayers,
                thumbnailPath = thumbPath,
                createdAt = root.optLong("createdAt", System.currentTimeMillis()),
                modifiedAt = root.optLong("modifiedAt", System.currentTimeMillis())
            )
        } catch (e: Exception) {
            null
        }
    }
}
