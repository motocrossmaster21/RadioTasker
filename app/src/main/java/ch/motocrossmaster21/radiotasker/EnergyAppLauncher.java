package ch.motocrossmaster21.radiotasker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import ch.motocrossmaster21.radiotasker.SharedPrefsUtil;
import ch.motocrossmaster21.radiotasker.CompanionManager;

/**
 * Helper for launching the configured radio app when the companion device is connected.
 */

public class EnergyAppLauncher {
    private static final String TAG = "EnergyLauncher";

    public static void launchApp(Context context) {
        String packageName = SharedPrefsUtil.getPackageName(context);
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!CompanionManager.isDeviceAssociated(context, SharedPrefsUtil.getDeviceName(context))) {
                Log.w(TAG, "Launching without companion association may be blocked");
            }
            try {
                context.startActivity(intent);
                Log.d(TAG, "Launched package " + packageName);
                UsageMonitor.recordLaunch();
            } catch (Exception ex) {
                Log.e(TAG, "Failed to launch package " + packageName, ex);
            }
        } else {
            Log.e(TAG, "Launch intent for " + packageName + " not found");
        }
    }
}
