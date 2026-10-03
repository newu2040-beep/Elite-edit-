package com.example.media.native

import android.graphics.Bitmap
import android.util.Log

object NativeMediaBridge {
    private const val TAG = "NativeMediaBridge"
    private var isNativeLoaded = false

    init {
        try {
            System.loadLibrary("eliteeditnative")
            isNativeLoaded = true
            Log.i(TAG, "Native C++ engine successfully loaded: ${nativeGetEngineVersion()}")
        } catch (e: Throwable) {
            isNativeLoaded = false
            Log.w(TAG, "Native library not available, using high-performance software pipeline: ${e.message}")
        }
    }

    val isAvailable: Boolean get() = isNativeLoaded

    fun processBitmapAdjustments(
        bitmap: Bitmap,
        exposure: Float,
        brightness: Float,
        contrast: Float,
        highlights: Float,
        shadows: Float,
        whites: Float,
        blacks: Float,
        saturation: Float,
        vibrance: Float,
        temperature: Float,
        tint: Float,
        vignette: Float,
        sharpness: Float
    ): Boolean {
        if (isNativeLoaded) {
            try {
                return nativeProcessBitmapAdjustments(
                    bitmap, exposure, brightness, contrast, highlights, shadows,
                    whites, blacks, saturation, vibrance, temperature, tint, vignette, sharpness
                )
            } catch (e: Throwable) {
                Log.e(TAG, "Native error during adjustments: ${e.message}")
            }
        }
        // Fallback: Software adjustments can be done via ColorMatrix / Canvas
        return false
    }

    fun extractWaveform(pcmSamples: ShortArray, targetBuckets: Int): FloatArray? {
        if (isNativeLoaded) {
            try {
                return nativeExtractWaveform(pcmSamples, targetBuckets)
            } catch (e: Throwable) {
                Log.e(TAG, "Native waveform extraction error: ${e.message}")
            }
        }
        // Software fallback
        if (pcmSamples.isEmpty() || targetBuckets <= 0) return FloatArray(0)
        val result = FloatArray(targetBuckets)
        val bucketSize = maxOf(1, pcmSamples.size / targetBuckets)
        for (b in 0 until targetBuckets) {
            val start = b * bucketSize
            val end = minOf(start + bucketSize, pcmSamples.size)
            var peak = 0.0f
            for (i in start until end) {
                val amp = kotlin.math.abs(pcmSamples[i].toInt()) / 32768.0f
                if (amp > peak) peak = amp
            }
            result[b] = peak
        }
        return result
    }

    private external fun nativeProcessBitmapAdjustments(
        bitmap: Bitmap,
        exposure: Float,
        brightness: Float,
        contrast: Float,
        highlights: Float,
        shadows: Float,
        whites: Float,
        blacks: Float,
        saturation: Float,
        vibrance: Float,
        temperature: Float,
        tint: Float,
        vignette: Float,
        sharpness: Float
    ): Boolean

    private external fun nativeExtractWaveform(
        pcmSamples: ShortArray,
        targetBuckets: Int
    ): FloatArray?

    private external fun nativeGetEngineVersion(): String
}
