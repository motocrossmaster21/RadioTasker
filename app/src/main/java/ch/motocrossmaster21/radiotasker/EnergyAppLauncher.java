package ch.motocrossmaster21.radiotasker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.app.PendingIntent.CanceledException;

import ch.motocrossmaster21.radiotasker.SystemUtils;

public class EnergyAppLauncher {
    private static final String TAG = "EnergyAppLauncher";

    public static void launchApp(Context context) {
        String packageName = SharedPrefsUtil.getPackageName(context);
        Log.d(TAG, "Attempting to launch package: " + packageName);

        if (!SystemUtils.isPackageInstalled(context, packageName)) {
            Log.w(TAG, "Configured package not installed: " + packageName);
            return;
        }

        if (UsageMonitor.isAppRunning(context)) {
            Log.d(TAG, "Target app already running, skipping launch");
            return;
        }

        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                );
                pendingIntent.send();
                UsageMonitor.recordLaunch();
                Log.i(TAG, "App launched successfully via PendingIntent");
            } catch (CanceledException e) {
                Log.w(TAG, "PendingIntent canceled, falling back", e);
                context.startActivity(intent);
                UsageMonitor.recordLaunch();
                Log.i(TAG, "App launched successfully");
            }
        } else {
            Log.w(TAG, "No launch intent found for package: " + packageName);
        }
    }
}
