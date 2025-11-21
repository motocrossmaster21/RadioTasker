package ch.motocrossmaster21.radiotasker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class UsageMonitorTest {
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        UsageMonitor.reset();
    }

    @Test
    public void testRecordLaunchSetsRunningFlag() {
        UsageMonitor.recordLaunch();
        SharedPrefsUtil.setPackageName(context, "test.app");
        assertTrue(UsageMonitor.isAppRunning(context));
    }

    @Test
    public void testResetClearsFlag() {
        UsageMonitor.recordLaunch();
        UsageMonitor.reset();
        assertFalse(UsageMonitor.isAppRunning(context));
    }
}
