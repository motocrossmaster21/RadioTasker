package ch.motocrossmaster21.radiotasker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.util.Log;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText deviceNameEditText;
    private EditText packageNameEditText;
    private Spinner pairedDevicesSpinner;
    private Spinner launchAppSpinner;
    private CheckBox restartCheckBox;
    private final java.util.List<String> launchAppPackages = new java.util.ArrayList<>();
    private final java.util.List<BluetoothDevice> pairedDeviceList = new java.util.ArrayList<>();
    private String selectedDeviceAddress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        deviceNameEditText = findViewById(R.id.deviceNameEditText);
        packageNameEditText = findViewById(R.id.packageNameEditText);
        Button saveButton = findViewById(R.id.saveButton);
        pairedDevicesSpinner = findViewById(R.id.pairedDevicesSpinner);
        launchAppSpinner = findViewById(R.id.launchAppSpinner);
        restartCheckBox = findViewById(R.id.restartCheckBox);

        deviceNameEditText.setText(SharedPrefsUtil.getDeviceName(this));
        packageNameEditText.setText(SharedPrefsUtil.getPackageName(this));
        selectedDeviceAddress = SharedPrefsUtil.getDeviceAddress(this);
        restartCheckBox.setChecked(SharedPrefsUtil.shouldRestartApp(this));
        Log.d(TAG, "Loaded prefs device=" + deviceNameEditText.getText() + " pkg=" + packageNameEditText.getText());

        saveButton.setOnClickListener(v -> saveConfig());
        pairedDevicesSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof String) {
                    String name = (String) item;
                    if (!name.equals(getString(R.string.no_paired_devices)) && !name.equals(getString(R.string.select_paired_device))) {
                        int index = position - 1; // account for header
                        if (index >= 0 && index < pairedDeviceList.size()) {
                            BluetoothDevice dev = pairedDeviceList.get(index);
                            selectedDeviceAddress = dev.getAddress();
                            deviceNameEditText.setText(dev.getName());
                            Log.d(TAG, "Paired device selected: " + dev.getName() + " addr=" + selectedDeviceAddress);
                        } else {
                            deviceNameEditText.setText(name);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // no-op
            }
        });
        launchAppSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position <= 0 || position - 1 >= launchAppPackages.size()) return;
                String pkg = launchAppPackages.get(position - 1);
                packageNameEditText.setText(pkg);
                Log.d(TAG, "Launch app selected: " + pkg);
                SharedPrefsUtil.setPackageName(MainActivity.this, pkg);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // no-op
            }
        });

        requestPermissionsIfNeeded();
        promptForNotificationsIfNeeded();
        populatePairedDevices();
        populateLaunchableApps();
    }

    private void saveConfig() {
        String deviceName = deviceNameEditText.getText().toString();
        String packageName = packageNameEditText.getText().toString();
        SharedPrefsUtil.setDeviceName(this, deviceName);
        SharedPrefsUtil.setPackageName(this, packageName);
        SharedPrefsUtil.setDeviceAddress(this, selectedDeviceAddress);
        SharedPrefsUtil.setShouldRestartApp(this, restartCheckBox.isChecked());
        Log.d(TAG, "Saved config device=" + deviceName + " pkg=" + packageName);
        Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show();
    }

    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
                Log.d(TAG, "Requesting BLUETOOTH_CONNECT");
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_SCAN}, 0);
                Log.d(TAG, "Requesting BLUETOOTH_SCAN");
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14
            if (checkSelfPermission(android.Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        android.Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE
                }, 1002);
                Log.d(TAG, "Requesting FOREGROUND_SERVICE_CONNECTED_DEVICE");
            }
        }
    }
    private void promptForNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1005);
                Log.d(TAG, "Requesting POST_NOTIFICATIONS");
            }
        }
    }


    private void populatePairedDevices() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            adapter.add(getString(R.string.no_paired_devices));
            Log.w(TAG, "BLUETOOTH_CONNECT missing; cannot list bonded devices");
            pairedDevicesSpinner.setAdapter(adapter);
            return;
        } else {
            BluetoothAdapter adapterBt = BluetoothAdapter.getDefaultAdapter();
            if (adapterBt != null && adapterBt.isEnabled()) {
                pairedDeviceList.clear();
                for (BluetoothDevice device : adapterBt.getBondedDevices()) {
                    if (device.getName() != null) {
                        pairedDeviceList.add(device);
                    }
                }
                java.util.Collections.sort(pairedDeviceList, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                for (BluetoothDevice device : pairedDeviceList) {
                    adapter.add(device.getName());
                }
                Log.d(TAG, "Found bonded devices: " + pairedDeviceList.size());
            }
            if (adapter.isEmpty()) {
                adapter.add(getString(R.string.no_paired_devices));
                Log.d(TAG, "No bonded devices available");
            } else {
                adapter.insert(getString(R.string.select_paired_device), 0);
            }
        }
        pairedDevicesSpinner.setAdapter(adapter);
        // preselect saved address if present
        if (!selectedDeviceAddress.isEmpty() && !pairedDeviceList.isEmpty()) {
            int idx = 1;
            for (BluetoothDevice device : pairedDeviceList) {
                if (selectedDeviceAddress.equals(device.getAddress())) {
                    pairedDevicesSpinner.setSelection(idx);
                    break;
                }
                idx++;
            }
        }
    }

    private void populateLaunchableApps() {
        launchAppPackages.clear();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        android.content.pm.PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        java.util.List<android.content.pm.ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
        java.util.Collections.sort(apps, (a, b) -> {
            CharSequence la = a.loadLabel(pm);
            CharSequence lb = b.loadLabel(pm);
            String sa = la != null ? la.toString() : "";
            String sb = lb != null ? lb.toString() : "";
            return sa.compareToIgnoreCase(sb);
        });
        for (android.content.pm.ResolveInfo info : apps) {
            CharSequence label = info.loadLabel(pm);
            String pkg = info.activityInfo.packageName;
            String name = label != null ? label.toString() : pkg;
            adapter.add(name + " (" + pkg + ")");
            launchAppPackages.add(pkg);
        }
        if (adapter.isEmpty()) {
            adapter.add(getString(R.string.no_launchable_apps));
        } else {
            adapter.insert(getString(R.string.select_launch_app), 0);
        }
        launchAppSpinner.setAdapter(adapter);
        String savedPkg = SharedPrefsUtil.getPackageName(this);
        if (!savedPkg.isEmpty()) {
            int idx = 1;
            for (String pkg : launchAppPackages) {
                if (savedPkg.equals(pkg)) {
                    launchAppSpinner.setSelection(idx);
                    break;
                }
                idx++;
            }
        }
        Log.d(TAG, "Launchable apps listed: " + launchAppPackages.size() + " savedPkg=" + savedPkg);
    }

}
