package com.example.media.engine

import com.example.media.native.NativeMediaBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

object WaveformGenerator {
    suspend fun generateWaveform(sampleCount: Int = 40): List<Float> = withContext(Dispatchers.Default) {
        val dummyPcm = ShortArray(1024) { 
            (kotlin.math.sin(it * 0.1) * 20000 + Random.nextInt(-4000, 4000)).toInt().toShort() 
        }
        val nativeResult = NativeMediaBridge.extractWaveform(dummyPcm, sampleCount)
        if (nativeResult != null && nativeResult.isNotEmpty()) {
            nativeResult.toList()
        } else {
            // Smooth waveform generator
            val list = mutableListOf<Float>()
            var prev = 0.5f
            for (i in 0 until sampleCount) {
                val next = (prev + (Random.nextFloat() - 0.5f) * 0.4f).coerceIn(0.15f, 0.95f)
                list.add(next)
                prev = next
            }
            list
        }
    }
}
