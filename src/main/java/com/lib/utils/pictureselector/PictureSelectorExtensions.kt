package com.lib.utils.pictureselector

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * PictureSelector 扩展函数
 * 提供更便捷的使用方式
 */

/**
 * FragmentActivity 扩展 - 选择图片
 */
fun FragmentActivity.selectImage(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.selectImage(this)
        .config(config)
        .start()
}

/**
 * Fragment 扩展 - 选择图片
 */
fun Fragment.selectImage(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.selectImage(this)
        .config(config)
        .start()
}

/**
 * FragmentActivity 扩展 - 选择视频
 */
fun FragmentActivity.selectVideo(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.selectVideo(this)
        .config(config)
        .start()
}

/**
 * Fragment 扩展 - 选择视频
 */
fun Fragment.selectVideo(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.selectVideo(this)
        .config(config)
        .start()
}

/**
 * FragmentActivity 扩展 - 选择图片和视频
 */
fun FragmentActivity.selectMedia(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.selectMedia(this)
        .config(config)
        .start()
}

/**
 * Fragment 扩展 - 选择图片和视频
 */
fun Fragment.selectMedia(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.selectMedia(this)
        .config(config)
        .start()
}

/**
 * FragmentActivity 扩展 - 拍照
 */
fun FragmentActivity.takePhoto(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.takePhoto(this)
        .config(config)
        .startCamera()
}

/**
 * Fragment 扩展 - 拍照
 */
fun Fragment.takePhoto(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.takePhoto(this)
        .config(config)
        .startCamera()
}

/**
 * FragmentActivity 扩展 - 录制视频
 */
fun FragmentActivity.recordVideo(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.recordVideo(this)
        .config(config)
        .startCamera()
}

/**
 * Fragment 扩展 - 录制视频
 */
fun Fragment.recordVideo(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this }
): Flow<PictureSelectorResult> {
    return PictureSelectorHelper.recordVideo(this)
        .config(config)
        .startCamera()
}

/**
 * 便捷方法 - 在 Activity 中使用
 * 
 * 使用示例：
 * ```
 * launchSelectImage { result ->
 *     if (result.isSuccess) {
 *         // 处理图片
 *     }
 * }
 * ```
 */
fun FragmentActivity.launchSelectImage(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this },
    onResult: (PictureSelectorResult) -> Unit
) {
    lifecycleScope.launch {
        selectImage(config).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 在 Fragment 中使用
 */
fun Fragment.launchSelectImage(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this },
    onResult: (PictureSelectorResult) -> Unit
) {
    lifecycleScope.launch {
        selectImage(config).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 选择视频
 */
fun FragmentActivity.launchSelectVideo(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this },
    onResult: (PictureSelectorResult) -> Unit
) {
    lifecycleScope.launch {
        selectVideo(config).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 选择视频
 */
fun Fragment.launchSelectVideo(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this },
    onResult: (PictureSelectorResult) -> Unit
) {
    lifecycleScope.launch {
        selectVideo(config).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 拍照
 */
fun FragmentActivity.launchTakePhoto(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this },
    onResult: (PictureSelectorResult) -> Unit
) {
    lifecycleScope.launch {
        takePhoto(config).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 拍照
 */
fun Fragment.launchTakePhoto(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this },
    onResult: (PictureSelectorResult) -> Unit
) {
    lifecycleScope.launch {
        takePhoto(config).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 录制视频
 */
fun FragmentActivity.launchRecordVideo(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this },
    onResult: (PictureSelectorResult) -> Unit
) {
    lifecycleScope.launch {
        recordVideo(config).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 录制视频
 */
fun Fragment.launchRecordVideo(
    config: PictureSelectorConfig.() -> PictureSelectorConfig = { this },
    onResult: (PictureSelectorResult) -> Unit
) {
    lifecycleScope.launch {
        recordVideo(config).collect { result ->
            onResult(result)
        }
    }
}
