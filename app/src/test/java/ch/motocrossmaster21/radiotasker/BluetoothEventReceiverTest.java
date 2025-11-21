package ch.motocrossmaster21.radiotasker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.Manifest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowBluetoothDevice;
import androidx.test.core.app.ApplicationProvider;


import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class BluetoothEventReceiverTest {
    private Context context;
    private BluetoothEventReceiver receiver;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        ShadowApplication shadowApp = Shadows.shadowOf((android.app.Application) context);
        shadowApp.grantPermissions(Manifest.permission.BLUETOOTH_CONNECT);
        receiver = new BluetoothEventReceiver();
        SharedPrefsUtil.setDeviceName(context, "Device");
    }

    @Test
    public void testOnReceiveStartsService() {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:11:22:33:44:55");
        ShadowBluetoothDevice shadowDevice = Shadows.shadowOf(device);
        shadowDevice.setName("Device");
        Intent intent = new Intent(BluetoothDevice.ACTION_ACL_CONNECTED);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        receiver.onReceive(context, intent);
        ShadowApplication shadowApp = Shadows.shadowOf((android.app.Application) context);
        Intent service = shadowApp.getNextStartedService();
        assertNotNull(service);
        assertEquals(RadioTaskerService.class.getName(), service.getComponent().getClassName());
    }
}
