package co.garmax.materialflashlight.features.modules

import android.Manifest
import android.content.Context

/**
 * Module for camera LED flashlight
 */
abstract class BaseCameraFlashModule(val context: Context) : ModuleBase {
    abstract fun lightOn()
    abstract fun lightOff()

    abstract override val isAvailable: Boolean
    abstract override val isSupported: Boolean

    override fun requiredRuntimePermissions(): List<String> = listOf(Manifest.permission.CAMERA)

    override fun init() {
        //Do nothing
    }

    override fun setBrightness(percents: Int) {
        if (percents < 50) lightOff() else lightOn()
    }

    override fun checkPermissions(): Boolean = true

    override fun release() {
        lightOff()
    }
}
