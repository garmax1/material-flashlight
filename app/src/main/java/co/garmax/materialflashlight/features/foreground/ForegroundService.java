package co.garmax.materialflashlight.features.foreground;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import javax.inject.Inject;

import co.garmax.materialflashlight.R;
import co.garmax.materialflashlight.features.LightManager;
import dagger.android.AndroidInjection;

/**
 * System won't kill our app with foreground service
 */
public class ForegroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String EXTRA_MODE = "extra_mode";
    private static final String CHANNEL_ID = "MaterialFlashLight.Notification.Channel";

    public static final int MODE_STOP = 0;
    public static final int MODE_START = 1;
    public static final int MODE_LIGHT_OFF = 2;

    @Inject
    LightManager lightManager;

    public static Intent buildIntent(Context context, int mode) {
        Intent intent = new Intent(context, ForegroundService.class);
        intent.putExtra(EXTRA_MODE, mode);

        return intent;
    }

    public ForegroundService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        int mode = intent.getIntExtra(EXTRA_MODE, MODE_STOP);

        // Handle mode
        if (mode == MODE_LIGHT_OFF) {
            lightManager.turnOff();
        } else if (mode == MODE_STOP) {
            stop();
        } else if (mode == MODE_START) {
            startForeground();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void stop() {
        stopForeground(true);
        stopSelf();
    }

    // Start foreground service with notification
    private void startForeground() {
        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        notifyBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_light_notification)
                .setWhen(System.currentTimeMillis())
                .setContentText(getString(R.string.notification_tap_to_turn_off))
                .setContentTitle(getString(R.string.notification_light));

        Intent notificationIntent = buildIntent(getApplicationContext(), MODE_LIGHT_OFF);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
                0,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        notifyBuilder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        startForeground(NOTIFICATION_ID, notifyBuilder.build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if(notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
