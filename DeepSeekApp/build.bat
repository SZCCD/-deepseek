@echo off
REM DeepSeek APP 快速打包脚本 (Windows)

echo ================================
echo   DeepSeek APP 快速打包工具
echo ================================
echo.

REM 检查Gradle Wrapper
if not exist "gradlew.bat" (
    echo 正在生成 Gradle Wrapper...
    call gradle wrapper
)

echo 开始构建 APK...
echo.

REM 清理并构建
call gradlew.bat clean assembleRelease

REM 检查构建结果
if exist "app\build\outputs\apk\release\app-release.apk" (
    echo.
    echo ================================
    echo   构建成功！
    echo ================================
    echo.
    echo APK 文件位置：
    echo app\build\outputs\apk\release\app-release.apk
    echo.
    
    REM 显示APK大小
    for %%A in ("app\build\outputs\apk\release\app-release.apk") do echo APK 大小：%%~zA 字节
    echo.
    
    REM 询问是否安装
    set /p INSTALL="是否立即安装到连接的设备？(y/n): "
    if /i "%INSTALL%"=="y" (
        echo 正在安装...
        adb install -r app\build\outputs\apk\release\app-release.apk
        echo 安装完成！
    )
) else (
    echo.
    echo ================================
    echo   构建失败！
    echo ================================
    echo.
    echo 请检查错误信息并重试。
    echo 常见问题：
    echo 1. 未安装 Android SDK
    echo 2. SDK 版本不匹配
    echo 3. Gradle 下载失败
)

pause
