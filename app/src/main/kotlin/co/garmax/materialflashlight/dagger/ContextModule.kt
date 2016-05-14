package co.garmax.materialflashlight.dagger

import android.content.Context
import co.garmax.materialflashlight.Preferences
import co.garmax.materialflashlight.modules.FlashModule
import co.garmax.materialflashlight.modules.ModuleManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class ContextModule(val context: Context) {

    @Provides @Singleton
    fun provideContext(): Context {
        return context;
    }
}
