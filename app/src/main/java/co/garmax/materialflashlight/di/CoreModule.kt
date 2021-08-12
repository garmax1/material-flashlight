package co.garmax.materialflashlight.di

import co.garmax.materialflashlight.features.LightManager
import co.garmax.materialflashlight.features.SettingsRepository
import co.garmax.materialflashlight.features.modes.ModeFactory
import co.garmax.materialflashlight.features.modules.ModuleFactory
import co.garmax.materialflashlight.ui.main.MainViewModel
import co.garmax.materialflashlight.ui.root.RootViewModel
import co.garmax.materialflashlight.utils.PostExecutionThread
import co.garmax.materialflashlight.widget.WidgetManager
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val coreModule = module {
    single<PostExecutionThread> {
        object : PostExecutionThread {
            override val scheduler: Scheduler get() = AndroidSchedulers.mainThread()
        }
    }
    single { Schedulers.io() }

    single {
        LightManager(get(), androidContext(), get(), get()).apply {
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

    viewModel { RootViewModel(get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get()) }

}