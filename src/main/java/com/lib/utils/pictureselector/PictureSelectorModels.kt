package com.lib.utils.pictureselector

import com.luck.picture.lib.entity.LocalMedia

/**
 * 图片选择配置
 */
data class PictureSelectorConfig(
    /** 选择模式：图片、视频、音频 */
    val selectionMode: SelectionMode = SelectionMode.IMAGE,
    
    /** 最大选择数量 */
    val maxSelectNum: Int = 1,
    
    /** 最小选择数量 */
    val minSelectNum: Int = 1,
    
    /** 是否显示相机 */
    val isCamera: Boolean = true,
    
    /** 是否裁剪 */
    val isCrop: Boolean = false,
    
    /** 是否圆形裁剪 */
    val isCircleCrop: Boolean = false,
    
    /** 裁剪宽度比例 */
    val cropAspectRatioX: Int = 1,
    
    /** 裁剪高度比例 */
    val cropAspectRatioY: Int = 1,
    
    /** 是否压缩 */
    val isCompress: Boolean = true,
    
    /** 压缩质量 0-100 */
    val compressQuality: Int = 80,
    
    /** 是否显示 GIF */
    val isGif: Boolean = false,
    
    /** 视频最大时长（秒） */
    val videoMaxSecond: Int = 60,
    
    /** 视频最小时长（秒） */
    val videoMinSecond: Int = 1,
    
    /** 录制视频秒数 */
    val recordVideoSecond: Int = 60,
    
    /** 图片最小宽度 */
    val minWidth: Int = 0,
    
    /** 图片最小高度 */
    val minHeight: Int = 0,
    
    /** 图片最大宽度 */
    val maxWidth: Int = 0,
    
    /** 图片最大高度 */
    val maxHeight: Int = 0,
    
    /** 图片最大文件大小（KB） */
    val maxFileSize: Long = 0,
    
    /** 视频最大文件大小（KB） */
    val videoMaxFileSize: Long = 0,
    
    /** 是否显示原图按钮 */
    val isOriginalControl: Boolean = false,
    
    /** 是否预览视频 */
    val isPreviewVideo: Boolean = true,
    
    /** 是否预览图片 */
    val isPreviewImage: Boolean = true,
    
    /** 是否预览音频 */
    val isPreviewAudio: Boolean = true,
    
    /** 是否显示已选择的文件 */
    val isDisplayCamera: Boolean = true,
    
    /** 是否使用系统相机 */
    val isUseSystemCamera: Boolean = false,
    
    /** 已选中的本地资源 */
    val selectedData: List<LocalMedia>? = null,
    
    /** 裁剪输出路径 */
    val cropOutputPath: String? = null,
    
    /** 压缩输出路径 */
    val compressOutputPath: String? = null
)

/**
 * 选择模式枚举
 */
enum class SelectionMode {
    /** 只显示图片 */
    IMAGE,
    
    /** 只显示视频 */
    VIDEO,
    
    /** 只显示音频 */
    AUDIO,
    
    /** 图片和视频 */
    IMAGE_VIDEO,
    
    /** 全部 */
    ALL
}

/**
 * 图片选择结果
 */
data class PictureSelectorResult(
    /** 是否成功 */
    val isSuccess: Boolean,
    
    /** 选中的媒体列表 */
    val mediaList: List<MediaData> = emptyList(),
    
    /** 错误信息 */
    val errorMessage: String? = null
)

/**
 * 媒体数据
 */
data class MediaData(
    /** 文件路径 */
    val path: String,
    
    /** 真实路径（裁剪或压缩后） */
    val realPath: String,
    
    /** 压缩路径 */
    val compressPath: String? = null,
    
    /** 裁剪路径 */
    val cutPath: String? = null,
    
    /** 原始路径 */
    val originalPath: String? = null,
    
    /** 文件名 */
    val fileName: String,
    
    /** 宽度 */
    val width: Int,
    
    /** 高度 */
    val height: Int,
    
    /** 文件大小 */
    val size: Long,
    
    /** 时长（视频/音频） */
    val duration: Long = 0,
    
    /** MIME 类型 */
    val mimeType: String,
    
    /** 是否是图片 */
    val isImage: Boolean,
    
    /** 是否是视频 */
    val isVideo: Boolean,
    
    /** 是否是音频 */
    val isAudio: Boolean,
    
    /** 是否是 GIF */
    val isGif: Boolean,
    
    /** 是否被裁剪 */
    val isCut: Boolean,
    
    /** 是否被压缩 */
    val isCompressed: Boolean
) {
    companion object {
        /**
         * 从 LocalMedia 转换为 MediaData
         */
        fun fromLocalMedia(media: LocalMedia): MediaData {
            return MediaData(
                path = media.path ?: "",
                realPath = media.availablePath ?: media.path ?: "",
                compressPath = media.compressPath,
                cutPath = media.cutPath,
                originalPath = media.originalPath,
                fileName = media.fileName ?: "",
                width = media.width,
                height = media.height,
                size = media.size,
                duration = media.duration,
                mimeType = media.mimeType ?: "",
                isImage = media.mimeType?.startsWith("image/") == true,
                isVideo = media.mimeType?.startsWith("video/") == true,
                isAudio = media.mimeType?.startsWith("audio/") == true,
                isGif = media.mimeType == "image/gif",
                isCut = !media.cutPath.isNullOrEmpty(),
                isCompressed = !media.compressPath.isNullOrEmpty()
            )
        }
    }
}
