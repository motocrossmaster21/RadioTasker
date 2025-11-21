package ch.motocrossmaster21.radiotasker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.android.controller.ServiceController;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class RadioTaskerServiceTest {
    private ServiceController<RadioTaskerService> controller;

    @Before
    public void setUp() {
        controller = Robolectric.buildService(RadioTaskerService.class);
    }

    @Test
    public void testOnCreateLaunchesApp() {
        // This validates the service lifecycle runs without throwing during onCreate.
        controller.create();
        controller.destroy();
    }
}
