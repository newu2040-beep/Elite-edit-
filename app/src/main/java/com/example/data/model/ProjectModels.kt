package com.example.data.model

import java.util.UUID

enum class CanvasAspectRatio(val label: String, val ratio: Float, val widthRatio: Int, val heightRatio: Int) {
    RATIO_9_16("9:16", 9f / 16f, 9, 16),
    RATIO_16_9("16:9", 16f / 9f, 16, 9),
    RATIO_4_5("4:5", 4f / 5f, 4, 5),
    RATIO_1_1("1:1", 1f, 1, 1),
    RATIO_4_3("4:3", 4f / 3f, 4, 3),
    CUSTOM("Custom", 9f / 16f, 9, 16)
}

enum class FitMode {
    FIT, FILL, CENTER
}

data class Transform(
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f,
    val translationX: Float = 0.0f,
    val translationY: Float = 0.0f,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val fitMode: FitMode = FitMode.FIT
)

data class ColorAdjustment(
    val exposure: Float = 0.0f,      // -1.0 to 1.0 (display as +0.20)
    val brightness: Float = 0.0f,    // -1.0 to 1.0
    val contrast: Float = 0.0f,      // -1.0 to 1.0 (display as +12)
    val highlights: Float = 0.0f,    // -1.0 to 1.0 (display as -10)
    val shadows: Float = 0.0f,       // -1.0 to 1.0 (display as +8)
    val whites: Float = 0.0f,        // -1.0 to 1.0
    val blacks: Float = 0.0f,        // -1.0 to 1.0
    val saturation: Float = 0.0f,    // -1.0 to 1.0 (display as +6)
    val vibrance: Float = 0.0f,      // -1.0 to 1.0
    val temperature: Float = 0.0f,   // -1.0 to 1.0
    val tint: Float = 0.0f,          // -1.0 to 1.0
    val fade: Float = 0.0f,          // 0.0 to 1.0
    val vignette: Float = 0.0f,      // 0.0 to 1.0
    val sharpness: Float = 0.0f,     // 0.0 to 1.0
    val activePreset: String = "Normal",
    val curveRedPoints: List<Pair<Float, Float>> = listOf(0f to 0f, 1f to 1f),
    val curveGreenPoints: List<Pair<Float, Float>> = listOf(0f to 0f, 1f to 1f),
    val curveBluePoints: List<Pair<Float, Float>> = listOf(0f to 0f, 1f to 1f),
    val curveRgbPoints: List<Pair<Float, Float>> = listOf(0f to 0f, 1f to 1f),
    val hslValues: Map<String, HslChannel> = defaultHslChannels(),
    val lutName: String = "None",
    val lutIntensity: Float = 1.0f
)

data class HslChannel(
    val hue: Float = 0f,        // -180 to 180
    val saturation: Float = 0f, // -100 to 100
    val luminance: Float = 0f   // -100 to 100
)

fun defaultHslChannels(): Map<String, HslChannel> = mapOf(
    "Red" to HslChannel(),
    "Orange" to HslChannel(),
    "Yellow" to HslChannel(),
    "Green" to HslChannel(),
    "Cyan" to HslChannel(),
    "Blue" to HslChannel(),
    "Purple" to HslChannel(),
    "Magenta" to HslChannel()
)

data class VideoClip(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val name: String,
    val durationMs: Long,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = durationMs,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val isVideo: Boolean = true,
    val colorAdjustment: ColorAdjustment = ColorAdjustment(),
    val transform: Transform = Transform(),
    val thumbnailResId: Int? = null,
    val thumbnailPath: String? = null
) {
    val effectiveDurationMs: Long
        get() = ((trimEndMs - trimStartMs).coerceAtLeast(100L) / speed).toLong()
}

data class AudioClip(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val title: String,
    val durationMs: Long,
    val startTimelineMs: Long = 0L,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = durationMs,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val isExtracted: Boolean = false,
    val fadeInMs: Int = 0,
    val fadeOutMs: Int = 0,
    val waveformAmplitudes: List<Float> = emptyList()
) {
    val effectiveDurationMs: Long
        get() = (trimEndMs - trimStartMs).coerceAtLeast(100L)
}

