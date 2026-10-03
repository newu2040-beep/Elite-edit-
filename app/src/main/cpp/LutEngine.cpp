#include "LutEngine.h"
#include <sstream>
#include <algorithm>
#include <cmath>

bool LutEngine::parseCubeLut(
    const std::string& cubeContent,
    int& outSize,
    std::vector<RgbFloat>& outTable
) {
    std::istringstream stream(cubeContent);
    std::string line;
    outSize = 0;
    outTable.clear();

    while (std::getline(stream, line)) {
        if (line.empty() || line[0] == '#') continue;

        if (line.rfind("LUT_3D_SIZE", 0) == 0) {
            std::istringstream ss(line.substr(11));
            ss >> outSize;
            continue;
        }

        std::istringstream valStream(line);
        RgbFloat c;
        if (valStream >> c.r >> c.g >> c.b) {
            outTable.push_back(c);
        }
    }

    if (outSize > 0 && static_cast<int>(outTable.size()) >= (outSize * outSize * outSize)) {
        return true;
    }
    return false;
}

void LutEngine::apply3dLut(
    uint32_t* pixels,
    int width,
    int height,
    int lutSize,
    const std::vector<RgbFloat>& lutTable,
    float intensity
) {
    if (!pixels || width <= 0 || height <= 0 || lutSize <= 1 || lutTable.empty()) return;

    int total = width * height;
    float scale = static_cast<float>(lutSize - 1);
    float inv255 = 1.0f / 255.0f;
    float blend = std::clamp(intensity, 0.0f, 1.0f);

    for (int i = 0; i < total; ++i) {
        uint32_t p = pixels[i];
        uint8_t a = (p >> 24) & 0xFF;
        float r = ((p >> 16) & 0xFF) * inv255;
        float g = ((p >> 8) & 0xFF) * inv255;
        float b = (p & 0xFF) * inv255;

        int ri = std::clamp(static_cast<int>(r * scale), 0, lutSize - 1);
        int gi = std::clamp(static_cast<int>(g * scale), 0, lutSize - 1);
        int bi = std::clamp(static_cast<int>(b * scale), 0, lutSize - 1);

        size_t idx = static_cast<size_t>(ri + gi * lutSize + bi * lutSize * lutSize);
        if (idx < lutTable.size()) {
            const auto& target = lutTable[idx];
            float outR = (r * (1.0f - blend) + target.r * blend) * 255.0f;
            float outG = (g * (1.0f - blend) + target.g * blend) * 255.0f;
            float outB = (b * (1.0f - blend) + target.b * blend) * 255.0f;

            uint8_t finalR = static_cast<uint8_t>(std::clamp(outR, 0.0f, 255.0f));
            uint8_t finalG = static_cast<uint8_t>(std::clamp(outG, 0.0f, 255.0f));
            uint8_t finalB = static_cast<uint8_t>(std::clamp(outB, 0.0f, 255.0f));

            pixels[i] = (static_cast<uint32_t>(a) << 24) |
                        (static_cast<uint32_t>(finalR) << 16) |
                        (static_cast<uint32_t>(finalG) << 8) |
                        static_cast<uint32_t>(finalB);
        }
    }
}
