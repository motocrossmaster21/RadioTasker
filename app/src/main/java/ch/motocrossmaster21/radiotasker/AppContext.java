package ch.motocrossmaster21.radiotasker;

import android.app.Application;
import android.content.Context;

public class AppContext extends Application {
    private static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.applicationContext = getApplicationContext();
    }

    public static Context get() {
        return applicationContext;
    }
}
