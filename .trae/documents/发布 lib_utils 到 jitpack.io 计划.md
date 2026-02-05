# 发布 lib_utils 到 jitpack.io 计划

## 项目分析
- **项目结构**：Android 项目，包含 `app` 和 `lib_utils` 模块
- **lib_utils**：Android 库模块，包含多个工具类（argument、datastore、event、permission、pictureselector）
- **构建配置**：使用 Kotlin，依赖 AndroidX、Coroutines、PictureSelector、Coil、XXPermissions、DataStore
- **Git 状态**：已初始化 Git 仓库，当前在 `pure` 分支，工作目录干净

## 发布步骤

### 1. 准备工作
- 确保 `lib_utils` 模块配置正确，适合作为独立库发布
- 检查依赖项，确保没有不必要的依赖
- 确保代码质量，运行测试

### 2. GitHub 仓库设置
- 在 GitHub 上创建新仓库
- 将本地代码推送到 GitHub
- 确保仓库设置正确（公开访问）

### 3. 版本管理
- 在 GitHub 上创建 release 或 tag
- 遵循语义化版本规范（如 v1.0.0）

### 4. JitPack 配置
- 访问 https://jitpack.io
- 注册并登录
- 搜索并添加 GitHub 仓库
- 触发构建
- 验证构建是否成功

### 5. 依赖使用说明
- 提供在其他 Android 项目中添加依赖的步骤
- 示例：在 `build.gradle` 中添加仓库和依赖

## 技术要点
- 确保 `lib_utils` 模块可以独立构建
- 验证所有依赖项在 jitpack 构建环境中可用
- 提供清晰的使用文档

## 预期结果
- lib_utils 成功发布到 jitpack.io
- 其他 Android 项目可以通过 Gradle 依赖直接引入使用