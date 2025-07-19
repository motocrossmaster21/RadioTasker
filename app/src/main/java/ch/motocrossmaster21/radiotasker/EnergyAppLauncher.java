package ch.motocrossmaster21.radiotasker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class EnergyAppLauncher {
    public static void launchApp(Context context) {
        String packageName = SharedPrefsUtil.getPackageName(context);
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            UsageMonitor.recordLaunch();
        }
    }
}
