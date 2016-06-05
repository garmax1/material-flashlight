package co.garmax.materialflashlight.modules

import android.app.Activity
import android.content.Context

abstract class ModuleBase(var context: Context) {

    init {
        // Use only app context to prevent link for activity
        context = context.applicationContext;
    }

    /**
     * Turn on light in the module
     */
    abstract fun turnOn()

    /**
     * Turn off light in the module
     */
    abstract fun turnOff()

    /**
     * Can we use this the module now or not. Call to check if module started correctly.
     */
    abstract fun isAvailable(): Boolean

    /**
     * Hardware support the module or not. Call if need to check module if it not started.
     */
    abstract fun isSupported(): Boolean

    /**
     * Set light brightness in the module
     */
    abstract fun setBrightnessVolume(percent: Int)

    /**
     * Initialize all property for module and catch resources
     */
    abstract fun start()

    /**
     * Release all resources in the module
     */
    abstract fun stop()

    /**
     * Check if module request runtime permissions and call permissions dialog if needed
     * Return true if permission do not required otherwise false
     */
    abstract fun checkPermissions(requestCode: Int, activity: Activity) : Boolean

    companion object {
        const val MODULE_SCREEN = 0
        const val MODULE_CAMERA_FLASHLIGHT = 1
    }
}