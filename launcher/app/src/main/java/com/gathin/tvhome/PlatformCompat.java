package com.gathin.tvhome;

import android.content.Context;
import android.content.res.Resources;
import android.media.tv.TvInputInfo;
import android.os.Build;
import android.os.UserManager;
import java.util.Locale;

/** Framework capabilities that did not exist on Android 6. */
final class PlatformCompat {
    private PlatformCompat() { }

    static boolean userUnlocked(Context context) {
        // File-based encryption and USER_UNLOCKED were introduced together in Android 7.
        if (Build.VERSION.SDK_INT < 24) return true;
        UserManager users = context.getSystemService(UserManager.class);
        return users == null || users.isUserUnlocked();
    }

    @SuppressWarnings("deprecation")
    static Locale locale(Resources resources) {
        if (Build.VERSION.SDK_INT >= 24) return resources.getConfiguration().getLocales().get(0);
        return resources.getConfiguration().locale;
    }

    static boolean inputHidden(TvInputInfo input, Context context) {
        return Build.VERSION.SDK_INT >= 24 && input.isHidden(context);
    }

    static CharSequence customInputLabel(TvInputInfo input, Context context) {
        return Build.VERSION.SDK_INT >= 24 ? input.loadCustomLabel(context) : null;
    }
}
