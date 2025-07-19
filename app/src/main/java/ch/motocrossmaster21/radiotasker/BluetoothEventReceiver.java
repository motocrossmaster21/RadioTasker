package ch.motocrossmaster21.radiotasker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothEventReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device != null) {
                String targetName = SharedPrefsUtil.getDeviceName(context);
                if (targetName.equals(device.getName())) {
                    Log.d(TAG, "Target device connected: " + device.getName());
                    Intent serviceIntent = new Intent(context, RadioTaskerService.class);
                    ContextCompat.startForegroundService(context, serviceIntent);
                }
            }
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            UsageMonitor.reset();
        }
    }
}
