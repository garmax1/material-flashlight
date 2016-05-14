package co.garmax.materialflashlight.modes

import co.garmax.materialflashlight.modules.ModuleManager

/**
 * Just steady light
 */
class TorchMode(moduleManager: ModuleManager) : ModeBase(moduleManager) {
    override fun start() {
        mModuleManager.turnOn()
    }

    override fun stop() {
        mModuleManager.turnOff()
    }
}
