# XXPermissions Kotlin 封装使用示例

本文档提供 XXPermissions 库的 Kotlin 封装使用示例。

## 基本用法

### 1. 显示权限描述弹窗

```kotlin
class MainActivity : AppCompatActivity() {
    
    private fun requestCameraPermission() {
        lifecycleScope.launch {
            XXPermissionsHelper.request(this@MainActivity)
                .permissions(Manifest.permission.CAMERA)
                .showDescription()  // 显示权限描述弹窗
                .start()
                .collect { result ->
                    if (result.isAllGranted) {
                        Toast.makeText(this@MainActivity, "相机权限已授予", Toast.LENGTH_SHORT).show()
                        openCamera()
                    } else {
                        Toast.makeText(this@MainActivity, "权限被拒绝", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
```

### 2. 自定义权限描述

```kotlin
private fun requestPermissions() {
    lifecycleScope.launch {
        XXPermissionsHelper.request(this@MainActivity)
            .permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
            .description("我们需要相机和录音权限来录制视频")
            .start()
            .collect { result ->
                if (result.isAllGranted) {
                    // 权限已授予
                }
            }
    }
}
```

### 3. 不显示描述直接请求

```kotlin
private fun requestPermissionDirectly() {
    launchRequestPermissions(Manifest.permission.CAMERA) { result ->
        if (result.isAllGranted) {
            // 权限已授予
        }
    }
}
```

## 扩展函数用法

### Activity 扩展

```kotlin
// 请求权限
launchRequestPermissions(Manifest.permission.CAMERA) { result ->
    if (result.isAllGranted) {
        openCamera()
    }
}

// 检查权限
if (hasPermissions(Manifest.permission.CAMERA)) {
    openCamera()
}

// 跳转设置
if (isPermissionPermanentDenied(Manifest.permission.CAMERA)) {
    openPermissionSettings(Manifest.permission.CAMERA)
}
```

## 常用权限快捷方法

```kotlin
// 相机权限
lifecycleScope.launch {
    requestCameraPermission().collect { result ->
        if (result.isAllGranted) {
            // 打开相机
        }
    }
}

// 存储权限
lifecycleScope.launch {
    requestStoragePermission().collect { result ->
        if (result.isAllGranted) {
            // 读写文件
        }
    }
}

// 位置权限
lifecycleScope.launch {
    requestLocationPermission().collect { result ->
        if (result.isAllGranted) {
            // 获取位置
        }
    }
}
```

## 权限描述功能

### 自动描述

系统会自动为常用权限生成友好的中文描述：

- **相机权限**："用于拍摄照片和录制视频"
- **存储权限**："用于读取/保存图片、视频和文件到设备存储"
- **位置权限**："用于获取精确/大致位置信息，提供基于位置的服务"
- **录音权限**："用于录制音频和语音通话"
- 等等...

### 描述弹窗特性

- ✅ 顶部弹窗显示，不阻挡主要内容
- ✅ CardView 卡片设计，美观大方
- ✅ 支持确定/取消按钮
- ✅ 点击外部可关闭
- ✅ 自动显示在状态栏下方

## 完整示例

```kotlin
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<Button>(R.id.btnCamera).setOnClickListener {
            handleCameraPermission()
        }
    }
    
    private fun handleCameraPermission() {
        // 先检查是否已有权限
        if (hasPermissions(Manifest.permission.CAMERA)) {
            openCamera()
            return
        }
        
        // 检查是否被永久拒绝
        if (isPermissionPermanentDenied(Manifest.permission.CAMERA)) {
            showGoToSettingsDialog()
            return
        }
        
        // 请求权限（显示描述）
        lifecycleScope.launch {
            XXPermissionsHelper.request(this@MainActivity)
                .permissions(Manifest.permission.CAMERA)
                .showDescription()
                .start()
                .collect { result ->
                    when {
                        result.isAllGranted -> {
                            Toast.makeText(this@MainActivity, "权限已授予", Toast.LENGTH_SHORT).show()
                            openCamera()
                        }
                        result.hasPermanentDenied -> {
                            showGoToSettingsDialog()
                        }
                        else -> {
                            Toast.makeText(this@MainActivity, "权限被拒绝", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }
    
    private fun showGoToSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("权限被拒绝")
            .setMessage("相机权限已被永久拒绝，请到设置页面手动开启")
            .setPositiveButton("去设置") { _, _ ->
                openPermissionSettings(Manifest.permission.CAMERA)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun openCamera() {
        Log.d("Camera", "Opening camera...")
    }
}
```

## 依赖配置

在 `build.gradle` 中添加：

```gradle
dependencies {
    // XXPermissions
    implementation 'com.github.getActivity:XXPermissions:18.63'
    
    // Kotlin Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // AndroidX
    implementation 'androidx.activity:activity-ktx:1.7.2'
    implementation 'androidx.fragment:fragment-ktx:1.6.0'
    implementation 'androidx.cardview:cardview:1.0.0'
}
```
