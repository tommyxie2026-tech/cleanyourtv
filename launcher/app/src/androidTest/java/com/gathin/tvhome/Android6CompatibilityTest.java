package com.gathin.tvhome;

import static org.junit.Assert.*;

import android.content.Context;
import android.graphics.Typeface;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Runs against the actual framework, including API 23, rather than JVM Android stubs. */
@RunWith(AndroidJUnit4.class)
public class Android6CompatibilityTest {
    private Context context() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test public void h2HdmiChoicesResolveToTheNativePlayer() throws Exception {
        org.junit.Assume.assumeTrue(XgimiH2InputsActivity.available(context()));
        for (int port : new int[] {1, 2}) {
            android.content.Intent intent = XgimiH2InputsActivity.inputIntent(port);
            assertNotNull(context().getPackageManager().resolveActivity(intent, 0));
            assertEquals("com.mstar.tv.tvplayer.ui", intent.getComponent().getPackageName());
            assertEquals(port == 1 ? 23 : 24, intent.getIntExtra("inputSrc", -1));
        }
    }

    @Test public void h2HdmiMenuAcceptsRemoteNavigationAndBackWithoutSwitching() {
        org.junit.Assume.assumeTrue(XgimiH2InputsActivity.available(context()));
        try (ActivityScenario<XgimiH2InputsActivity> scenario = ActivityScenario.launch(XgimiH2InputsActivity.class)) {
            scenario.onActivity(activity -> assertEquals(0, activity.choices().getSelectedItemPosition()));
            InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_DPAD_DOWN);
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(activity -> assertEquals(1, activity.choices().getSelectedItemPosition()));
            InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK);
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            // finish() pauses immediately; H2 completes destruction after its closing animation.
            long deadline = android.os.SystemClock.uptimeMillis() + 5000;
            while (scenario.getState() != androidx.lifecycle.Lifecycle.State.DESTROYED
                    && android.os.SystemClock.uptimeMillis() < deadline) {
                android.os.SystemClock.sleep(50);
            }
            assertEquals(androidx.lifecycle.Lifecycle.State.DESTROYED, scenario.getState());
        }
    }

    @Test public void televisionInputBackendMayBeAbsent() {
        // A framework manager backed by a null ITvInputManager must not crash an AOSP projector.
        android.media.tv.TvInputManager inputs = PlatformCompat.tvInputManager(context());
        if (inputs != null) assertNotNull(inputs.getTvInputList());
    }

    @Test public void homeHelperIntentResolvesOnLegacyProjector() {
        android.content.Intent intent = HomeKeyService.homeIntent(context(), false);
        assertNotNull(context().getPackageManager().resolveActivity(intent,
                android.content.pm.PackageManager.MATCH_DEFAULT_ONLY));
        if (android.os.Build.VERSION.SDK_INT < 26) {
            assertEquals(new android.content.ComponentName(context(), MainActivity.class), intent.getComponent());
            assertFalse(intent.hasCategory(android.content.Intent.CATEGORY_HOME));
        }
    }

    @Test public void bundledAndSystemFontsCanBeLoaded() {
        assertNotNull(UiFont.get(context(), R.string.font_body));
        assertNotNull(UiFont.get(context(), R.string.font_medium));
        assertNotNull(Typeface.create("sans-serif", Typeface.NORMAL));
    }

    @Test public void guardPreferencesDoNotRequireAndroid7UnlockApis() {
        assertTrue(PlatformCompat.userUnlocked(context()));
    }

    @Test public void legacyBootIdentityIsAvailableAndStable() {
        int boot = OpenOnStart.boot(context());
        assertTrue("A usable boot identity is required for boot actions", boot >= 0);
        assertEquals(boot, OpenOnStart.boot(context()));
    }

    @Test public void captionFilesReadUtf8AndMissingFilesAreEmpty() throws Exception {
        File folder = new File(context().getCacheDir(), "compat-captions");
        assertTrue(folder.isDirectory() || folder.mkdirs());
        File captions = new File(folder, WallpaperCaptions.FILE_NAME);
        try {
            try (FileOutputStream out = new FileOutputStream(captions)) {
                out.write("moon.jpg\t月亮 · Moon\n".getBytes(StandardCharsets.UTF_8));
            }
            Map<String, String> names = WallpaperCaptions.read(folder);
            assertEquals("月亮 · Moon", names.get("moon.jpg"));
            assertTrue(captions.delete());
            assertTrue(WallpaperCaptions.read(folder).isEmpty());
            try (FileOutputStream out = new FileOutputStream(captions)) {
                out.write(new byte[256 * 1024 + 1]);
            }
            assertTrue(WallpaperCaptions.read(folder).isEmpty());
        } finally {
            captions.delete();
            folder.delete();
        }
    }

    @Test public void launcherCanStartRecreateAndReleaseNetworkCallbacks() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.btnSettings));
                assertNotNull(activity.findViewById(R.id.btnWifi));
                // Starting the actual activity exercises its desugared date calculations. Keep the
                // test APK free of its own partial LocalDate implementation shadowing the app's one.
                assertNotNull(activity.findViewById(R.id.clock));
            });
            scenario.recreate();
            scenario.onActivity(activity -> assertFalse(activity.isFinishing()));
        }
    }
}
