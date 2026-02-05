package com.lib.utils.pictureselector

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

/**
 * PictureSelector 3.0 Kotlin 封装
 * 使用 Flow 实现异步回调
 * 
 * 使用示例：
 * ```
 * // 在 Activity 或 Fragment 中
 * lifecycleScope.launch {
 *     PictureSelectorHelper.selectImage(this@MainActivity)
 *         .config {
 *             maxSelectNum = 9
 *             isCompress = true
 *             isCrop = false
 *         }
 *         .start()
 *         .collect { result ->
 *             if (result.isSuccess) {
 *                 // 处理选择的图片
 *                 result.mediaList.forEach { media ->
 *                     Log.d("Path", media.realPath)
 *                 }
 *             } else {
 *                 // 处理错误
 *                 Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_SHORT).show()
 *             }
 *         }
 * }
 * ```
 */
class PictureSelectorHelper private constructor(
    private val activity: FragmentActivity?,
    private val fragment: Fragment?,
    private val selectionMode: SelectionMode
) {

    private var config = PictureSelectorConfig(selectionMode = selectionMode)

    companion object {
        /**
         * 选择图片
         */
        fun selectImage(activity: FragmentActivity): PictureSelectorHelper {
            return PictureSelectorHelper(activity, null, SelectionMode.IMAGE)
        }

        fun selectImage(fragment: Fragment): PictureSelectorHelper {
            return PictureSelectorHelper(null, fragment, SelectionMode.IMAGE)
        }

        /**
         * 选择视频
         */
        fun selectVideo(activity: FragmentActivity): PictureSelectorHelper {
            return PictureSelectorHelper(activity, null, SelectionMode.VIDEO)
        }

        fun selectVideo(fragment: Fragment): PictureSelectorHelper {
            return PictureSelectorHelper(null, fragment, SelectionMode.VIDEO)
        }

        /**
         * 选择图片和视频
         */
        fun selectMedia(activity: FragmentActivity): PictureSelectorHelper {
            return PictureSelectorHelper(activity, null, SelectionMode.IMAGE_VIDEO)
        }

        fun selectMedia(fragment: Fragment): PictureSelectorHelper {
            return PictureSelectorHelper(null, fragment, SelectionMode.IMAGE_VIDEO)
        }

        /**
         * 拍照
         */
        fun takePhoto(activity: FragmentActivity): PictureSelectorHelper {
            return PictureSelectorHelper(activity, null, SelectionMode.IMAGE).apply {
                config = config.copy(isCamera = true, maxSelectNum = 1)
            }
        }

        fun takePhoto(fragment: Fragment): PictureSelectorHelper {
            return PictureSelectorHelper(null, fragment, SelectionMode.IMAGE).apply {
                config = config.copy(isCamera = true, maxSelectNum = 1)
            }
        }

        /**
         * 录制视频
         */
        fun recordVideo(activity: FragmentActivity): PictureSelectorHelper {
            return PictureSelectorHelper(activity, null, SelectionMode.VIDEO).apply {
                config = config.copy(isCamera = true, maxSelectNum = 1)
            }
        }

        fun recordVideo(fragment: Fragment): PictureSelectorHelper {
            return PictureSelectorHelper(null, fragment, SelectionMode.VIDEO).apply {
                config = config.copy(isCamera = true, maxSelectNum = 1)
            }
        }
    }

    /**
     * 配置参数
     */
    fun config(block: PictureSelectorConfig.() -> PictureSelectorConfig): PictureSelectorHelper {
        config = block(config)
        return this
    }

    /**
     * 设置配置
     */
    fun setConfig(config: PictureSelectorConfig): PictureSelectorHelper {
        this.config = config
        return this
    }

    /**
     * 启动图片选择，返回 Flow
     */
    fun start(): Flow<PictureSelectorResult> = callbackFlow {
        try {
            val context = activity ?: fragment?.requireActivity()
            if (context == null) {
                trySend(
                    PictureSelectorResult(
                        isSuccess = false,
                        errorMessage = "Activity or Fragment is null"
                    )
                )
                close()
                return@callbackFlow
            }

            // 创建 PictureSelector
            val selector = if (activity != null) {
                PictureSelector.create(activity)
            } else {
                PictureSelector.create(fragment!!)
            }

            // 设置选择模式
            val mimeType = when (config.selectionMode) {
                SelectionMode.IMAGE -> SelectMimeType.ofImage()
                SelectionMode.VIDEO -> SelectMimeType.ofVideo()
                SelectionMode.AUDIO -> SelectMimeType.ofAudio()
                SelectionMode.IMAGE_VIDEO -> SelectMimeType.ofAll()
                SelectionMode.ALL -> SelectMimeType.ofAll()
            }

            // 打开相册
            selector.openGallery(mimeType)
                .setImageEngine(CoilEngine.createCoilEngine())
                .setMaxSelectNum(config.maxSelectNum)
                .setMinSelectNum(config.minSelectNum)
                .isDisplayCamera(config.isDisplayCamera && config.isCamera)
                .isGif(config.isGif)
                .isPreviewImage(config.isPreviewImage)
                .isPreviewVideo(config.isPreviewVideo)
                .isMaxSelectEnabledMask(true)
                .apply {
                    // 文件大小限制
                    if (config.maxFileSize > 0) {
                        setFilterMaxFileSize(config.maxFileSize)
                    }
                    
                    // 视频时长限制
                    if (config.selectionMode == SelectionMode.VIDEO || 
                        config.selectionMode == SelectionMode.IMAGE_VIDEO) {
                        setFilterVideoMinSecond(config.videoMinSecond)
                        setFilterVideoMaxSecond(config.videoMaxSecond)
                        setRecordVideoMaxSecond(config.recordVideoSecond)
                    }
                    
                    // 已选中的数据
                    config.selectedData?.let { 
                        setSelectedData(ArrayList(it))
                    }
                    
                    // 是否显示原图控制
                    isOriginalControl(config.isOriginalControl)
                }
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if (result.isNullOrEmpty()) {
                            trySend(
                                PictureSelectorResult(
                                    isSuccess = true,
                                    mediaList = emptyList()
                                )
                            )
                        } else {
                            val mediaList = result.map { MediaData.fromLocalMedia(it) }
                            trySend(
                                PictureSelectorResult(
                                    isSuccess = true,
                                    mediaList = mediaList
                                )
                            )
                        }
                        close()
                    }

                    override fun onCancel() {
                        trySend(
                            PictureSelectorResult(
                                isSuccess = false,
                                errorMessage = "User cancelled"
                            )
                        )
                        close()
                    }
                })

        } catch (e: Exception) {
            trySend(
                PictureSelectorResult(
                    isSuccess = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            )
            close(e)
        }

        awaitClose {
            // 清理资源
        }
    }

    /**
     * 拍照（直接调用相机）
     */
    fun startCamera(): Flow<PictureSelectorResult> = callbackFlow {
        try {
            val context = activity ?: fragment?.requireActivity()
            if (context == null) {
                trySend(
                    PictureSelectorResult(
                        isSuccess = false,
                        errorMessage = "Activity or Fragment is null"
                    )
                )
                close()
                return@callbackFlow
            }

            val selector = if (activity != null) {
                PictureSelector.create(activity)
            } else {
                PictureSelector.create(fragment!!)
            }

            val mimeType = when (config.selectionMode) {
                SelectionMode.VIDEO -> SelectMimeType.ofVideo()
                else -> SelectMimeType.ofImage()
            }

            selector.openCamera(mimeType)
                .apply {
                    if (config.selectionMode == SelectionMode.VIDEO) {
                        setRecordVideoMaxSecond(config.recordVideoSecond)
                    }
                }
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if (result.isNullOrEmpty()) {
                            trySend(
                                PictureSelectorResult(
                                    isSuccess = true,
                                    mediaList = emptyList()
                                )
                            )
                        } else {
                            val mediaList = result.map { MediaData.fromLocalMedia(it) }
                            trySend(
                                PictureSelectorResult(
                                    isSuccess = true,
                                    mediaList = mediaList
                                )
                            )
                        }
                        close()
                    }

                    override fun onCancel() {
                        trySend(
                            PictureSelectorResult(
                                isSuccess = false,
                                errorMessage = "User cancelled"
                            )
                        )
                        close()
                    }
                })

        } catch (e: Exception) {
            trySend(
                PictureSelectorResult(
                    isSuccess = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            )
            close(e)
        }

        awaitClose {
            // 清理资源
        }
    }

    /**
     * 预览图片
     */
    fun previewImages(
        mediaList: List<MediaData>,
        position: Int = 0
    ): Flow<PictureSelectorResult> = callbackFlow {
        try {
            val localMediaList = mediaList.map { media ->
                LocalMedia().apply {
                    path = media.path
                    this.width = media.width
                    this.height = media.height
                    this.size = media.size
                    this.duration = media.duration
                    this.mimeType = media.mimeType
                }
            }

            if (activity != null) {
                PictureSelector.create(activity)
            } else if (fragment != null) {
                PictureSelector.create(fragment)
            } else {
                trySend(
                    PictureSelectorResult(
                        isSuccess = false,
                        errorMessage = "Activity or Fragment is null"
                    )
                )
                close()
                return@callbackFlow
            }?.let { selector ->
                selector.openPreview()
                    .setImageEngine(CoilEngine.createCoilEngine())
                    .setSelectorUIStyle(null)
                    .startActivityPreview(position, true, ArrayList(localMediaList))

                trySend(
                    PictureSelectorResult(
                        isSuccess = true,
                        mediaList = emptyList()
                    )
                )
            }

            close()
        } catch (e: Exception) {
            trySend(
                PictureSelectorResult(
                    isSuccess = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            )
            close(e)
        }

        awaitClose {
            // 清理资源
        }
    }
}
