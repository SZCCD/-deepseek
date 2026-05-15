# DeepSeek 极简版 APP

一个极简的DeepSeek客户端应用，通过WebView封装DeepSeek官方网页，兼容 Android 4.4.2 及以上系统。

## 项目特点

- ✅ **极简设计**：仅一个Activity，代码精简
- ✅ **兼容性强**：支持 Android 4.4.2 (API 19) 及以上
- ✅ **无需API Key**：直接使用DeepSeek网页版
- ✅ **登录状态保持**：自动保存Cookie
- ✅ **历史记录**：由DeepSeek网页自动管理
- ✅ **上下文记忆**：由DeepSeek网页自动处理

## 项目结构

```
DeepSeekApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/deepseek/app/
│   │   │   └── MainActivity.java      # 主Activity
│   │   ├── res/
│   │   │   ├── drawable/              # 应用图标
│   │   │   └── values/                # 字符串、颜色、样式
│   │   └── AndroidManifest.xml        # 清单文件
│   ├── build.gradle                   # 模块构建配置
│   └── proguard-rules.pro             # 混淆规则
├── build.gradle                       # 项目构建配置
├── settings.gradle                    # 项目设置
├── gradle.properties                  # Gradle属性
└── README.md                          # 本说明文档
```

## 打包方法

### 方法一：使用 Android Studio（推荐）

1. **安装 Android Studio**
   - 下载地址：https://developer.android.com/studio
   - 建议版本：Android Studio 3.6 或更高

2. **打开项目**
   - 启动 Android Studio
   - 选择 `File` → `Open`
   - 选择 `DeepSeekApp` 文件夹
   - 等待 Gradle 同步完成

3. **配置签名（可选但推荐）**
   - 选择 `Build` → `Generate Signed Bundle/APK`
   - 选择 `APK`
   - 创建或选择签名密钥
   - 选择 `release` 构建变体

4. **构建APK**
   - 方式A：`Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - 方式B：`Build` → `Generate Signed Bundle/APK` → 选择APK

5. **获取APK**
   - APK位置：`app/build/outputs/apk/release/app-release.apk`
   - 调试版：`app/build/outputs/apk/debug/app-debug.apk`

### 方法二：命令行打包

```bash
# 进入项目目录
cd DeepSeekApp

# Windows
gradlew.bat assembleRelease

# Mac/Linux
./gradlew assembleRelease

# APK输出位置
# app/build/outputs/apk/release/app-release.apk
```

### 方法三：在线打包服务

如果本地没有Android开发环境，可以使用在线打包服务：

1. **AppCenter (Microsoft)**
   - https://appcenter.ms
   - 上传项目源码，自动构建

2. **Bitrise**
   - https://www.bitrise.io
   - 支持免费构建

## 安装到手机

### 方式A：ADB安装

```bash
adb install app-release.apk
```

### 方式B：直接安装

1. 将APK文件传输到手机
2. 在手机上打开APK文件
3. 允许安装未知来源应用
4. 完成安装

## 使用说明

1. 打开应用后自动加载DeepSeek网页
2. 首次使用需要登录DeepSeek账号
3. 登录状态会自动保存，下次打开无需重新登录
4. 按"返回键"可以后退网页
5. 对话历史由DeepSeek网页自动保存

## 兼容性说明

### Android 4.4.2 特别说明

- WebView使用系统内置WebView
- 已禁用AndroidX迁移（保持兼容）
- 使用Java 7语法（兼容旧系统）
- 主题使用Holo风格（Android 4.4原生风格）

### 已知限制

- Android 4.4.2 的 WebView 版本较旧，某些现代网页特性可能不完全支持
- 建议在 Android 5.0+ 上使用以获得最佳体验

## 自定义修改

### 修改加载的网址

编辑 `MainActivity.java`，修改以下常量：

```java
private static final String DEEPSEEK_URL = "https://chat.deepseek.com/";
```

### 修改应用名称

编辑 `res/values/strings.xml`：

```xml
<string name="app_name">你的应用名</string>
```

### 修改应用图标

替换 `res/drawable/ic_launcher.xml` 文件

## 常见问题

### Q: 打包时提示 SDK 版本错误？
A: 在 Android Studio 中打开 `SDK Manager`，安装 Android SDK 28

### Q: 安装后打开白屏？
A: 检查网络连接，确保能访问 chat.deepseek.com

### Q: 登录状态丢失？
A: 清除应用数据后重新登录，Cookie会自动保存

### Q: 如何查看日志？
A: 使用 `adb logcat | grep DeepSeek` 查看应用日志

## 技术栈

- **开发语言**：Java
- **最低SDK**：Android 4.4 (API 19)
- **目标SDK**：Android 9 (API 28)
- **构建工具**：Gradle 5.6.4
- **Android Gradle Plugin**：3.6.4

## 许可证

本项目仅供学习交流使用，DeepSeek为其注册商标，归DeepSeek公司所有。
