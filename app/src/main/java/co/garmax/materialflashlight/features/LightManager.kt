package co.garmax.materialflashlight.features

import android.content.Context
import android.widget.Toast
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.features.modes.IntervalStrobeMode
import co.garmax.materialflashlight.features.modes.ModeBase
import co.garmax.materialflashlight.features.modules.ModuleBase
import co.garmax.materialflashlight.widget.WidgetManager
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class LightManager(
    private val widgetManager: WidgetManager,
    private val context: Context
) {

    val isTurnedOn get() = _toggleStateObservable.value == true

    val isSupported get() = requireModule().isSupported

    val toggleStateStream: Observable<Boolean> get() = _toggleStateObservable
    private val _toggleStateObservable = BehaviorSubject.createDefault(false)

    private var disposableModeState: Disposable? = null

    private var currentModule: ModuleBase? = null

    private var currentMode: ModeBase? = null

    fun turnOn() {
        if (isTurnedOn) return

        // Check that the module is supported
        if (!requireModule().isSupported) {
            Toast.makeText(context, R.string.toast_module_not_supported, Toast.LENGTH_LONG).show()
            return
        }

        // Check that the module is available
        if (!requireModule().isAvailable) {
            Toast.makeText(context, R.string.toast_module_not_available, Toast.LENGTH_LONG).show()
            return
        }

        // Check tht we have all permission for module and mode
        if (!requireModule().checkPermissions() || !requireMode().checkPermissions()) {
            return
        }

        requireModule().init()

        // Listen mode light state and set to module
        disposableModeState = requireMode()
            .lightVolumeSubject
            .subscribe {
                requireModule().setBrightness(it)
            }

        requireMode().start()

        _toggleStateObservable.onNext(true)

        widgetManager.updateWidgets()
    }

    fun turnOff() {
        if (!isTurnedOn) return

        requireMode().stop()
        requireModule().release()
        requireModule().release()
        _toggleStateObservable.onNext(false)

        // Free observable
        disposableModeState?.dispose()
        widgetManager.updateWidgets()
    }

    private fun requireModule(): ModuleBase {
        return currentModule ?: throw IllegalStateException("Module not set")
    }

    private fun requireMode(): ModeBase {
        return currentMode ?: throw IllegalStateException("Mode not set")
    }

    fun setMode(mode: ModeBase?) {
        val isWasTurnedOn = isTurnedOn
        turnOff()
        currentMode = mode

        if (isWasTurnedOn) turnOn()
    }

    fun setModule(module: ModuleBase?) {
        val isWasTurnedOn = isTurnedOn
        turnOff()
        currentModule = module

        if (isWasTurnedOn) turnOn()
    }

    fun setStrobePeriod(timeOn: Int, timeOff: Int) {
        if (currentMode is IntervalStrobeMode) {
            (currentMode as IntervalStrobeMode).updateStrobe(timeOn, timeOff)
        }
    }
}