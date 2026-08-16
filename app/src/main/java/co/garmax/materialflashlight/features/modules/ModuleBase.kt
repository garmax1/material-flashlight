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
     * Runtime permissions required by this module.
     */
    fun requiredRuntimePermissions(): List<String> = emptyList()

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
     * Check if module request additional permissions (e.g. WRITE_SETTINGS) and open dialogs if needed.
     * Return true if permission do not required otherwise false
     */
    fun checkPermissions(): Boolean
}
