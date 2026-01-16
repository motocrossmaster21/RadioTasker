package ch.motocrossmaster21.radiotasker;

import android.app.Activity;
import android.companion.AssociationRequest;
import android.companion.AssociationInfo;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.Context;
import android.content.IntentSender;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.regex.Pattern;

public class CompanionManager {
    public interface AssociationListener {
        void onAssociated();
        void onFailure(CharSequence error);
    }

    public static final int ASSOCIATE_REQUEST = 1001;
    private static final String TAG = "CompanionMgr";

    /**
     * Checks if the given device name is currently associated via the Companion Device API.
     */
    public static boolean isDeviceAssociated(Context context, String deviceName) {
        if (context == null || deviceName == null) {
            return false;
        }
        CompanionDeviceManager cdm =
                (CompanionDeviceManager) context.getSystemService(Context.COMPANION_DEVICE_SERVICE);
        if (cdm == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (AssociationInfo info : cdm.getMyAssociations()) {
                if (deviceName.equals(info.getDisplayName())) {
                    return true;
                }
            }
        } else {
            for (String mac : cdm.getAssociations()) {
                // getAssociations() only returns MAC addresses pre-Android 13.
                // Caller should supply exact name match in this case.
                if (deviceName.equals(mac)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void associateDevice(Activity activity, String deviceName, @Nullable AssociationListener listener) {
        if (activity == null || deviceName == null) {
            return;
        }
        CompanionDeviceManager cdm =
                (CompanionDeviceManager) activity.getSystemService(Context.COMPANION_DEVICE_SERVICE);
        if (cdm == null) {
            Log.e(TAG, "CompanionDeviceManager not available");
            return;
        }

        if (!activity.getPackageManager().hasSystemFeature("android.software.companion_device_setup")) {
            Log.w(TAG, "Companion Device feature not available");
            if (listener != null) listener.onFailure("Feature not available");
            return;
        }

        Log.d(TAG, "associateDevice called with name: " + deviceName);

        // Log existing associations and skip if the device is already registered
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (AssociationInfo info : cdm.getMyAssociations()) {
                Log.d(TAG, "API33+ Association: name=" + info.getDisplayName()
                        + ", MAC=" + info.getDeviceMacAddress());
                if (deviceName.equals(info.getDisplayName())) {
                    Log.d(TAG, "Device already associated: " + info.getDeviceMacAddress());
                    if (listener != null) listener.onAssociated();
                    return;
                }
            }
        } else {
            for (String mac : cdm.getAssociations()) {
                Log.d(TAG, "API<33 Association MAC: " + mac);
            }
        }
        BluetoothDeviceFilter filter = new BluetoothDeviceFilter.Builder()
                .setNamePattern(Pattern.compile(Pattern.quote(deviceName)))
                .build();
        Log.d(TAG, "Building association request for pattern: " + deviceName);
        AssociationRequest request = new AssociationRequest.Builder()
                .addDeviceFilter(filter)
                .setSingleDevice(true)
                .build();
        Log.d(TAG, "Calling associate()");
        cdm.associate(request, new CompanionDeviceManager.Callback() {
            @Override
            public void onDeviceFound(IntentSender chooserLauncher) {
                Log.d(TAG, "Device found, launching chooser");
                try {
                    activity.startIntentSenderForResult(chooserLauncher, ASSOCIATE_REQUEST,
                            null, 0, 0, 0, null);
                    Log.d(TAG, "Chooser launched");
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Failed to launch chooser", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                }
            }

            @Override
            public void onFailure(CharSequence error) {
                Log.e(TAG, "Association failed: " + error);
                if (listener != null) listener.onFailure(error);
            }
        }, null);
    }
}
