# Android 6+ APK

[Download CleanYourTV 3.4-android6](CleanYourTV-Android6-v3.4.apk) · [SHA256](CleanYourTV-Android6-v3.4.apk.sha256)

This APK is included at the repository owner's explicit request. It is the same release installed and
verified on XGIMI H2, Android 6/API 23, firmware v1.13.41. The user confirmed physical remote controls
and automatic Home startup after a real reboot on 2026-11-06.

- Package: `com.gathin.tvhome`; versionCode 14; minSdk 23; targetSdk 34.
- Size: 815,496 bytes. SHA256: `3ada1700111cd34725a77de26bbbfa88cdf1e168487699de56ca86e3eda194c0`.
- Signed with the existing sideload debug configuration; v1/v2 signature verification passed.
  Subsequent updates must use the same signing key. No private signing key is included.
- 319 Java unit tests and seven H2 device tests passed; lint has zero errors and 112 warnings.
- The full Python suite ran 2903 tests: 67 skipped and one environment-dependent DNS failure for
  the reserved `tv.invalid` hostname. Android 7 and newer devices were not physically tested here.
- Android 6/7 use system fonts and software wallpaper bitmaps. H2's incomplete TV input backend
  cannot populate the built-in HDMI list; the projector's native input controls remain available.
- The APK exceeds upstream's 576 KiB size gate because of Java API backfilling. CI and that gate are unchanged.

[Source and build instructions](../../ANDROID6.md). The device-test runner is excluded from this release APK.
Installing the APK alone does not enable the Home accessibility helper or change the stock launcher.
