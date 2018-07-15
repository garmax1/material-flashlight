package co.garmax.materialflashlight.features.modules;

/**
 * Module implements light source like screen, camera flashlight
 */
public abstract class ModuleBase {

    /**
     * Initialize and capture resources for module
     */
    public abstract void init();

    /**
     * Release resources for module
     */
    public abstract void release();

    /**
     * Can we use this the module now or not.
     */
    public abstract boolean isAvailable();

    /**
     * Hardware support the module or not.
     */
    public abstract boolean isSupported();

    /**
     * Set light brightnessO in percents
     */
    public abstract void setBrightness(int percents);

    /**
     * Check if module request runtime permissions and call permissions dialog if needed
     * Return true if permission do not required otherwise false
     */
    public abstract boolean checkPermissions();
}