enum class TextAnimationType(val label: String) {
    NONE("None"),
    FADE("Fade"),
    POP("Pop"),
    SLIDE("Slide"),
    TYPEWRITER("Typewriter"),
    SCALE("Scale")
}

data class TextLayer(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "Good Vibes",
    val fontFamilyName: String = "Inter",
    val fontSizeSp: Float = 28f,
    val textColorHex: String = "#FFFFFF",
    val opacity: Float = 1.0f,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val strokeColorHex: String? = "#000000",
    val strokeWidthDp: Float = 1.5f,
    val shadowColorHex: String? = "#40000000",
    val backgroundColorHex: String? = null,
    val posX: Float = 0.5f, // Normalized 0..1 (center)
    val posY: Float = 0.5f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val animationType: TextAnimationType = TextAnimationType.POP,
    val startTimelineMs: Long = 0L,
    val endTimelineMs: Long = 10000L
)

data class StickerLayer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconKey: String,
    val posX: Float = 0.5f,
    val posY: Float = 0.4f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val startTimelineMs: Long = 0L,
    val endTimelineMs: Long = 10000L
)

enum class ExportResolution(val label: String, val width: Int, val height: Int) {
    RES_480P("480p", 480, 854),
    RES_720P("720p", 720, 1280),
    RES_1080P("1080p", 1080, 1920),
    RES_2K("2K", 1440, 2560),
    RES_4K("4K", 2160, 3840)
}

enum class ExportBitrate(val label: String, val mbps: Float) {
    LOW("Low", 8f),
    MEDIUM("Medium", 16f),
    HIGH("High", 28f),
    CUSTOM("Custom", 20f)
}

enum class ExportCodec(val label: String) {
    H264("H.264"),
    H265("H.265 (HEVC)")
}

data class ExportSettings(
    val resolution: ExportResolution = ExportResolution.RES_1080P,
    val frameRate: Int = 30, // 24, 25, 30, 50, 60
    val bitrateMode: ExportBitrate = ExportBitrate.HIGH,
    val customBitrateMbps: Float = 24.0f,
    val codec: ExportCodec = ExportCodec.H264,
    val highQuality: Boolean = true
)

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "New Project",
    val aspectRatio: CanvasAspectRatio = CanvasAspectRatio.RATIO_9_16,
    val backgroundColorHex: String = "#000000",
    val clips: List<VideoClip> = emptyList(),
    val audioClips: List<AudioClip> = emptyList(),
    val textLayers: List<TextLayer> = emptyList(),
    val stickerLayers: List<StickerLayer> = emptyList(),
    val exportSettings: ExportSettings = ExportSettings(),
    val thumbnailUri: String? = null,
    val thumbnailPath: String? = null,
    val previewDrawableResId: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
) {
    val totalDurationMs: Long
        get() {
            val videoDuration = clips.sumOf { it.effectiveDurationMs }
            val audioDuration = audioClips.maxOfOrNull { it.startTimelineMs + it.effectiveDurationMs } ?: 0L
            return maxOf(videoDuration, audioDuration, 1000L)
        }

    fun getActiveClipInfo(positionMs: Long): ActiveClipInfo? {
        if (clips.isEmpty()) return null
        var acc = 0L
        for (i in clips.indices) {
            val clip = clips[i]
            val dur = clip.effectiveDurationMs
            if (positionMs in acc until (acc + dur)) {
                return ActiveClipInfo(i, clip, acc, positionMs - acc)
            }
            acc += dur
        }
        val lastIdx = clips.size - 1
        val lastClip = clips[lastIdx]
        val lastStart = (acc - lastClip.effectiveDurationMs).coerceAtLeast(0L)
        return ActiveClipInfo(lastIdx, lastClip, lastStart, (positionMs - lastStart).coerceAtLeast(0L))
    }
}

data class ActiveClipInfo(
    val clipIndex: Int,
    val clip: VideoClip,
    val clipStartInProjectMs: Long,
    val localOffsetMs: Long
)
