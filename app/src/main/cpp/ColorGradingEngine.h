#ifndef COLOR_GRADING_ENGINE_H
#define COLOR_GRADING_ENGINE_H

#include <cstdint>
#include <vector>

struct ColorAdjustmentParams {
    float exposure;    // -1.0 to 1.0
    float brightness;  // -1.0 to 1.0
    float contrast;    // -1.0 to 1.0
    float highlights;  // -1.0 to 1.0
    float shadows;     // -1.0 to 1.0
    float whites;      // -1.0 to 1.0
    float blacks;      // -1.0 to 1.0
    float saturation;  // -1.0 to 1.0
    float vibrance;    // -1.0 to 1.0
    float temperature; // -1.0 to 1.0
    float tint;        // -1.0 to 1.0
    float vignette;    // 0.0 to 1.0
    float sharpness;   // 0.0 to 1.0
};

class ColorGradingEngine {
public:
    static void applyAdjustments(
        uint32_t* pixels,
        int width,
        int height,
        const ColorAdjustmentParams& params
    );

    static void applyCurves(
        uint32_t* pixels,
        int width,
        int height,
        const uint8_t* lutR,
        const uint8_t* lutG,
        const uint8_t* lutB
    );
};

#endif // COLOR_GRADING_ENGINE_H
