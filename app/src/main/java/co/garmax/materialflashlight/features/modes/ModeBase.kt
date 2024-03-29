package co.garmax.materialflashlight.features.modes

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * Module generate event for light volume according to implementation
 * (torch, interval, microphone volume)
 */
abstract class ModeBase {

    enum class Mode {
        MODE_SOUND_STROBE, MODE_INTERVAL_STROBE, MODE_TORCH, MODE_SOS
    }

    /**
     * Volume of the light
     */
    val lightVolumeSubject: Observable<Int> get() = _lightVolumeSubject
    private val _lightVolumeSubject: Subject<Int> = PublishSubject.create()

    /**
     * Start mode. Light will be turned on\off depends on mode implementation.
     */
    abstract fun start()

    /**
     * Stop mode. Light will be turned off.
     */
    abstract fun stop()

    /**
     * Check runtime permission for the mode
     * @return true if all needed permission granted, false - if permission requested
     */
    abstract fun checkPermissions(): Boolean

    /**
     * Change brightnessObservable state
     */
    fun setBrightness(percentage: Int) {
        _lightVolumeSubject.onNext(percentage)
    }

    companion object {
        const val MAX_LIGHT_VOLUME = 100
        const val MIN_LIGHT_VOLUME = 0
    }
}