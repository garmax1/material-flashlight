package co.garmax.materialflashlight.features.modules

import android.content.Context
import android.os.Build

class ModuleFactory(private val context: Context) {

    fun getModule(module: ModuleBase.Module): ModuleBase {

        // Create new module
        if (module == ModuleBase.Module.MODULE_SCREEN) {
            return ScreenModule(context)
        } else if (module === ModuleBase.Module.MODULE_CAMERA_FLASHLIGHT) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CameraFlashModuleV23(context)
            } else {
                CameraFlashModuleV16(context)
            }
        }
        throw IllegalArgumentException(module.name + " module not implemented")
    }
}