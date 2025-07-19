package ch.motocrossmaster21.radiotasker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class EnergyAppLauncherTest {
    @Mock
    Context context;
    @Mock
    PackageManager pm;
    @Mock
    Intent launchIntent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(context.getPackageManager()).thenReturn(pm);
        Mockito.when(context.getApplicationContext()).thenReturn(context);
    }

    @Test
    public void testLaunchAppWhenIntentExists() {
        Mockito.when(pm.getLaunchIntentForPackage("pkg")).thenReturn(launchIntent);
        Mockito.when(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)).thenReturn(launchIntent);
        SharedPrefsUtil.setPackageName(context, "pkg");

        EnergyAppLauncher.launchApp(context);

        Mockito.verify(context).startActivity(launchIntent);
    }

    @Test
    public void testLaunchAppWhenIntentMissing() {
        Mockito.when(pm.getLaunchIntentForPackage("pkg")).thenReturn(null);
        SharedPrefsUtil.setPackageName(context, "pkg");

        EnergyAppLauncher.launchApp(context);

        Mockito.verify(context, Mockito.never()).startActivity(Mockito.any());
    }
}
