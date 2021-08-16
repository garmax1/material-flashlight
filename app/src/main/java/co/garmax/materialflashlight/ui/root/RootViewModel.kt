package co.garmax.materialflashlight.ui.root

import androidx.lifecycle.ViewModel
import co.garmax.materialflashlight.extensions.liveDataOf
import co.garmax.materialflashlight.features.LightManager
import co.garmax.materialflashlight.repositories.SettingsRepository
import co.garmax.materialflashlight.utils.PostExecutionThread
import io.reactivex.disposables.Disposable

class RootViewModel(
    postExecutionThread: PostExecutionThread,
    private val lightManager: LightManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val liveDataLightToggle = liveDataOf<Boolean>()

    val isAutoTurnOn get() = settingsRepository.isAutoTurnedOn

    val lightModule get() = settingsRepository.module

    private var disposableLightToggle: Disposable? = null

    init {
        disposableLightToggle = lightManager
            .toggleStateStream
            .observeOn(postExecutionThread.scheduler)
            .subscribe { liveDataLightToggle.value = it }
    }

    fun toggleLight(isTurnOn: Boolean) {
        if (isTurnOn) lightManager.turnOn() else lightManager.turnOff()
    }

    override fun onCleared() {
        super.onCleared()

        disposableLightToggle?.dispose()
    }
}