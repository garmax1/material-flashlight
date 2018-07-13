package co.garmax.materialflashlight.features;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import javax.inject.Inject;

import co.garmax.materialflashlight.R;
import dagger.android.AndroidInjection;

/**
 * System won't kill our app with foreground service
 */
public class ForegroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String EXTRA_MODE = "extra_mode";

    private static final int MODE_STOP = 0;
    private static final int MODE_START = 1;
    private static final int MODE_LIGHT_OFF = 2;

    @Inject
    LightManager lightManager;

    public static void startService(Context context) {
        context.startService(buildIntent(context, MODE_START));
    }

    public static void stopService(Context context) {
        context.startService(buildIntent(context, MODE_STOP));
    }

    private static Intent buildIntent(Context context, int mode) {
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
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(getApplicationContext(), "");
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

        startForeground(NOTIFICATION_ID, notifyBuilder.build());
    }
}
