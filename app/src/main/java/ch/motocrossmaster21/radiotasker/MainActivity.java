package ch.motocrossmaster21.radiotasker;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
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

public class MainActivity extends AppCompatActivity {
    private EditText deviceNameEditText;
    private EditText packageNameEditText;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceNameEditText = findViewById(R.id.deviceNameEditText);
        packageNameEditText = findViewById(R.id.packageNameEditText);
        saveButton = findViewById(R.id.saveButton);

        deviceNameEditText.setText(SharedPrefsUtil.getDeviceName(this));
        packageNameEditText.setText(SharedPrefsUtil.getPackageName(this));

        saveButton.setOnClickListener(v -> saveConfig());

        requestPermissionsIfNeeded();
    }

    private void saveConfig() {
        String deviceName = deviceNameEditText.getText().toString();
        String packageName = packageNameEditText.getText().toString();
        SharedPrefsUtil.setDeviceName(this, deviceName);
        SharedPrefsUtil.setPackageName(this, packageName);
        Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show();
    }

    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
            }
        }
    }
}
