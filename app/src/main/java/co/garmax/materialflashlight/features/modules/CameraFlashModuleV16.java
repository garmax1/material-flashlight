package co.garmax.materialflashlight.features.modules;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

/**
 * Module for camera LED flashlight
 */
public class CameraFlashModuleV16 extends BaseCameraFlashModule {

    private Camera camera;
    private SurfaceTexture previewTexture = new SurfaceTexture(0);

    CameraFlashModuleV16(Context context) {
        super(context);

        initializeCamera();
    }

    @Override
    public void lightOn() {
        Camera.Parameters params = camera.getParameters();

        List<String> flashModes = params.getSupportedFlashModes();

        if (isParameterSupported(Camera.Parameters.FLASH_MODE_TORCH, flashModes)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else if (isParameterSupported(Camera.Parameters.FLASH_MODE_ON, flashModes)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }

        camera.setParameters(params);
        camera.startPreview();
    }

    @Override
    public void lightOff() {
        if(camera != null) {
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
        }
    }

    @Override
    public void release() {
        invalidateCamera();
    }

    @Override
    public boolean isAvailable() {
        boolean result = false;

        try {
            if (camera != null) {
                // Try to get parameters to check if instance is available
                camera.getParameters();

                result = true;
            }
        } catch (Exception e) {
            Timber.e("Camera instance not available", e);
        }

        // Release camera if we have problem with it
        if(camera != null && !result) {
            release();
        }

        return result;
    }

    @Override
    public boolean isSupported() {
        // If flash supported by camera
        if (isAvailable()) {
            List<String> flashModes = camera.getParameters().getSupportedFlashModes();
            return isParameterSupported(Camera.Parameters.FLASH_MODE_TORCH, flashModes) ||
                    isParameterSupported(Camera.Parameters.FLASH_MODE_ON, flashModes);
        }
        // Return true and will check by flash modes
        // because hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) do not work correctly
        else {
            return true;
        }
    }

    private void initializeCamera() {
        camera = rearCamera();

        if(camera == null) {
            throw new IllegalStateException("Camera is null should check with isAvailable" +
                    " before calling this method");
        }

        // Hack for some android versions
        try {
            camera.setPreviewTexture(previewTexture);
        } catch (IOException e) {
            Timber.e("Can't set preview texture");
        }
    }

    private void invalidateCamera() {
        camera.release();
        camera = null;
        previewTexture = null;
    }

    private Boolean isParameterSupported(String value, List<String> supported) {
        return supported != null && supported.indexOf(value) >= 0;
    }

    @Nullable
    private Camera rearCamera() {
        int cameraId = -1;
        Camera.CameraInfo info = new Camera.CameraInfo();

        for (int i = 0; i < Camera.getNumberOfCameras() - 1; i++) {
            Camera.getCameraInfo(i, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }

        if (cameraId < 0) {
            Timber.w("Wrong camera id %d", cameraId);
            return null;
        }

        try {
            return Camera.open(cameraId);
        } catch (Exception e){
            Timber.e(e, "Exception when open camera %d", cameraId);
        }

        return null;
    }

}