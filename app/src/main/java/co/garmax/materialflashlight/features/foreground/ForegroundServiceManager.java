package co.garmax.materialflashlight.features.foreground;

import android.content.Context;

import static co.garmax.materialflashlight.features.foreground.ForegroundService.MODE_START;
import static co.garmax.materialflashlight.features.foreground.ForegroundService.MODE_STOP;

public class ForegroundServiceManager {

    private final Context context;

    public ForegroundServiceManager(Context context) {
        this.context = context;
    }

    public void startService() {
        context.startService(ForegroundService.buildIntent(context, MODE_START));
    }

    public void stopService() {
        context.startService(ForegroundService.buildIntent(context, MODE_STOP));
    }
}
