package ch.motocrossmaster21.radiotasker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowPackageManager;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class EnergyAppLauncherTest {
    private Application context;
    private ShadowApplication shadowApp;
    private ShadowPackageManager shadowPm;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        shadowApp = shadowOf(context);
        shadowPm = shadowOf(context.getPackageManager());
    }

    @Test
    public void testLaunchAppWhenIntentExists() {
        String pkg = "pkg";
        SharedPrefsUtil.setPackageName(context, pkg);
        registerLaunchableActivity(pkg);

        EnergyAppLauncher.launchApp(context);

        Intent started = shadowApp.getNextStartedActivity();
        assertNotNull(started);
        assertEquals(pkg, started.getComponent().getPackageName());
    }

    @Test
    public void testLaunchAppWhenIntentMissing() {
        String pkg = "pkg";
        SharedPrefsUtil.setPackageName(context, pkg);

        EnergyAppLauncher.launchApp(context);

        assertNull(shadowApp.getNextStartedActivity());
    }

    private void registerLaunchableActivity(String pkg) {
        Intent queryIntent = new Intent(Intent.ACTION_MAIN);
        queryIntent.setPackage(pkg);
        queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName = pkg;
        resolveInfo.activityInfo.name = "MainActivity";
        shadowPm.addResolveInfoForIntent(queryIntent, resolveInfo);
    }
}
