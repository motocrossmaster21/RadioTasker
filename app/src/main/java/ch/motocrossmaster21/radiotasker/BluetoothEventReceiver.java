package ch.motocrossmaster21.radiotasker;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.text.TextUtils;
import androidx.core.app.NotificationManagerCompat;
import android.widget.Toast;

public class BluetoothEventReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothReceiver";

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "BLUETOOTH_CONNECT not granted; skipping event");
                Toast.makeText(context, "BT connect permission missing", Toast.LENGTH_SHORT).show();
                return;
            }
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null) {
                Log.w(TAG, "No device in intent extras");
                Toast.makeText(context, "No device info in intent", Toast.LENGTH_SHORT).show();
                return;
            }
            String targetName = SharedPrefsUtil.getDeviceName(context);
            String targetAddress = SharedPrefsUtil.getDeviceAddress(context);
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();
            if (deviceName == null) {
                Log.w(TAG, "Device name null; cannot match");
                Toast.makeText(context, "Device name missing", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean addressMatch = !TextUtils.isEmpty(targetAddress) && targetAddress.equalsIgnoreCase(deviceAddress);
            boolean nameMatch = targetName.equals(deviceName);
            if (addressMatch || nameMatch) {
                Log.d(TAG, "Target device connected: " + deviceName + " addr=" + deviceAddress + " addressMatch=" + addressMatch + " nameMatch=" + nameMatch);
                Toast.makeText(context, "Device connected: " + deviceName, Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(context, RadioTaskerService.class);
                try {
                    ContextCompat.startForegroundService(context, serviceIntent);
                    Log.d(TAG, "Foreground service requested");
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to start foreground service", ex);
                    Toast.makeText(context, "Starting app directly...", Toast.LENGTH_SHORT).show();
                    EnergyAppLauncher.launchApp(context);
                }
            } else {
                Log.d(TAG, "Connected device does not match target: " + deviceName + " addr=" + deviceAddress + " expectedName=" + targetName + " expectedAddr=" + targetAddress);
                Toast.makeText(context, "Other device connected: " + deviceName, Toast.LENGTH_SHORT).show();
            }
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            Log.d(TAG, "Device disconnected");
            UsageMonitor.reset();
        }
    }
}
