package co.garmax.materialflashlight.features.modes

/**
 * Just steady light
 */
class TorchMode : ModeBase() {

    override fun start() {
        setBrightness(MAX_LIGHT_VOLUME)
    }

    override fun stop() {
        setBrightness(MIN_LIGHT_VOLUME)
    }

    override fun checkPermissions(): Boolean {
        return true
    }
}