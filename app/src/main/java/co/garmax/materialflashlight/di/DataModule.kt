package co.garmax.materialflashlight.di

import co.garmax.materialflashlight.features.LightManager
import co.garmax.materialflashlight.features.modes.ModeFactory
import co.garmax.materialflashlight.features.modules.ModuleFactory
import co.garmax.materialflashlight.repositories.SettingsRepository
import co.garmax.materialflashlight.utils.PostExecutionThread
import co.garmax.materialflashlight.widget.WidgetManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<PostExecutionThread> {
        object : PostExecutionThread {
            override val scheduler get() = AndroidSchedulers.mainThread()
        }
    }
    single { Schedulers.io() }

    single {
        LightManager(get(), androidContext()).apply {
            val settingsRepository: SettingsRepository = get()
            val moduleFactory: ModuleFactory = get()
            val modeFactory: ModeFactory = get()

            setModule(moduleFactory.getModule(settingsRepository.module))
            setMode(modeFactory.getMode(settingsRepository.mode))
        }
    }

    single { SettingsRepository(get()) }
    single { WidgetManager(androidContext()) }
    single { ModuleFactory(androidContext()) }
    single { ModeFactory(get(), androidContext()) }
}