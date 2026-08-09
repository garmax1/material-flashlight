package co.garmax.materialflashlight.features.modules

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import timber.log.Timber
import java.io.IOException

/**
 * Module for camera LED flashlight
 */
class CameraFlashModuleV16(context: Context) : BaseCameraFlashModule(context) {

    override val isAvailable: Boolean
        get() {
            var result = false
            try {
                if (camera != null) {
                    // Try to get parameters to check if instance is available
                    camera!!.parameters
                    result = true
                }
            } catch (e: Exception) {
                Timber.e(e, "Camera instance not available")
            }

            // Release camera if we have problem with it
            if (camera != null && !result) release()

            return result
        }

    override val isSupported
        get() = if (isAvailable) {
            val flashModes = camera?.parameters?.supportedFlashModes
            isParameterSupported(
                Camera.Parameters.FLASH_MODE_TORCH,
                flashModes
            ) || isParameterSupported(Camera.Parameters.FLASH_MODE_ON, flashModes)
        } else {
            true
        }

    private var camera: Camera? = null

    private var previewTexture: SurfaceTexture? = SurfaceTexture(0)

    init {
        initializeCamera()
    }

    override fun lightOn() {
        val params = camera?.parameters
        val flashModes = params?.supportedFlashModes
        if (isParameterSupported(Camera.Parameters.FLASH_MODE_TORCH, flashModes)) {
            params?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
        } else if (isParameterSupported(Camera.Parameters.FLASH_MODE_ON, flashModes)) {
            params?.flashMode = Camera.Parameters.FLASH_MODE_ON
        }
        camera?.parameters = params
        camera?.startPreview()
    }

    override fun lightOff() {
        camera?.let {
            val params = it.parameters
            params.flashMode = Camera.Parameters.FLASH_MODE_OFF
            it.parameters = params
            it.stopPreview()
        }
    }

    override fun release() {
        super.release()

        invalidateCamera()
    }

    private fun initializeCamera() {
        camera = rearCamera()

        // Hack for some android versions
        try {
            camera?.setPreviewTexture(previewTexture)
        } catch (e: IOException) {
            Timber.e("Can't set preview texture")
        }
    }

    private fun invalidateCamera() {
        camera?.release()
        camera = null
        previewTexture = null
    }

    private fun isParameterSupported(value: String, supported: List<String>?): Boolean {
        return supported != null && supported.indexOf(value) >= 0
    }

    private fun rearCamera(): Camera? {
        var cameraId = -1
        val info = CameraInfo()
        for (i in 0 until Camera.getNumberOfCameras() - 1) {
            Camera.getCameraInfo(i, info)
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                break
            }
        }
        if (cameraId < 0) {
            Timber.w("Wrong camera id $cameraId")
            return null
        }

        try {
            return Camera.open(cameraId)
        } catch (e: Exception) {
            Timber.e(e, "Exception when open camera $cameraId")
        }
        return null
    }
}