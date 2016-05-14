package co.garmax.materialflashlight.modes

import co.garmax.materialflashlight.modules.ModuleManager

abstract class ModeBase(val mModuleManager: ModuleManager) {

    abstract fun start()
    abstract fun stop()
}
