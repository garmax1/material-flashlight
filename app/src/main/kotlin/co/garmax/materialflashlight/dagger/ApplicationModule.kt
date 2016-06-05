package co.garmax.materialflashlight.dagger

import android.content.Context
import co.garmax.materialflashlight.Preferences
import co.garmax.materialflashlight.modules.FlashModule
import co.garmax.materialflashlight.modules.ModuleBase
import co.garmax.materialflashlight.modules.ModuleManager
import co.garmax.materialflashlight.modules.ScreenModule
import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module class ApplicationModule() {

    @Provides @Singleton
    fun provideModuleManager(context: Context, preferences: Preferences): ModuleManager {
        val manager = ModuleManager()
        val module = preferences.module;

        // Set module
        if (module == ModuleBase.MODULE_CAMERA_FLASHLIGHT) {
            manager.module = FlashModule(context);
        } else if (module == ModuleBase.MODULE_SCREEN) {
            manager.module = ScreenModule(context);
        } else {
            throw IllegalArgumentException("Unknown module type " + module)
        }

        return manager;
    }

    @Provides @Singleton
    fun providePreferences(context: Context): Preferences {
        return Preferences(context);
    }

    @Provides @Singleton
    fun provideTaskThread(): ExecutorService {
        return Executors.newFixedThreadPool(1);
    }
}
