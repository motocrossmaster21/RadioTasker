package ch.motocrossmaster21.radiotasker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
import android.app.ActivityManager;

import ch.motocrossmaster21.radiotasker.SharedPrefsUtil;
import ch.motocrossmaster21.radiotasker.CompanionManager;

/**
 * Helper for launching the configured radio app when the companion device is connected.
 */

public class EnergyAppLauncher {
    private static final String TAG = "EnergyLauncher";

    public static boolean launchApp(Context context) {
        String packageName = SharedPrefsUtil.getPackageName(context);
        Log.d(TAG, "Attempting to launch package: " + packageName);
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            Log.d(TAG, "Launch intent: " + intent.toUri(0));
            if (SharedPrefsUtil.shouldRestartApp(context)) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                if (am != null) {
                    try {
                        am.killBackgroundProcesses(packageName);
                        Log.d(TAG, "killBackgroundProcesses issued for " + packageName);
                    } catch (Exception ex) {
                        Log.w(TAG, "killBackgroundProcesses failed for " + packageName, ex);
                    }
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (!CompanionManager.isDeviceAssociated(context, SharedPrefsUtil.getDeviceName(context))) {
                Log.w(TAG, "Launching without companion association may be blocked");
            }
            try {
                context.startActivity(intent);
                Log.d(TAG, "Launched package " + packageName);
                Toast.makeText(context, "Launched: " + packageName, Toast.LENGTH_SHORT).show();
                UsageMonitor.recordLaunch();
                return true;
            } catch (Exception ex) {
                Log.e(TAG, "Failed to launch package " + packageName, ex);
                Toast.makeText(context, "Launch failed: " + packageName, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Launch intent for " + packageName + " not found");
            Toast.makeText(context, "Launch intent not found for " + packageName, Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
