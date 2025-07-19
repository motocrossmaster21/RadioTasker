package ch.motocrossmaster21.radiotasker;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

public class UsageMonitor {
    private static boolean appLaunched = false;

    public static void recordLaunch() {
        appLaunched = true;
    }

    public static void reset() {
        appLaunched = false;
    }

    public static boolean isAppRunning(Context context) {
        if (!appLaunched) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            UsageEvents events = usm.queryEvents(time - 1000 * 5, time);
            UsageEvents.Event event = new UsageEvents.Event();
            String packageName = SharedPrefsUtil.getPackageName(context);
            while (events.hasNextEvent()) {
                events.getNextEvent(event);
                if (TextUtils.equals(event.getPackageName(), packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
