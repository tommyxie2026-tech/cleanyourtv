# Android 6+ compatibility fork

This branch ports the existing launcher to Android 6.0 (API 23) and newer. The upstream project targets
Android 8.0; the original instructions below the fork notice remain upstream documentation.

The UI and features are retained where the framework supports them. Java time/collection APIs are
backfilled using `desugar_jdk_libs:2.0.3`. Android 6/7 use system sans-serif fonts and software wallpaper
bitmaps; Android 8+ retain the bundled fonts and hardware bitmap path. On Android 6 the input menu uses
the input's regular label because custom labels and hidden-input queries are unavailable.

Network callbacks and unlock checks use version-aware paths. On Android 6/7, a stable installed-package
snapshot also detects updates missed by package broadcasts, rather than only comparing app counts.

## Build

Use JDK 17, Android SDK platform 34 and build-tools 34.0.0. Set `ANDROID_HOME` or write `sdk.dir` in
the gitignored `launcher/local.properties`. Then run:

```sh
cd launcher
./gradlew --no-daemon testReleaseUnitTest lintRelease assembleRelease assembleReleaseAndroidTest
```

The application is `launcher/app/build/outputs/apk/release/app-release.apk`.
The device tests are under `launcher/app/build/outputs/apk/androidTest/release/`.
The release APK uses the existing debug signing configuration for sideloading, not a production signing
key. Keep that key when building subsequent updates. Java library backfilling increases APK size: this
fork does not claim to satisfy the upstream 576 KB size gate and does not change that gate or CI.

## Install and test

```sh
adb install -r launcher/app/build/outputs/apk/release/app-release.apk
adb shell am start -n com.gathin.tvhome/.MainActivity
```

The ordinary app entry lets older AOSP projector launchers list this app. Installation does not grant
root, enable accessibility helpers, change the device owner, or disable the original launcher.
Do not disable stock Home to test compatibility. H2 firmware may continue opening its stock launcher
when Home is pressed; OS compatibility and default-Home takeover are separate issues.

Device tests cover Java dates, fonts, caption files, guard preferences, and startup/recreation/destruction.
The implementation must be tested on a real API 23 device; newer API versions require their own test
results before being described as verified. Keep package/data backups and existing Ice Box settings.

## Design and implementation record

- [Approved design](docs/superpowers/specs/2026-11-05-android6-compatibility-design.md)
- [Implementation plan and ledger](docs/superpowers/plans/2026-11-05-android6-compatibility.md)
