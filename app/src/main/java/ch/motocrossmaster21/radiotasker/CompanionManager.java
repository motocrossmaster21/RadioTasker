package ch.motocrossmaster21.radiotasker;

import android.app.Activity;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.Context;
import android.content.IntentSender;
import android.util.Log;

import java.util.regex.Pattern;

public class CompanionManager {
    public static final int ASSOCIATE_REQUEST = 1001;
    private static final String TAG = "CompanionMgr";

    public static void associateDevice(Activity activity, String deviceName) {
        if (activity == null || deviceName == null) {
            return;
        }
        CompanionDeviceManager cdm =
                (CompanionDeviceManager) activity.getSystemService(Context.COMPANION_DEVICE_SERVICE);
        if (cdm == null) {
            Log.e(TAG, "CompanionDeviceManager not available");
            return;
        }
        BluetoothDeviceFilter filter = new BluetoothDeviceFilter.Builder()
                .setNamePattern(Pattern.compile(Pattern.quote(deviceName)))
                .build();
        AssociationRequest request = new AssociationRequest.Builder()
                .addDeviceFilter(filter)
                .setSingleDevice(true)
                .build();
        cdm.associate(request, new CompanionDeviceManager.Callback() {
            @Override
            public void onDeviceFound(IntentSender chooserLauncher) {
                try {
                    activity.startIntentSenderForResult(chooserLauncher, ASSOCIATE_REQUEST,
                            null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Failed to launch chooser", e);
                }
            }

            @Override
            public void onFailure(CharSequence error) {
                Log.e(TAG, "Association failed: " + error);
            }
        }, null);
    }
}
