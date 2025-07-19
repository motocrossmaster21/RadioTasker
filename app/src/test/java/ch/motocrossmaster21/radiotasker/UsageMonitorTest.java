package ch.motocrossmaster21.radiotasker;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsageMonitorTest {
    @Mock
    Context context;
    @Mock
    UsageStatsManager usm;
    @Mock
    UsageEvents events;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(context.getSystemService(Context.USAGE_STATS_SERVICE)).thenReturn(usm);
    }

    @Test
    public void testRecordAndReset() {
        UsageMonitor.recordLaunch();
        assertFalse(UsageMonitor.isAppRunning(context));
        UsageMonitor.reset();
        assertFalse(UsageMonitor.isAppRunning(context));
    }

    @Test
    public void testIsAppRunningTrue() {
        UsageMonitor.recordLaunch();
        Mockito.when(usm.queryEvents(Mockito.anyLong(), Mockito.anyLong())).thenReturn(events);
        UsageEvents.Event event = new UsageEvents.Event();
        event.setPackageName("test.app");
        Mockito.when(events.hasNextEvent()).thenReturn(true, false);
        Mockito.when(events.getNextEvent(Mockito.any())).thenAnswer(invocation -> {
            UsageEvents.Event e = invocation.getArgument(0);
            e.setPackageName("test.app");
            return null;
        });
        SharedPrefsUtil.setPackageName(context, "test.app");
        assertTrue(UsageMonitor.isAppRunning(context));
    }
}
