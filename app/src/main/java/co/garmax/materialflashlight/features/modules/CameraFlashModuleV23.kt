package co.garmax.materialflashlight.features.modules

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber

@RequiresApi(api = Build.VERSION_CODES.M)
class CameraFlashModuleV23(context: Context) : BaseCameraFlashModule(context) {

    override val isAvailable get() = cameraManager != null && cameraId != null

    override val isSupported get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    private var cameraManager: CameraManager? = null

    private var cameraId: String? = null

    init {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?

        cameraManager?.let {
            try {
                it.cameraIdList.forEach { id ->
                    val characteristics = it.getCameraCharacteristics(id)
                    val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                        this.cameraId = id
                    }
                }
            } catch (e: CameraAccessException) {
                Timber.e(e, "Can't get cameras list")
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Can't turn on flashlight")
            }
        } ?: run {
            Timber.e("Can't initialize CameraManager")
        }
    }

    override fun lightOn() {
        try {
            cameraId?.let { cameraManager?.setTorchMode(it, true) }
        } catch (e: CameraAccessException) {
            Timber.e(e, "Can't turn on flashlight")
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Can't turn on flashlight")
        }
    }

    override fun lightOff() {
        try {
            cameraId?.let { cameraManager?.setTorchMode(it, false) }
        } catch (e: CameraAccessException) {
            Timber.e(e, "Can't turn off flashlight")
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Can't turn on flashlight")
        }
    }
}