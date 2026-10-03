#include "AudioEngine.h"
#include <cmath>
#include <algorithm>

std::vector<float> AudioEngine::generateWaveformAmplitudes(
    const int16_t* pcmData,
    size_t sampleCount,
    int targetBuckets
) {
    std::vector<float> waveform;
    if (!pcmData || sampleCount == 0 || targetBuckets <= 0) {
        return waveform;
    }
    waveform.resize(targetBuckets, 0.0f);
    size_t bucketSize = std::max<size_t>(1, sampleCount / targetBuckets);

    for (int b = 0; b < targetBuckets; ++b) {
        size_t start = b * bucketSize;
        size_t end = std::min(start + bucketSize, sampleCount);
        float peak = 0.0f;
        for (size_t i = start; i < end; ++i) {
            float val = std::abs(static_cast<float>(pcmData[i])) / 32768.0f;
            if (val > peak) peak = val;
        }
        waveform[b] = peak;
    }
    return waveform;
}

void AudioEngine::applyVolumeAndFades(
    int16_t* pcmData,
    size_t sampleCount,
    int sampleRate,
    float volume,
    int fadeInMs,
    int fadeOutMs
) {
    if (!pcmData || sampleCount == 0 || sampleRate <= 0) return;

    size_t fadeInSamples = (static_cast<size_t>(fadeInMs) * sampleRate) / 1000;
    size_t fadeOutSamples = (static_cast<size_t>(fadeOutMs) * sampleRate) / 1000;

    for (size_t i = 0; i < sampleCount; ++i) {
        float gain = volume;
        if (i < fadeInSamples && fadeInSamples > 0) {
            gain *= (static_cast<float>(i) / static_cast<float>(fadeInSamples));
        }
        if (sampleCount - i < fadeOutSamples && fadeOutSamples > 0) {
            gain *= (static_cast<float>(sampleCount - i) / static_cast<float>(fadeOutSamples));
        }
        float adjusted = static_cast<float>(pcmData[i]) * gain;
        pcmData[i] = static_cast<int16_t>(std::clamp(adjusted, -32768.0f, 32767.0f));
    }
}
