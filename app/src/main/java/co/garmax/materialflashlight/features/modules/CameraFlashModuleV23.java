package co.garmax.materialflashlight.features.modules;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import timber.log.Timber;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CameraFlashModuleV23 extends BaseCameraFlashModule {

    private CameraManager cameraManager;
    private String cameraId;

    public CameraFlashModuleV23(Context context) {
        super(context);
        cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            Timber.e(e, "Can't get cameras list");
        }
    }

    @Override
    public void lightOn() {
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            Timber.e(e, "Can't turn on flashlight");
        }
    }

    @Override
    public void lightOff() {
        try {
            if(cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, false);
            }
        } catch (CameraAccessException e) {
            Timber.e(e, "Can't turn off flashlight");
        }
    }

    @Override
    public boolean isAvailable() {
        return cameraManager != null && cameraId != null;
    }

    @Override
    public boolean isSupported() {
        return getContext()
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}