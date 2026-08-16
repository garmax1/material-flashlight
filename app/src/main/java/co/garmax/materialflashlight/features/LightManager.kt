package co.garmax.materialflashlight.features

import android.content.Context
import android.content.pm.PackageManager
import android.os.PowerManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.features.modes.IntervalStrobeMode
import co.garmax.materialflashlight.features.modes.ModeBase
import co.garmax.materialflashlight.features.modules.ModuleBase
import co.garmax.materialflashlight.ui.PermissionsActivity
import co.garmax.materialflashlight.widget.WidgetManager
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

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

    private val wakeLock: PowerManager.WakeLock by lazy {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "materialflashlight:light"
        ).apply { setReferenceCounted(false) }
    }

    /**
     * Collects all missing runtime permissions for current module/mode and requests them once.
     * @return true if all already granted
     */
    fun ensureRuntimePermissions(): Boolean {
        val missing = buildList {
            addAll(requireModule().requiredRuntimePermissions())
            addAll(requireMode().requiredRuntimePermissions())
        }
            .distinct()
            .filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

        if (missing.isNotEmpty()) {
            PermissionsActivity.startActivity(context, missing.toTypedArray())
            return false
        }
        return true
    }

    fun toggle() {
        if (isTurnedOn) turnOff() else turnOn()
    }

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

        if (!ensureRuntimePermissions()) return

        // Additional non-runtime checks (e.g. WRITE_SETTINGS for screen module)
        if (!requireModule().checkPermissions() || !requireMode().checkPermissions()) {
            return
        }

        acquireWakeLock()

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
        _toggleStateObservable.onNext(false)

        // Free observable
        disposableModeState?.dispose()
        releaseWakeLock()
        widgetManager.updateWidgets()
    }

    private fun acquireWakeLock() {
        try {
            if (!wakeLock.isHeld) {
                // Keep CPU awake so SOS/strobe timers keep running with the screen off.
                wakeLock.acquire(60 * 60 * 1000L) // 1 hour max; renewed on each turnOn
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to acquire wake lock")
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to release wake lock")
        }
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
