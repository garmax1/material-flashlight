package co.garmax.materialflashlight.features.modules;

import android.content.Context;
import android.os.Build;

public class ModuleFactory {

    private final Context context;

    public ModuleFactory(Context context) {
        this.context = context;
    }

    public ModuleBase getModule(ModuleBase.Module module) {

        // Create new module
        if (module == ModuleBase.Module.MODULE_SCREEN) {
            return new ScreenModule(context);
        } else if (module == ModuleBase.Module.MODULE_CAMERA_FLASHLIGHT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return new CameraFlashModuleV23(context);
            } else {
                return new CameraFlashModuleV16(context);
            }
        }

        throw new IllegalArgumentException(module.name() + " module not implemented");
    }

}
