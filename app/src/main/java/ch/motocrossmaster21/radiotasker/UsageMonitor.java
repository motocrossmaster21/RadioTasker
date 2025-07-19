package ch.motocrossmaster21.radiotasker;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class UsageMonitor {
    private static boolean appLaunched = false;
    private static final String TAG = "UsageMonitor";

    public static void recordLaunch() {
        Log.d(TAG, "App launch recorded");
        appLaunched = true;
    }

    public static void reset() {
        Log.d(TAG, "Usage monitor reset");
        appLaunched = false;
    }

    public static boolean isAppRunning(Context context) {
        if (!appLaunched) {
            Log.d(TAG, "App not launched yet");
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            UsageEvents events = usm.queryEvents(time - 1000 * 5, time);
            UsageEvents.Event event = new UsageEvents.Event();
            String packageName = SharedPrefsUtil.getPackageName(context);
            while (events.hasNextEvent()) {
                events.getNextEvent(event);
                if (TextUtils.equals(event.getPackageName(), packageName)) {
                    Log.d(TAG, "Detected running package: " + packageName);
                    return true;
                }
            }
        }
        Log.d(TAG, "Target package not found in recent usage events");
        return false;
    }
}
