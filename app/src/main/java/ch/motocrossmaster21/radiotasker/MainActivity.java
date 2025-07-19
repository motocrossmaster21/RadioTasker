package ch.motocrossmaster21.radiotasker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import ch.motocrossmaster21.radiotasker.SystemUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText deviceNameEditText;
    private EditText packageNameEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceNameEditText = findViewById(R.id.deviceNameEditText);
        packageNameEditText = findViewById(R.id.packageNameEditText);
        Button saveButton = findViewById(R.id.saveButton);

        deviceNameEditText.setText(SharedPrefsUtil.getDeviceName(this));
        packageNameEditText.setText(SharedPrefsUtil.getPackageName(this));

        saveButton.setOnClickListener(v -> saveConfig());

        requestPermissionsIfNeeded();
    }

    private void saveConfig() {
        String deviceName = deviceNameEditText.getText().toString().trim();
        String packageName = packageNameEditText.getText().toString().trim();

        SharedPrefsUtil.setDeviceName(this, deviceName);
        SharedPrefsUtil.setPackageName(this, packageName);
        Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Konfiguration gespeichert: deviceName=" + deviceName + ", packageName=" + packageName);

        // Paketstatus prüfen und loggen
        boolean pkgExists = SystemUtils.isPackageInstalled(getApplicationContext(), packageName);
        Log.i(TAG, "Paket " + packageName + (pkgExists ? " ist installiert" : " ist nicht installiert"));
        if (!pkgExists && !packageName.isEmpty()) {
            Toast.makeText(this, "Warnung: Das Paket '" + packageName + "' ist nicht installiert.", Toast.LENGTH_SHORT).show();
        }

        // Gerätestatus prüfen und loggen
        boolean deviceExists = SystemUtils.isPairedDevice(deviceName);
        Log.i(TAG, "Gerät " + deviceName + (deviceExists ? " ist gekoppelt" : " ist nicht gekoppelt"));

        if (!deviceExists && !deviceName.isEmpty()) {
            String message = "Das Bluetooth-Gerät '" + deviceName + "' ist nicht gekoppelt. Bitte koppeln Sie es in den Bluetooth-Einstellungen.";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Warnung: Gerät '" + deviceName + "' ist nicht gekoppelt.");
        }
    }


    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting BLUETOOTH_CONNECT permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
            } else {
                Log.d(TAG, "BLUETOOTH_CONNECT permission already granted");
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14
            if (checkSelfPermission(android.Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting FOREGROUND_SERVICE_CONNECTED_DEVICE permission");
                requestPermissions(new String[] {
                        android.Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE
                }, 1002);
            } else {
                Log.d(TAG, "FOREGROUND_SERVICE_CONNECTED_DEVICE permission already granted");
            }
        }
    }
}
