# Android Utils 工具库

这是一个基于 Kotlin 的 Android 工具库，提供了一系列常用的工具类和扩展功能，帮助开发者更高效地构建 Android 应用。

## 功能模块

### 1. DataStoreManager
- 对 Android Jetpack DataStore 的 Kotlin 封装
- 支持多种数据类型：String、Int、Long、Float、Double、Boolean、Set<String>
- 提供协程和同步两种操作方式
- 支持数据流观察，实时响应数据变化
- 支持批量操作和清除所有数据
- 线程安全，使用单例模式管理 DataStore 实例

### 2. 权限管理 (XXPermissions)
- 基于 XXPermissions 库的 Kotlin 封装
- 提供友好的权限请求流程
- 自动生成权限描述文案
- 支持权限描述弹窗
- 提供常用权限的快捷方法
- 扩展函数支持，使用更简洁

### 3. 事件总线 (EventBus)
- 轻量级事件总线实现
- 支持 Kotlin 协程
- 支持事件订阅和发布
- 线程安全

### 4. 图片选择器 (PictureSelector)
- 基于 PictureSelector 库的封装
- 提供简洁的图片选择 API
- 支持图片预览、裁剪等功能
- 集成 Coil 图片加载库

### 5. 参数委托 (ArgumentDelegate)
- 简化 Activity 和 Fragment 的参数传递
- 支持多种数据类型的参数获取
- 使用 Kotlin 委托属性，代码更简洁

## 技术栈

- Kotlin
- AndroidX
- Kotlin Coroutines
- XXPermissions (权限管理)
- PictureSelector (图片选择)
- Coil (图片加载)
- DataStore (数据存储)

## 如何使用 JitPack 引入

### 1. 在项目级 build.gradle 文件中添加 JitPack 仓库

```gradle
// 项目级 build.gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url 'https://jitpack.io'
        }
    }
}
```

### 2. 在应用级 build.gradle 文件中添加依赖

```gradle
// 应用级 build.gradle
dependencies {
    implementation 'com.github.guowenlong123:utils:v1.0.0'
}
```

### 3. 同步项目

点击 Android Studio 中的 "Sync Now" 按钮，同步项目依赖。

## 使用示例

### DataStoreManager 使用示例

```kotlin
// 初始化（在 Application 类中）
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DataStoreManager.initialize(this)
    }
}

// 保存数据
lifecycleScope.launch {
    saveData("username", "张三")
    saveData("age", 25)
    saveData("isLogin", true)
}

// 读取数据
lifecycleScope.launch {
    val username = readData("username", "")
    val age = readData("age", 0)
    val isLogin = readData("isLogin", false)
}

// 观察数据变化
readDataFlow("isLogin", false)
    .onEach { isLogin ->
        println("登录状态变化: $isLogin")
    }
    .launchIn(lifecycleScope)
```

### 权限管理使用示例

```kotlin
// 请求相机权限
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

// 使用扩展函数
launchRequestPermissions(Manifest.permission.CAMERA) {
    if (it.isAllGranted) {
        openCamera()
    }
}
```

### 图片选择器使用示例

```kotlin
// 选择单张图片
lifecycleScope.launch {
    val result = PictureSelectorHelper.selectImage(this@MainActivity)
    if (result.isSuccess) {
        val imagePath = result.getOrNull()
        // 处理图片路径
    }
}

// 选择多张图片
lifecycleScope.launch {
    val result = PictureSelectorHelper.selectImages(this@MainActivity, maxSelectCount = 9)
    if (result.isSuccess) {
        val imagePaths = result.getOrNull()
        // 处理图片路径列表
    }
}
```

## 项目结构

```
src/
├── main/
│   ├── java/com/lib/utils/
│   │   ├── argument/         // 参数委托相关
│   │   ├── datastore/        // DataStore 管理相关
│   │   ├── event/            // 事件总线相关
│   │   ├── permission/       // 权限管理相关
│   │   └── pictureselector/  // 图片选择器相关
│   ├── res/                  // 资源文件
│   └── AndroidManifest.xml   // 清单文件
├── test/                     // 单元测试
└── androidTest/              // 仪器测试
```

## 版本信息

- **Current Version**: v1.0.0
- **Min SDK**: 24
- **Target SDK**: 34
- **Compile SDK**: 34

## 依赖说明

本库使用了以下第三方库：

- [XXPermissions](https://github.com/getActivity/XXPermissions) - 权限管理
- [PictureSelector](https://github.com/LuckSiege/PictureSelector) - 图片选择
- [Coil](https://github.com/coil-kt/coil) - 图片加载
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - 数据存储
- [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - 协程支持

## 如何贡献

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件

## 联系方式

- GitHub: [guowenlong123](https://github.com/guowenlong123)

---

希望这个工具库能够帮助你更高效地开发 Android 应用！如果有任何问题或建议，欢迎提出 Issue 或 Pull Request。