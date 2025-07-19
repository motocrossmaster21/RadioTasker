package ch.motocrossmaster21.radiotasker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.bluetooth.BluetoothDevice;
import android.companion.CompanionDeviceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ch.motocrossmaster21.radiotasker.CompanionManager;

public class MainActivity extends AppCompatActivity {
    private EditText deviceNameEditText;
    private EditText packageNameEditText;
    private Button pairButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceNameEditText = findViewById(R.id.deviceNameEditText);
        packageNameEditText = findViewById(R.id.packageNameEditText);
        Button saveButton = findViewById(R.id.saveButton);
        pairButton = findViewById(R.id.pairButton);

        deviceNameEditText.setText(SharedPrefsUtil.getDeviceName(this));
        packageNameEditText.setText(SharedPrefsUtil.getPackageName(this));

        saveButton.setOnClickListener(v -> saveConfig());
        pairButton.setOnClickListener(v -> startPairing());

        requestPermissionsIfNeeded();
    }

    private void saveConfig() {
        String deviceName = deviceNameEditText.getText().toString();
        String packageName = packageNameEditText.getText().toString();
        SharedPrefsUtil.setDeviceName(this, deviceName);
        SharedPrefsUtil.setPackageName(this, packageName);
        Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show();
    }

    private void startPairing() {
        String deviceName = deviceNameEditText.getText().toString();
        CompanionManager.associateDevice(this, deviceName);
    }

    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14
            if (checkSelfPermission(android.Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        android.Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE
                }, 1002);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CompanionManager.ASSOCIATE_REQUEST && resultCode == RESULT_OK && data != null) {
            BluetoothDevice device = data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
            if (device != null) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
                    device.createBond();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1003);
                }
            }
        }
    }
}
