package co.garmax.materialflashlight.features.modules

/**
 * Module implements light source like screen, camera flashlight
 */
interface ModuleBase {
    enum class Module {
        MODULE_SCREEN, MODULE_CAMERA_FLASHLIGHT
    }

    /**
     * Can we use this the module now or not.
     */
    val isAvailable: Boolean

    /**
     * Hardware support the module or not.
     */
    val isSupported: Boolean

    /**
     * Initialize and capture resources for module
     */
    fun init()

    /**
     * Release resources for module
     */
    fun release()

    /**
     * Set light brightnessO in percents
     */
    fun setBrightness(percents: Int)

    /**
     * Check if module request runtime permissions and call permissions dialog if needed
     * Return true if permission do not required otherwise false
     */
    fun checkPermissions(): Boolean
}