package co.garmax.materialflashlight.features.modules;

/**
 * Module implements light source like screen, camera flashlight
 */
public interface ModuleBase {

    enum Module {
        MODULE_SCREEN,
        MODULE_CAMERA_FLASHLIGHT
    }

    /**
     * Initialize and capture resources for module
     */
    void init();

    /**
     * Release resources for module
     */
    void release();

    /**
     * Can we use this the module now or not.
     */
    boolean isAvailable();

    /**
     * Hardware support the module or not.
     */
    boolean isSupported();

    /**
     * Set light brightnessO in percents
     */
    void setBrightness(int percents);

    /**
     * Check if module request runtime permissions and call permissions dialog if needed
     * Return true if permission do not required otherwise false
     */
    boolean checkPermissions();
}
