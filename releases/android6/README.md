# Android 6+ APK

[Download CleanYourTV 3.4-android6.1](CleanYourTV-Android6-v3.4.1.apk) · [SHA256](CleanYourTV-Android6-v3.4.1.apk.sha256)

This APK is included at the repository owner's explicit request. It updates the H2-tested release with
an opt-in, remote-compatible HDMI 1 / HDMI 2 menu. The user's previous version confirmed physical remote
controls and automatic Home startup after a real reboot on 2026-11-06; the new HDMI menu still needs
physical remote/picture/audio confirmation.

- Package: `com.gathin.tvhome`; versionCode 15; minSdk 23; targetSdk 34.
- Size: 816,156 bytes. SHA256: `a7e0337d619a73071b9adb33c3250d7b488a2cb53a026836d5ed82c77df12883`.
- Signed with the same sideload debug configuration; v1/v2 signature verification passed.
  Subsequent updates must use the same signing key. No private signing key is included.
- 321 Java unit tests passed; lint has zero errors and 113 warnings.
- Nine H2 device tests cover compatibility, HDMI native intent resolution and picker navigation.
- The optimized release switched the native hardware input to HDMI 1 and HDMI 2 from the launcher's own
  app UID; Home return passed for both ports. Actual HDMI picture/audio still needs a sender-device test.
- AirPlay uses the installed receiver, shown as “AirPlay 投屏”. Discovery on the LAN was confirmed.
  User must personally choose whether to accept the receiver's first-run terms; actual mirroring is pending.
- The prior full Python suite ran 2903 tests: 67 skipped and one environment-dependent DNS failure for
  the reserved `tv.invalid` hostname. Android 7 and newer devices were not physically tested here.
- The APK exceeds upstream's 576 KiB size gate because of Java API backfilling. CI and that gate are unchanged.

[Source and build instructions](../../ANDROID6.md) · [H2 configuration](../../profiles/xgimi-h2/config.json)

The device-test runner is excluded from this release APK. Installing the APK alone does not enable
Home accessibility or change the stock launcher. Apply the H2 profile separately with the owner's consent.
The earlier [3.4-android6 APK](CleanYourTV-Android6-v3.4.apk) is retained for rollback.
