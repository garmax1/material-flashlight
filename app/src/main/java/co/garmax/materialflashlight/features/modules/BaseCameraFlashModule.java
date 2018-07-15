package co.garmax.materialflashlight.features.modules;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import co.garmax.materialflashlight.ui.PermissionsActivity;

/**
 * Module for camera LED flashlight
 */
public abstract class BaseCameraFlashModule extends ModuleBase {

    private Context context;

    /**
     * @param context Used for runtime permission request
     */
    BaseCameraFlashModule(Context context) {
        this.context = context;
    }

    abstract void lightOn();

    abstract void lightOff();

    @Override
    public abstract boolean isAvailable();

    @Override
    public abstract boolean isSupported();

    @Override
    public void init() {
        //Do nothing
    }

    @Override
    public void setBrightness(int percentage) {
        if(percentage < 50) {
            lightOff();
        } else {
            lightOn();
        }
    }

    @Override
    public boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            PermissionsActivity.startActivity(context, new String[]{Manifest.permission.CAMERA});

            return false;
        }

        return true;
    }

    public Context getContext() {
        return context;
    }
}