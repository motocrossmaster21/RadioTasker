package ch.motocrossmaster21.radiotasker;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothEventReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothReceiver";

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device != null) {
                Log.d(TAG, "Connected device name: " + device.getName());
                String targetName = SharedPrefsUtil.getDeviceName(context);
                Log.d(TAG, "Configured device name: " + targetName);
                if (targetName.equals(device.getName())) {
                    Log.i(TAG, "Target device connected: " + device.getName());
                    Intent serviceIntent = new Intent(context, RadioTaskerService.class);
                    ContextCompat.startForegroundService(context, serviceIntent);
                } else {
                    Log.d(TAG, "Device name does not match configured target");
                }
            } else {
                Log.w(TAG, "BluetoothDevice EXTRA_DEVICE missing in intent");
            }
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            Log.d(TAG, "Bluetooth device disconnected; resetting usage monitor");
            UsageMonitor.reset();
        }
    }
}
