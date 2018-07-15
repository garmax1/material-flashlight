package co.garmax.materialflashlight.features.modules;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;

import co.garmax.materialflashlight.ui.RootActivity;
import timber.log.Timber;

/**
 * Module for device screen
 */
public class ScreenModule implements ModuleBase {

    public static final String ACTION_SCREEN_MODULE = "action_screen_module";
    public static final String EXTRA_BRIGHTNESS_PERCENT = "extra_brightness_percent";

    private Context context;
    private int previousScreenBrightness = -1;
    private int previousBrightnessMode = -1;

    ScreenModule(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        // Save initial values
        try {
            previousScreenBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            previousBrightnessMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Timber.e(e, "Can't read screen brightnessObservable settings");
        }

        // Set system values
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 255);
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

        // Open activity with light screen
        context.startActivity(new Intent(context, RootActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    @Override
    public void release() {
        // Restore system values
        if (previousScreenBrightness >= 0) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, previousScreenBrightness);
        }

        if (previousBrightnessMode >= 0) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, previousBrightnessMode);
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public void setBrightness(int percents) {
        // Pass value to the screen light
        Intent intent = new Intent(ACTION_SCREEN_MODULE);
        intent.putExtra(EXTRA_BRIGHTNESS_PERCENT, percents);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {

            Intent grantIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            grantIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(grantIntent);

            return false;
        }

        return true;
    }
}