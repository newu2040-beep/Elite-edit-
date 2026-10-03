#ifndef AUDIO_ENGINE_H
#define AUDIO_ENGINE_H

#include <vector>
#include <cstdint>

class AudioEngine {
public:
    static std::vector<float> generateWaveformAmplitudes(
        const int16_t* pcmData,
        size_t sampleCount,
        int targetBuckets
    );

    static void applyVolumeAndFades(
        int16_t* pcmData,
        size_t sampleCount,
        int sampleRate,
        float volume,
        int fadeInMs,
        int fadeOutMs
    );
};

#endif // AUDIO_ENGINE_H
