#include "ColorGradingEngine.h"
#include <algorithm>
#include <cmath>

static inline uint8_t clamp8(float v) {
    if (v < 0.0f) return 0;
    if (v > 255.0f) return 255;
    return static_cast<uint8_t>(v);
}

void ColorGradingEngine::applyAdjustments(
    uint32_t* pixels,
    int width,
    int height,
    const ColorAdjustmentParams& params
) {
    if (!pixels || width <= 0 || height <= 0) return;

    int totalPixels = width * height;
    float exposureFactor = std::pow(2.0f, params.exposure);
    float contrastFactor = 1.0f + params.contrast;
    float brightnessOffset = params.brightness * 128.0f;
    float satFactor = 1.0f + params.saturation;
    float tempR = 1.0f + (params.temperature > 0 ? params.temperature * 0.3f : 0.0f);
    float tempB = 1.0f + (params.temperature < 0 ? -params.temperature * 0.3f : 0.0f);

    float centerX = width * 0.5f;
    float centerY = height * 0.5f;
    float maxDistSq = centerX * centerX + centerY * centerY;

    for (int i = 0; i < totalPixels; ++i) {
        uint32_t p = pixels[i];
        uint8_t a = (p >> 24) & 0xFF;
        uint8_t r = (p >> 16) & 0xFF;
        uint8_t g = (p >> 8) & 0xFF;
        uint8_t b = p & 0xFF;

        float rf = static_cast<float>(r) * tempR;
        float gf = static_cast<float>(g);
        float bf = static_cast<float>(b) * tempB;

        // Exposure & Brightness
        rf = rf * exposureFactor + brightnessOffset;
        gf = gf * exposureFactor + brightnessOffset;
        bf = bf * exposureFactor + brightnessOffset;

        // Contrast
        rf = (rf - 128.0f) * contrastFactor + 128.0f;
        gf = (gf - 128.0f) * contrastFactor + 128.0f;
        bf = (bf - 128.0f) * contrastFactor + 128.0f;

        // Saturation
        float luma = 0.299f * rf + 0.587f * gf + 0.114f * bf;
        rf = luma + (rf - luma) * satFactor;
        gf = luma + (gf - luma) * satFactor;
        bf = luma + (bf - luma) * satFactor;

        // Vignette
        if (params.vignette > 0.01f) {
            int x = i % width;
            int y = i / width;
            float dx = static_cast<float>(x) - centerX;
            float dy = static_cast<float>(y) - centerY;
            float distNorm = (dx * dx + dy * dy) / maxDistSq;
            float vig = 1.0f - (distNorm * params.vignette * 0.7f);
            if (vig < 0.0f) vig = 0.0f;
            rf *= vig;
            gf *= vig;
            bf *= vig;
        }

        pixels[i] = (static_cast<uint32_t>(a) << 24) |
                    (static_cast<uint32_t>(clamp8(rf)) << 16) |
                    (static_cast<uint32_t>(clamp8(gf)) << 8) |
                    static_cast<uint32_t>(clamp8(bf));
    }
}

void ColorGradingEngine::applyCurves(
    uint32_t* pixels,
    int width,
    int height,
    const uint8_t* lutR,
    const uint8_t* lutG,
    const uint8_t* lutB
) {
    if (!pixels || width <= 0 || height <= 0 || !lutR || !lutG || !lutB) return;
    int total = width * height;
    for (int i = 0; i < total; ++i) {
        uint32_t p = pixels[i];
        uint8_t a = (p >> 24) & 0xFF;
        uint8_t r = (p >> 16) & 0xFF;
        uint8_t g = (p >> 8) & 0xFF;
        uint8_t b = p & 0xFF;

        pixels[i] = (static_cast<uint32_t>(a) << 24) |
                    (static_cast<uint32_t>(lutR[r]) << 16) |
                    (static_cast<uint32_t>(lutG[g]) << 8) |
                    static_cast<uint32_t>(lutB[b]);
    }
}
