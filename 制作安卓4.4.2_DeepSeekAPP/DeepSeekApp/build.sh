#!/bin/bash
# DeepSeek APP 快速打包脚本

echo "================================"
echo "  DeepSeek APP 快速打包工具"
echo "================================"
echo ""

# 检查Gradle Wrapper
if [ ! -f "gradlew" ]; then
    echo "正在生成 Gradle Wrapper..."
    gradle wrapper
fi

# 赋予执行权限
chmod +x gradlew

echo "开始构建 APK..."
echo ""

# 清理并构建
./gradlew clean assembleRelease

# 检查构建结果
if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    echo ""
    echo "================================"
    echo "  构建成功！"
    echo "================================"
    echo ""
    echo "APK 文件位置："
    echo "app/build/outputs/apk/release/app-release.apk"
    echo ""
    
    # 显示APK信息
    APK_SIZE=$(du -h "app/build/outputs/apk/release/app-release.apk" | cut -f1)
    echo "APK 大小：$APK_SIZE"
    echo ""
    
    # 询问是否安装
    read -p "是否立即安装到连接的设备？(y/n): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "正在安装..."
        adb install -r app/build/outputs/apk/release/app-release.apk
        echo "安装完成！"
    fi
else
    echo ""
    echo "================================"
    echo "  构建失败！"
    echo "================================"
    echo ""
    echo "请检查错误信息并重试。"
    echo "常见问题："
    echo "1. 未安装 Android SDK"
    echo "2. SDK 版本不匹配"
    echo "3. Gradle 下载失败"
fi
