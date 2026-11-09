# RushLab 30（Android）

一个本地线索记录器，用于 RushWallet Puzzle #30 的公开视频研究。

## 功能
- 研究概览
- 16 枚硬币观察记录
- 线索档案：事实 / 假设 / 待核实
- 本地保存
- 导出 JSON

## 安全设计
本项目不包含助记词猜测、私钥推导、密码爆破、钱包导入或资产转移功能。请只记录公开可观察的线索。

## 构建 APK
项目使用 Android Gradle Plugin 8.7.3、Gradle 8.9、JDK 17、Android SDK 35。
将整个项目上传到自己的 GitHub 仓库后，GitHub Actions 会自动构建 debug APK：
1. 新建一个空仓库。
2. 上传本项目全部文件。
3. 打开仓库的 Actions 页面，等待 `Build RushLab30 APK` 完成。
4. 在对应运行记录的 Artifacts 下载 `RushLab30-debug-apk`，解压后得到 `app-debug.apk`。
5. 在安卓手机上安装前，确认 APK 来自你自己的构建记录。

当前环境没有 Android SDK / Gradle Android 构建环境，因此这里提供的是可构建项目源码和自动构建工作流，不是已经编译好的 APK。
