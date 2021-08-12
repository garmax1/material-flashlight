package co.garmax.materialflashlight.features.modules

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import co.garmax.materialflashlight.ui.PermissionsActivity

/**
 * Module for camera LED flashlight
 */
abstract class BaseCameraFlashModule(val context: Context) : ModuleBase {
    abstract fun lightOn()
    abstract fun lightOff()

    abstract override val isAvailable: Boolean
    abstract override val isSupported: Boolean

    override fun init() {
        //Do nothing
    }

    override fun setBrightness(percents: Int) {
        if (percents < 50) lightOff() else lightOn()
    }

    override fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionsActivity.startActivity(context, arrayOf(Manifest.permission.CAMERA))
            return false
        }

        return true
    }
}