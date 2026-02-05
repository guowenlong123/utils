package com.lib.utils.pictureselector

/**
 * PictureSelector 使用示例
 * 
 * ==================== 基础使用 ====================
 * 
 * 1. 选择单张图片
 * ```kotlin
 * class MainActivity : AppCompatActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         
 *         // 方式1：使用 Flow
 *         lifecycleScope.launch {
 *             selectImage().collect { result ->
 *                 if (result.isSuccess) {
 *                     val path = result.mediaList.firstOrNull()?.realPath
 *                     // 使用图片路径
 *                 }
 *             }
 *         }
 *         
 *         // 方式2：使用便捷扩展函数
 *         launchSelectImage { result ->
 *             if (result.isSuccess) {
 *                 val path = result.mediaList.firstOrNull()?.realPath
 *                 // 使用图片路径
 *             }
 *         }
 *     }
 * }
 * ```
 * 
 * 2. 选择多张图片
 * ```kotlin
 * launchSelectImage(
 *     config = {
 *         copy(
 *             maxSelectNum = 9,
 *             isCompress = true,
 *             compressQuality = 80
 *         )
 *     }
 * ) { result ->
 *     if (result.isSuccess) {
 *         result.mediaList.forEach { media ->
 *             Log.d("Image", "Path: ${media.realPath}")
 *             Log.d("Image", "Size: ${media.size}")
 *             Log.d("Image", "Width: ${media.width}, Height: ${media.height}")
 *         }
 *     }
 * }
 * ```
 * 
 * 3. 选择并裁剪图片
 * ```kotlin
 * launchSelectImage(
 *     config = {
 *         copy(
 *             maxSelectNum = 1,
 *             isCrop = true,
 *             isCircleCrop = false, // 圆形裁剪
 *             cropAspectRatioX = 1,
 *             cropAspectRatioY = 1
 *         )
 *     }
 * ) { result ->
 *     if (result.isSuccess) {
 *         val media = result.mediaList.firstOrNull()
 *         val croppedPath = media?.cutPath ?: media?.realPath
 *         // 使用裁剪后的图片
 *     }
 * }
 * ```
 * 
 * 4. 拍照
 * ```kotlin
 * launchTakePhoto { result ->
 *     if (result.isSuccess) {
 *         val photoPath = result.mediaList.firstOrNull()?.realPath
 *         // 处理拍照的图片
 *     }
 * }
 * ```
 * 
 * 5. 选择视频
 * ```kotlin
 * launchSelectVideo(
 *     config = {
 *         copy(
 *             maxSelectNum = 1,
 *             videoMaxSecond = 60, // 最大60秒
 *             videoMinSecond = 3   // 最小3秒
 *         )
 *     }
 * ) { result ->
 *     if (result.isSuccess) {
 *         val media = result.mediaList.firstOrNull()
 *         Log.d("Video", "Path: ${media?.realPath}")
 *         Log.d("Video", "Duration: ${media?.duration}ms")
 *     }
 * }
 * ```
 * 
 * 6. 录制视频
 * ```kotlin
 * launchRecordVideo(
 *     config = {
 *         copy(recordVideoSecond = 30) // 录制最长30秒
 *     }
 * ) { result ->
 *     if (result.isSuccess) {
 *         val videoPath = result.mediaList.firstOrNull()?.realPath
 *         // 处理录制的视频
 *     }
 * }
 * ```
 * 
 * ==================== 高级配置 ====================
 * 
 * 7. 完整配置示例
 * ```kotlin
 * lifecycleScope.launch {
 *     PictureSelectorHelper.selectImage(this@MainActivity)
 *         .setConfig(
 *             PictureSelectorConfig(
 *                 selectionMode = SelectionMode.IMAGE,
 *                 maxSelectNum = 9,
 *                 minSelectNum = 1,
 *                 isCamera = true,
 *                 isCrop = false,
 *                 isCompress = true,
 *                 compressQuality = 80,
 *                 isGif = true,
 *                 minWidth = 100,
 *                 minHeight = 100,
 *                 maxWidth = 4096,
 *                 maxHeight = 4096,
 *                 maxFileSize = 10 * 1024, // 10MB
 *                 isOriginalControl = true,
 *                 isPreviewImage = true,
 *                 isDisplayCamera = true
 *             )
 *         )
 *         .start()
 *         .collect { result ->
 *             if (result.isSuccess) {
 *                 // 处理结果
 *             } else {
 *                 // 处理错误
 *                 Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_SHORT).show()
 *             }
 *         }
 * }
 * ```
 * 
 * 8. 在 Fragment 中使用
 * ```kotlin
 * class MyFragment : Fragment() {
 *     
 *     private fun selectImage() {
 *         launchSelectImage(
 *             config = {
 *                 copy(maxSelectNum = 6)
 *             }
 *         ) { result ->
 *             if (result.isSuccess) {
 *                 // 处理图片
 *             }
 *         }
 *     }
 * }
 * ```
 * 
 * 9. 文件大小限制
 * ```kotlin
 * launchSelectImage(
 *     config = {
 *         copy(
 *             maxFileSize = 5 * 1024, // 最大 5MB
 *             minWidth = 500,
 *             minHeight = 500
 *         )
 *     }
 * ) { result ->
 *     // 处理结果
 * }
 * ```
 * 
 * 10. 已选择数据回显
 * ```kotlin
 * val selectedList: List<MediaData> = ... // 已选择的数据
 * 
 * launchSelectImage(
 *     config = {
 *         copy(
 *             maxSelectNum = 9,
 *             selectedData = selectedList.map { media ->
 *                 LocalMedia().apply {
 *                     path = media.path
 *                     width = media.width
 *                     height = media.height
 *                 }
 *             }
 *         )
 *     }
 * ) { result ->
 *     // 处理新选择的结果
 * }
 * ```
 * 
 * ==================== 图片预览 ====================
 * 
 * 11. 预览图片
 * ```kotlin
 * lifecycleScope.launch {
 *     PictureSelectorHelper.selectImage(this@MainActivity)
 *         .previewImages(
 *             mediaList = mediaList, // List<MediaData>
 *             position = 0  // 当前位置
 *         )
 *         .collect { result ->
 *             // 预览完成
 *         }
 * }
 * ```
 * 
 * ==================== 使用 CoilEngine ====================
 * 
 * 12. 自定义使用 CoilEngine
 * ```kotlin
 * val coilEngine = CoilEngine.createCoilEngine()
 * 
 * // 加载图片
 * coilEngine.loadImage(context, imageUrl, imageView)
 * 
 * // 加载圆形图片
 * coilEngine.loadCircleImage(context, imageUrl, imageView)
 * 
 * // 加载圆角图片
 * coilEngine.loadRoundedImage(context, imageUrl, imageView, 16f)
 * 
 * // 加载 GIF
 * coilEngine.loadGifImage(context, gifUrl, imageView)
 * ```
 * 
 * ==================== 权限处理 ====================
 * 
 * 注意：需要在 AndroidManifest.xml 中添加权限：
 * ```xml
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
 *     android:maxSdkVersion="28" />
 * <uses-permission android:name="android.permission.CAMERA" />
 * ```
 * 
 * Android 13+ 需要：
 * ```xml
 * <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
 * <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
 * ```
 */
