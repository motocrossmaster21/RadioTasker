package ch.motocrossmaster21.radiotasker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.Set;

/**
 * Utility methods for checking system state such as installed packages
 * and paired Bluetooth devices.
 */
public final class SystemUtils {
    private SystemUtils() {
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isPairedDevice(String deviceName) {
        if (deviceName == null || deviceName.isEmpty()) {
            return false;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        if (devices == null) {
            return false;
        }
        for (BluetoothDevice device : devices) {
            if (deviceName.equals(device.getName())) {
                return true;
            }
        }
        return false;
    }
}
