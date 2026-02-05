# DataStoreManager 用法指南

## 简介

DataStoreManager 是对 Android Jetpack DataStore 的 Kotlin 封装，提供了简洁易用的 API 来管理应用配置和状态数据。它支持以下特性：

- 支持多种数据类型：String、Int、Long、Float、Double、Boolean、Set<String>
- 提供协程和同步两种操作方式
- 支持数据流观察，实时响应数据变化
- 支持批量操作和清除所有数据
- 线程安全，使用单例模式管理 DataStore 实例

## 集成步骤

### 1. 添加依赖

在项目的 `build.gradle` 文件中添加 DataStore 依赖：

```groovy
// 项目级 build.gradle
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// 应用级 build.gradle
dependencies {
    // DataStore Preferences
    implementation "androidx.datastore:datastore-preferences:1.0.0"
    
    // Kotlin 协程（如果尚未添加）
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
}
```

### 2. 复制代码

将 `DataStoreManager.kt` 文件复制到您的项目中。

## 使用指南

### 1. 初始化（推荐）

在 Application 类中初始化一次 DataStoreManager：

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 DataStoreManager
        DataStoreManager.initialize(this)
    }
}
```

### 2. 初始化（兼容旧版本）

如果不想在 Application 中初始化，也可以在第一次使用时自动初始化：

```kotlin
val dataStoreManager = DataStoreManager.getInstance(context)
```

### 3. 保存数据

#### 协程方式（推荐）

```kotlin
// 保存单个数据
lifecycleScope.launch {
    saveData("username", "张三")
    saveData("age", 25)
    saveData("isLogin", true)
    saveData("score", 98.5)
}

// 保存多个数据
lifecycleScope.launch {
    val userData = mapOf(
        "username" to "李四",
        "age" to 30,
        "isLogin" to false,
        "score" to 85.5
    )
    saveDataAll(userData)
}
```

#### 同步方式（不推荐在主线程使用）

```kotlin
// 保存单个数据
saveDataSync("username", "张三")

// 保存多个数据
val userData = mapOf(
    "username" to "李四",
    "age" to 30
)
saveDataAllSync(userData)
```

### 4. 读取数据

#### 协程方式（推荐）

```kotlin
lifecycleScope.launch {
    val username = readData("username", "")
    val age = readData("age", 0)
    val isLogin = readData("isLogin", false)
    val score = readData("score", 0.0)
    
    println("用户名: $username, 年龄: $age, 是否登录: $isLogin, 分数: $score")
}
```

#### 同步方式（不推荐在主线程使用）

```kotlin
val username = readDataSync("username", "")
val age = readDataSync("age", 0)
```

### 5. 观察数据变化

使用数据流观察数据变化，实时响应：

```kotlin
// 观察用户名变化
readDataFlow("username", "")
    .onEach { username ->
        println("用户名变化: $username")
        // 更新 UI 或执行其他操作
    }
    .launchIn(lifecycleScope) // 需要在 LifecycleOwner 中使用

// 观察登录状态变化
readDataFlow("isLogin", false)
    .onEach { isLogin ->
        println("登录状态变化: $isLogin")
        // 更新 UI 或执行其他操作
    }
    .launchIn(lifecycleScope)
```

### 6. 删除数据

#### 协程方式（推荐）

```kotlin
lifecycleScope.launch {
    // 删除单个数据
    deleteData("username")
    
    // 清除所有数据
    clearAllData()
}
```

#### 同步方式（不推荐在主线程使用）

```kotlin
// 删除单个数据
deleteDataSync("username")

// 清除所有数据
clearAllDataSync()
```

### 7. 其他操作

```kotlin
lifecycleScope.launch {
    val dataStoreManager = DataStoreManager.getInstance()
    
    // 检查键是否存在
    val exists = dataStoreManager.contains("username")
    println("username 键是否存在: $exists")
    
    // 获取所有键
    val keys = dataStoreManager.getAllKeys()
    println("所有键: $keys")
    
    // 获取所有数据
    val allData = dataStoreManager.getAll()
    println("所有数据: $allData")
}
```

## 支持的数据类型

| 类型 | 示例 |
|------|------|
| String | `"张三"` |
| Int | `25` |
| Long | `1234567890L` |
| Float | `98.5f` |
| Double | `98.5` |
| Boolean | `true` |
| Set<String> | `setOf("苹果", "香蕉", "橙子")` |

## 注意事项

1. **线程安全**：DataStoreManager 是线程安全的，可以在任何线程中使用。

2. **主线程操作**：
   - 协程方式：推荐在主线程使用，内部会自动切换到 IO 线程执行
   - 同步方式：不推荐在主线程使用，可能会阻塞 UI

3. **异常处理**：内部已处理 IOException，确保操作不会崩溃。

4. **默认值**：读取数据时必须提供默认值，当数据不存在时会返回默认值。

5. **数据流**：使用 `readDataFlow` 时，需要在适当的生命周期作用域中启动流，避免内存泄漏。

## 示例应用

### 保存用户设置

```kotlin
// 保存用户设置
lifecycleScope.launch {
    saveData("darkMode", true)
    saveData("notificationsEnabled", true)
    saveData("language", "zh-CN")
    saveData("fontSize", 16)
}

// 读取用户设置
lifecycleScope.launch {
    val darkMode = readData("darkMode", false)
    val notificationsEnabled = readData("notificationsEnabled", true)
    val language = readData("language", "zh-CN")
    val fontSize = readData("fontSize", 14)
    
    // 应用设置
    updateTheme(darkMode)
    updateNotificationSettings(notificationsEnabled)
    updateLanguage(language)
    updateFontSize(fontSize)
}

// 观察设置变化
readDataFlow("darkMode", false)
    .onEach { darkMode ->
        updateTheme(darkMode)
    }
    .launchIn(lifecycleScope)
```

### 保存登录状态

```kotlin
// 登录成功后保存状态
lifecycleScope.launch {
    val userData = mapOf(
        "isLoggedIn" to true,
        "userId" to "12345",
        "username" to "张三",
        "lastLoginTime" to System.currentTimeMillis()
    )
    saveDataAll(userData)
}

// 检查登录状态
val isLoggedIn = readDataSync("isLoggedIn", false)
if (isLoggedIn) {
    // 已登录，跳转到主页面
} else {
    // 未登录，跳转到登录页面
}

// 退出登录
lifecycleScope.launch {
    saveData("isLoggedIn", false)
    // 可选：清除用户数据
    deleteData("userId")
    deleteData("username")
}
```

## 总结

DataStoreManager 提供了一种简洁、高效的方式来管理应用的配置和状态数据。通过封装 DataStore，它简化了数据存储的操作，同时保持了 DataStore 的所有优点，如类型安全、异步操作和实时数据观察。

使用 DataStoreManager，您可以：
- 轻松保存和读取各种类型的数据
- 实时观察数据变化，响应状态更新
- 在协程中使用异步操作，避免阻塞 UI
- 批量操作数据，提高效率

希望这个封装能够帮助您更方便地使用 DataStore，让您的应用数据管理更加简单高效！