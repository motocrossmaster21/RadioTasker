package ch.motocrossmaster21.radiotasker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class EnergyAppLauncher {
    private static final String TAG = "EnergyAppLauncher";

    public static void launchApp(Context context) {
        String packageName = SharedPrefsUtil.getPackageName(context);
        Log.d(TAG, "Attempting to launch package: " + packageName);

        if (UsageMonitor.isAppRunning(context)) {
            Log.d(TAG, "Target app already running, skipping launch");
            return;
        }

        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            UsageMonitor.recordLaunch();
            Log.i(TAG, "App launched successfully");
        } else {
            Log.w(TAG, "No launch intent found for package: " + packageName);
        }
    }
}
