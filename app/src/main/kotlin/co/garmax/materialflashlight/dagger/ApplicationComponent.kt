package co.garmax.materialflashlight.dagger

import co.garmax.materialflashlight.modes.ModeService
import co.garmax.materialflashlight.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(ApplicationModule::class, ContextModule::class))
@Singleton
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(customService: ModeService)
}