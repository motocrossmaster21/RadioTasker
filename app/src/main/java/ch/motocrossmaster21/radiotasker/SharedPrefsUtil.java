package ch.motocrossmaster21.radiotasker;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtil {
    private static final String PREFS_NAME = "RadioTaskerPrefs";
    private static final String KEY_DEVICE_NAME = "deviceName";
    private static final String KEY_DEVICE_ADDRESS = "deviceAddress";
    private static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_RESTART_APP = "restartApp";

    public static String getDeviceName(Context context) {
        return getPrefs(context).getString(KEY_DEVICE_NAME, "VW BT 6485");
    }

    public static void setDeviceName(Context context, String name) {
        getPrefs(context).edit().putString(KEY_DEVICE_NAME, name).apply();
    }

    public static String getDeviceAddress(Context context) {
        return getPrefs(context).getString(KEY_DEVICE_ADDRESS, "");
    }

    public static void setDeviceAddress(Context context, String address) {
        getPrefs(context).edit().putString(KEY_DEVICE_ADDRESS, address).apply();
    }

    public static boolean shouldRestartApp(Context context) {
        return getPrefs(context).getBoolean(KEY_RESTART_APP, false);
    }

    public static void setShouldRestartApp(Context context, boolean restart) {
        getPrefs(context).edit().putBoolean(KEY_RESTART_APP, restart).apply();
    }

    public static String getPackageName(Context context) {
        return getPrefs(context).getString(KEY_PACKAGE_NAME, "radioenergy.app");
    }

    public static void setPackageName(Context context, String pkg) {
        getPrefs(context).edit().putString(KEY_PACKAGE_NAME, pkg).apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
