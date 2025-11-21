package ch.motocrossmaster21.radiotasker;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class SharedPrefsUtilTest {
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        // ensure a clean slate for each test run
        context.getSharedPreferences("RadioTaskerPrefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    @Test
    public void testDefaultValues() {
        assertEquals("VW BT 6485", SharedPrefsUtil.getDeviceName(context));
        assertEquals("radioenergy.app", SharedPrefsUtil.getPackageName(context));
    }

    @Test
    public void testSetValues() {
        SharedPrefsUtil.setDeviceName(context, "TestDevice");
        SharedPrefsUtil.setPackageName(context, "test.app");
        assertEquals("TestDevice", SharedPrefsUtil.getDeviceName(context));
        assertEquals("test.app", SharedPrefsUtil.getPackageName(context));
    }
}
