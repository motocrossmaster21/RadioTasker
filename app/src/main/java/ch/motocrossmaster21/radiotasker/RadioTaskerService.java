package ch.motocrossmaster21.radiotasker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class RadioTaskerService extends Service {
    private static final String CHANNEL_ID = "RadioTasker";
    private static final String TAG = "RadioTaskSvc";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        createNotificationChannel();
        Notification notification = buildNotification();
        if (notification == null) {
            Log.e(TAG, "Notification build failed; stopping service");
            stopSelf();
            return;
        }
        Log.d(TAG, "Starting foreground with notification");
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand flags=" + flags + " startId=" + startId);
        // Refresh notification in case it was dismissed
        Notification notification = buildNotification();
        if (notification != null) {
            startForeground(1, notification);
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Toast.makeText(this, "Launching app...", Toast.LENGTH_SHORT).show();
            boolean launched = EnergyAppLauncher.launchApp(this);
            Log.d(TAG, "Launch request sent (delayed), launched=" + launched);
        }, 1000);
        return START_STICKY;
    }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        // Action to open target app
        PendingIntent launchPending = pendingIntent;
        String targetPkg = SharedPrefsUtil.getPackageName(this);
        try {
            PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(targetPkg);
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchPending = PendingIntent.getActivity(this, 1, launchIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            }
        } catch (Exception ignored) {}
        String title = getString(R.string.app_name);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(getString(R.string.running) + " - tap to open target")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .addAction(0, getString(R.string.notification_action_launch), launchPending)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            } else {
                Log.w(TAG, "NotificationManager null; cannot create channel");
            }
        }
    }
}
