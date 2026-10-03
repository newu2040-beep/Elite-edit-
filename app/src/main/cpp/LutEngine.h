#ifndef LUT_ENGINE_H
#define LUT_ENGINE_H

#include <cstdint>
#include <vector>
#include <string>

struct RgbFloat {
    float r, g, b;
};

class LutEngine {
public:
    static bool parseCubeLut(
        const std::string& cubeContent,
        int& outSize,
        std::vector<RgbFloat>& outTable
    );

    static void apply3dLut(
        uint32_t* pixels,
        int width,
        int height,
        int lutSize,
        const std::vector<RgbFloat>& lutTable,
        float intensity
    );
};

#endif // LUT_ENGINE_H
