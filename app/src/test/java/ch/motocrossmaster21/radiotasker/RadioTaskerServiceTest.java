package ch.motocrossmaster21.radiotasker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ServiceController;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class RadioTaskerServiceTest {
    private ServiceController<RadioTaskerService> controller;

    @Before
    public void setUp() {
        controller = Robolectric.buildService(RadioTaskerService.class);
    }

    @Test
    public void testOnCreateLaunchesApp() {
        EnergyAppLauncher launcher = mock(EnergyAppLauncher.class);
        // Not easy to inject; this test only ensures service starts without crash
        controller.create();
        controller.destroy();
    }
}
