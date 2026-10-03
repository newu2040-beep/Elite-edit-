package com.example.media.engine

import com.example.media.native.NativeMediaBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs

object WaveformGenerator {
    suspend fun generateWaveform(sampleCount: Int = 40): List<Float> = withContext(Dispatchers.Default) {
        val pcm = ShortArray(1024) { (it % 100).toShort() }
        val nativeResult = NativeMediaBridge.extractWaveform(pcm, sampleCount)
        if (nativeResult != null && nativeResult.isNotEmpty()) {
            nativeResult.toList()
        } else {
            List(sampleCount) { 0.4f }
        }
    }

    suspend fun generateFromFile(file: File, sampleCount: Int = 40): List<Float> = withContext(Dispatchers.IO) {
        if (!file.exists() || file.length() == 0L) {
            return@withContext List(sampleCount) { 0.3f }
        }
        try {
            val bytes = file.readBytes()
            val step = maxOf(1, bytes.size / sampleCount)
            val result = mutableListOf<Float>()
            for (i in 0 until sampleCount) {
                val idx = i * step
                if (idx < bytes.size) {
                    val sample = abs(bytes[idx].toInt()) / 128.0f
                    result.add(sample.coerceIn(0.15f, 0.95f))
                } else {
                    result.add(0.3f)
                }
            }
            result
        } catch (e: Exception) {
            List(sampleCount) { 0.4f }
        }
    }
}
