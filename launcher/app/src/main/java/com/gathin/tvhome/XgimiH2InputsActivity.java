package com.gathin.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

/** Opt-in H2 picker: the old vendor panel handles ENTER, but the Bluetooth remote sends DPAD_CENTER. */
public class XgimiH2InputsActivity extends Activity {
    private static final ComponentName PLAYER = new ComponentName(
            "com.mstar.tv.tvplayer.ui", "com.mstar.tv.tvplayer.ui.RootActivity");
    private AlertDialog dialog;

    static int sourceForPort(int port) {
        if (port != 1 && port != 2) throw new IllegalArgumentException("H2 has HDMI 1 and HDMI 2 only");
        return 22 + port;
    }

    static Intent inputIntent(int port) {
        // This is the native picker/player contract, verified against the H2 firmware.
        return new Intent("com.mstar.android.intent.action.START_TV_PLAYER")
                .setComponent(PLAYER)
                .putExtra("inputSrc", sourceForPort(port))
                .putExtra("inputAntennaType", 0)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    static boolean available(Context context) {
        try {
            ActivityInfo info = context.getPackageManager().getActivityInfo(PLAYER, 0);
            return info.exported && info.enabled && info.applicationInfo.enabled;
        } catch (PackageManager.NameNotFoundException unavailable) {
            return false;
        }
    }

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(LauncherLocale.wrap(base));
    }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!available(this)) {
            Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.inputs)
                .setItems(new String[] {"HDMI 1", "HDMI 2"}, (ignored, which) -> {
                    try {
                        startActivity(inputIntent(which + 1));
                    } catch (android.content.ActivityNotFoundException | SecurityException unavailable) {
                        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, (ignored, which) -> finish())
                .setOnCancelListener(ignored -> finish())
                .create();
        dialog.show();
        choices().requestFocus();
        choices().setSelection(0);
    }

    ListView choices() { return dialog.getListView(); }

    @Override protected void onDestroy() {
        if (dialog != null) dialog.dismiss();
        super.onDestroy();
    }
}
