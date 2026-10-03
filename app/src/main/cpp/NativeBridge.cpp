#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include "ColorGradingEngine.h"
#include "AudioEngine.h"
#include "LutEngine.h"

#define TAG "EliteEditNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_example_media_native_NativeMediaBridge_nativeProcessBitmapAdjustments(
    JNIEnv* env,
    jobject /* thiz */,
    jobject bitmap,
    jfloat exposure,
    jfloat brightness,
    jfloat contrast,
    jfloat highlights,
    jfloat shadows,
    jfloat whites,
    jfloat blacks,
    jfloat saturation,
    jfloat vibrance,
    jfloat temperature,
    jfloat tint,
    jfloat vignette,
    jfloat sharpness
) {
    AndroidBitmapInfo info;
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        LOGE("Failed to get bitmap info");
        return JNI_FALSE;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format must be RGBA_8888");
        return JNI_FALSE;
    }

    void* pixels = nullptr;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
        LOGE("Failed to lock pixels");
        return JNI_FALSE;
    }

    ColorAdjustmentParams params {
        exposure, brightness, contrast, highlights, shadows,
        whites, blacks, saturation, vibrance, temperature,
        tint, vignette, sharpness
    };

    ColorGradingEngine::applyAdjustments(
        reinterpret_cast<uint32_t*>(pixels),
        info.width,
        info.height,
        params
    );

    AndroidBitmap_unlockPixels(env, bitmap);
    return JNI_TRUE;
}

JNIEXPORT jfloatArray JNICALL
Java_com_example_media_native_NativeMediaBridge_nativeExtractWaveform(
    JNIEnv* env,
    jobject /* thiz */,
    jshortArray pcmSamples,
    jint targetBuckets
) {
    if (!pcmSamples || targetBuckets <= 0) {
        return nullptr;
    }
    jsize len = env->GetArrayLength(pcmSamples);
    jshort* body = env->GetShortArrayElements(pcmSamples, nullptr);
    if (!body) return nullptr;

    std::vector<float> amps = AudioEngine::generateWaveformAmplitudes(
        reinterpret_cast<const int16_t*>(body),
        static_cast<size_t>(len),
        targetBuckets
    );
    env->ReleaseShortArrayElements(pcmSamples, body, JNI_ABORT);

    jfloatArray result = env->NewFloatArray(amps.size());
    if (result) {
        env->SetFloatArrayRegion(result, 0, amps.size(), amps.data());
    }
    return result;
}

JNIEXPORT jstring JNICALL
Java_com_example_media_native_NativeMediaBridge_nativeGetEngineVersion(
    JNIEnv* env,
    jobject /* thiz */
) {
    return env->NewStringUTF("EliteEdit-NDK-Core v2.4 (Rahul Shah)");
}

} // extern "C"
