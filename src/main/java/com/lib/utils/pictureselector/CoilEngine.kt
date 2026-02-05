package com.lib.utils.pictureselector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper

/**
 * Coil 图片加载引擎实现
 * 用于 PictureSelector 3.0
 */
class CoilEngine private constructor() : ImageEngine {

    companion object {
        private var instance: CoilEngine? = null

        fun createCoilEngine(): CoilEngine {
            return instance ?: synchronized(this) {
                instance ?: CoilEngine().also { instance = it }
            }
        }
    }

    /**
     * 加载图片
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.load(url) {
            crossfade(true)
        }
    }

    /**
     * 加载指定宽高的图片
     */
    override fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String,
        maxWidth: Int,
        maxHeight: Int
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.load(url) {
            size(maxWidth, maxHeight)
            crossfade(true)
        }
    }

    /**
     * 加载相册目录封面
     */
    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.load(url) {
            crossfade(true)
            size(180, 180)
        }
    }

    /**
     * 加载图片列表缩略图
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.load(url) {
            crossfade(true)
            size(200, 200)
        }
    }

    /**
     * 暂停请求
     */
    override fun pauseRequests(context: Context) {
        // Coil 不需要特殊处理暂停
    }

    /**
     * 恢复请求
     */
    override fun resumeRequests(context: Context) {
        // Coil 不需要特殊处理恢复
    }

    /**
     * 加载圆角图片
     */
    fun loadRoundedImage(
        context: Context,
        url: String,
        imageView: ImageView,
        cornerRadius: Float
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.load(url) {
            crossfade(true)
            transformations(RoundedCornersTransformation(cornerRadius))
        }
    }

    /**
     * 加载圆形图片
     */
    fun loadCircleImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.load(url) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    /**
     * 加载 GIF 图片
     */
    fun loadGifImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.load(url) {
            crossfade(false) // GIF 不使用淡入效果
        }
    }

    /**
     * 加载视频帧
     */
    fun loadVideoFrame(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.load(url) {
            crossfade(true)
        }
    }

    /**
     * 创建 ImageLoader 实例
     */
    fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(VideoFrameDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }
}
