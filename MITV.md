# GlassTV MiTV 定制版

基于 [jichun29/glasstv](https://github.com/jichun29/glasstv) v4.0.0（MIT）的私人定制版，运行在
小米电视 MIUI TV 23.9.6 / Android 9 / 1080p 上。

## 这版改了什么

- **包名**：使用 `com.ts.launcher` 自编译，桌面 Activity 才能成为这台电视的 Home。
- **待机防杀**：`AndroidManifest.xml` 里 `<application android:process=":home">`，避开 MIUI
  待机时对普通第三方进程的强杀；唤醒后仍回到本桌面。
- **应用库**：`app_library=true`，用 `extra_apps` 收录没有 LAUNCHER 入口的系统应用；
  应用库共 22 个应用，A/B 两类入口都验证可启动。
- **统一图标磁贴**：`uniform_app_icons=true`，忽略应用自带 banner，全部显示应用图标。
- **磁贴布局**：`columns=5`，72dp 图标，名称放在磁贴内图标下方，不再有 16:9 大黑框。
- **焦点效果**：独立的 `app_icon_ring` 浮层（外层细白圈 + 内层 2dp 白框 + 白色雾面），
  焦点框只包住图标；旧的整张卡片玻璃焦点层在统一图标模式下隐藏。
- **Dock**：半透明玻璃条，12% 白色叠加，背景和聚焦应用的氛围光可以透出来。
- **屏保**：桌面闲置 5 分钟进入时钟屏保；进屏保前再校验一次「桌面在前台且有窗口焦点」，
  并在 `onStop` 里清一次计时器，避免计时器残留时盖在正在播放的视频上（2026-10-25 修）。
- **去广告**：通过外部 ADB 脚本停用包、改设置完成，不在本仓库内。

## 构建

需要 JDK 17+ 与 Android SDK（platform 34 / build-tools 34.0.0）。

```powershell
$env:JAVA_HOME = "<JDK 路径>"
$env:ANDROID_HOME = "<Android SDK 路径>"

gradle -p launcher assembleRelease `
  "-PlauncherApplicationId=com.ts.launcher" `
  --no-daemon
```

输出：`launcher/app/build/outputs/apk/release/app-release.apk`

## 安装

```powershell
adb connect <电视IP>:5555
adb install -r launcher/app/build/outputs/apk/release/app-release.apk
```

升级时必须使用同一个 keystore（`launcher/keystore/cleanyourtv.keystore`，配置见
`launcher/app/build.gradle`），否则应用库和首页排序会丢失。

## 注意

- 本机没有 root；`/data/user/0/com.ts.launcher/files/library_apps.txt` 无法直接读取。
- 不要设置 `Settings.Global debug_str_back_to_home = 0`，那会破坏 MIUI 的待机回桌面逻辑。
- 旧版 `com.ts.guard` 守护已经移除，待机问题由 `:home` 进程名解决。
- `cleanyourtv.keystore` 是上游项目自带的开发签名，密码也在上游源码中公开；不要用它签
  发布到应用商店的包。
