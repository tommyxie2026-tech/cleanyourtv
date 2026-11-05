# Android 6+ Compatibility Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Deliver a reproducible API 23+ launcher APK with real Android 6 verification.
**Architecture:** Keep the existing Java launcher. Backfill Java standard APIs and guard newer Android framework calls at their boundaries.
**Tech Stack:** Java 17, AGP 8.5.2, Android SDK 34, core library desugaring, JUnit.
**Spec:** ../specs/2026-11-05-android6-compatibility-design.md

## Global Constraints
- minSdk 23; targetSdk 34; preserve current UI and functionality where supported.
- No root, no firmware changes, no disabling stock Home, no changes to existing Ice Box states.
- Work on local branch codex/android6-compat; no publication or push.
- Deliver APK, patch, and actual validation results in outputs.

## Review Focus
- API 23 startup and resource inflation: lint plus real H2 launch.
- Missing default-network APIs: version-dependent callback registration and cleanup.
- Android 6 has no locked-user phase: guard all unlock and accessibility APIs.
- Dates and files on API 23: Java library backfill and UTF-8 caption read tests.
- Modern Android behavior retained: lint and existing launcher tests; state any untested devices.

## Task 1: Establish toolchain and baseline
- [x] Prepare JDK 17 and SDK 34/build-tools 34 in the task workspace.
- [x] Build unmodified launcher and run existing testReleaseUnitTest/lintRelease.
- [x] Record baseline results and source revision in ledger.

## Task 2: Implement compatibility
Files: launcher/app/build.gradle; MainActivity.java; HomeKeyService.java; GuardKeep.java; GuardRevival.java; WallpaperCaptions.java; AndroidManifest.xml; targeted tests.
- [x] Add file-read regression tests with UTF-8, missing and oversized files; run before implementation.
- [x] Set minSdk 23 and enable supported Java API desugaring; run lint to expose framework incompatibilities.
- [x] Add API 23 network fallback and matching lifecycle cleanup; guard all missing framework APIs identified by lint.
- [x] Add conventional LAUNCHER entry without disabling or replacing stock Home.
- [x] Run targeted and existing tests, lintRelease, assembleRelease; inspect APK metadata and signature.

## Task 3: Device verification and delivery
- [x] Install APK alongside existing H2 apps; start as an ordinary app.
- [x] Check current activity, fatal exceptions and app listing; test native settings and injected Home return.
- [ ] User verification of physical remote directions, OK, Back and Home; actual reboot-to-Home check.
- [x] Confirm stock Home still works and the five Ice Box states remain unchanged.
- [x] Review full diff; fix material findings with tests; copy APK and patch to outputs with validation notes and SHA256.

## Execution ledger
- User explicitly approved implementation and APK verification. Continue inline without another plan approval.
- Fresh clone is already isolated from user projects; use its dedicated feature branch instead of another checkout.

- Baseline fc148d5: 314 unit tests passed; lint/build passed; original APK rejected by API 23 with INSTALL_FAILED_OLDER_SDK.
- API 23 compatibility audit initially exposed 104 lint errors. Final release: zero lint errors, 112 warnings and 96 informational findings; 319 JVM tests passed.
- Review completed once; addressed missing legacy TV app discovery, stable API 23 boot identity, multi-network validation updates, and accessibility step-out refusal handling.
- H2 Android 6/API 23, firmware v1.13.41: seven self-instrumented real-framework tests passed. Optimized release installed, launched, lists apps, opens native settings, redirects stock Home, and handles injected Home from settings. No fatal launcher exception during final redirect check.
- H2 has a framework TV input manager without a working backend: treat it as unavailable. Original input controls remain available.
- H2 also rejects package-scoped HOME intents: Android 6/7 helper uses explicit MainActivity without HOME category. Regression verified on H2.
- User explicitly authorized persistent Home helper and default startup. Enabled only this helper in addition to the existing voice service; configured stock_home_redirect in app external config. All five pre-existing Ice Box hidden states confirmed unchanged.
- Full Python suite: 2903 tests, 67 skipped, one environment-dependent DNS failure in test_adb_closed_address_words (reserved tv.invalid unexpectedly resolves in this environment). Do not report a fully green repository suite.
- Real remote and actual reboot verification remain pending user feedback. Boot redirect is configured, not yet proven across a reboot. Android 7 and newer devices have not been physically tested in this run.
- Preserve local branch and deliver APK plus source patch; do not push or publish. APK exceeds upstream 576 KiB gate due Java library backfill; gate and CI remain unchanged.
