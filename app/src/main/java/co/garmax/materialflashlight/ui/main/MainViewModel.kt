package co.garmax.materialflashlight.ui.main

import androidx.lifecycle.ViewModel
import co.garmax.materialflashlight.extensions.liveDataOf
import co.garmax.materialflashlight.features.LightManager
import co.garmax.materialflashlight.features.modes.ModeBase
import co.garmax.materialflashlight.features.modes.ModeFactory
import co.garmax.materialflashlight.features.modules.ModuleBase
import co.garmax.materialflashlight.features.modules.ModuleFactory
import co.garmax.materialflashlight.repositories.SettingsRepository
import co.garmax.materialflashlight.utils.PostExecutionThread
import io.reactivex.disposables.Disposable

class MainViewModel(
    postExecutionThread: PostExecutionThread,
    private val lightManager: LightManager,
    private val settingsRepository: SettingsRepository,
    private val modeFactory: ModeFactory,
    private val moduleFactory: ModuleFactory
) : ViewModel() {

    val liveDataLightToggle = liveDataOf<Boolean>()

    var isAutoTurnedOn: Boolean
        get() = settingsRepository.isAutoTurnedOn
        set(value) {
            settingsRepository.isAutoTurnedOn = value
        }

    var isKeepScreenOn: Boolean
        get() = settingsRepository.isKeepScreenOn
        set(value) {
            settingsRepository.isKeepScreenOn = value
        }

    val isLightTurnedOn get() = lightManager.isTurnedOn

    val lightMode get() = settingsRepository.mode

    val lightModule get() = settingsRepository.module

    val strobeOnPeriod get() = settingsRepository.strobeOnPeriod
    val strobeOffPeriod get() = settingsRepository.strobeOffPeriod

    private var disposableLightToggle: Disposable? = null

    init {
        disposableLightToggle = lightManager
            .toggleStateStream
            .observeOn(postExecutionThread.scheduler)
            .subscribe { liveDataLightToggle.value = it }
    }

    fun setMode(mode: ModeBase.Mode) {
        settingsRepository.mode = mode
        lightManager.setMode(modeFactory.getMode(mode))
    }

    fun setModule(module: ModuleBase.Module) {
        settingsRepository.module = module
        lightManager.setModule(moduleFactory.getModule(module))
    }

    fun setStrobePeriod(timeOn: Int, timeOff: Int) {
        settingsRepository.strobeOnPeriod = timeOn
        settingsRepository.strobeOffPeriod = timeOff
        lightManager.setStrobePeriod(timeOn, timeOff)
    }

    override fun onCleared() {
        super.onCleared()

        disposableLightToggle?.dispose()
    }
}