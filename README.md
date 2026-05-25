# DeepSeek Android APP 开发记录

> 一个兼容 Android 4.4.2 的 DeepSeek 客户端开发历程

---

## 项目概述

| 项目 | 说明 |
|------|------|
| 目标 | 在 Android 4.4.2 设备上使用 DeepSeek AI |
| 技术方案 | WebView 封装 + 腾讯 X5 内核 |
| 开发方式 | GitHub Actions 自动构建 |
| 最终状态 | ✅ 构建成功，待测试 |

---

## 开发历程

### 第一阶段：基础版本（系统 WebView）

**方案**：使用 Android 原生 WebView 封装 DeepSeek 网页

**问题**：
- Android 4.4.2 系统 WebView 版本太旧（Chrome 30）
- 无法加载现代网页，白屏
- 不支持 TLS 1.2 加密协议

**结果**：❌ 失败

---

### 第二阶段：CrossWalk 内核

**方案**：使用 CrossWalk 第三方 WebView 内核

**尝试过程**：
1. 添加 CrossWalk Maven 仓库
2. 尝试多个版本：`23.53.589.4`、`b84dcb2467`
3. 修改依赖配置

**问题**：
- CrossWalk 官方 Maven 仓库已下线
- JitPack 上版本不可用
- 手动下载 AAR 文件复杂

**结果**：❌ 失败（依赖无法解决）

---

### 第三阶段：腾讯 X5 内核（最终方案）

**方案**：使用腾讯 TBS X5 内核

**优势**：
- ✅ Maven Central 官方仓库可用
- ✅ 国内访问稳定
- ✅ 自动下载内核，无需手动配置
- ✅ 兼容 Android 4.4+
- 

**依赖配置**：
```gradle
// build.gradle dependencies { implementation 'com.tencent.tbs:tbssdk:44286' }

```java
// MainActivity.java import com.tencent.smtt.sdk.WebView;

WebView webView = new WebView(this); webView.loadUrl("https://chat.deepseek.com/");


---

## 下载安装

1. 进入 Actions 页面
2. 下载 `DeepSeek-App-Debug.zip`
3. 解压安装 APK

---

## 作者

- GitHub: [SZCCD](https://github.com/SZCCD)
- 设备: LG-F200K (Android 4.4.2)
