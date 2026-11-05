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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Runs against the actual framework, including API 23, rather than JVM Android stubs. */
@RunWith(AndroidJUnit4.class)
public class Android6CompatibilityTest {
    private Context context() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test public void dateCalculationsWorkWithoutPlatformJavaTime() {
        assertEquals("2024-02-29", LocalDate.of(2024, 2, 28).plusDays(1).toString());
        assertNotNull(LocalDate.now(ZoneId.systemDefault()));
    }

    @Test public void bundledAndSystemFontsCanBeLoaded() {
        assertNotNull(UiFont.get(context(), R.string.font_body));
        assertNotNull(UiFont.get(context(), R.string.font_medium));
        assertNotNull(Typeface.create("sans-serif", Typeface.NORMAL));
    }

    @Test public void guardPreferencesDoNotRequireAndroid7UnlockApis() {
        // The default is off; reading it must also work before USER_UNLOCKED existed.
        assertFalse(GuardKeep.offInStandby(context()));
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
            });
            scenario.recreate();
            scenario.onActivity(activity -> assertFalse(activity.isFinishing()));
        }
    }
}
