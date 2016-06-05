package co.garmax.materialflashlight.modes

import co.garmax.materialflashlight.modules.ModuleManager

abstract class ModeBase(val mModuleManager: ModuleManager) {

    abstract fun start()
    abstract fun stop()

    companion object {
        const val MODE_OFF = 0
        const val MODE_SOUND_STROBE = 1
        const val MODE_INTERVAL_STROBE = 2
        const val MODE_TORCH = 3
    }
}
