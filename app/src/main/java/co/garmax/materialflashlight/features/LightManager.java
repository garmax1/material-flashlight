package co.garmax.materialflashlight.features;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import co.garmax.materialflashlight.R;
import co.garmax.materialflashlight.features.modes.IntervalStrobeMode;
import co.garmax.materialflashlight.features.modes.ModeBase;
import co.garmax.materialflashlight.features.modes.SosMode;
import co.garmax.materialflashlight.features.modes.SoundStrobeMode;
import co.garmax.materialflashlight.features.modes.TorchMode;
import co.garmax.materialflashlight.features.modules.CameraFlashModuleV16;
import co.garmax.materialflashlight.features.modules.CameraFlashModuleV23;
import co.garmax.materialflashlight.features.modules.ModuleBase;
import co.garmax.materialflashlight.features.modules.ScreenModule;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class LightManager {

    public enum Module {
        MODULE_SCREEN,
        MODULE_CAMERA_FLASHLIGHT
    }

    public enum Mode {
        MODE_OFF,
        MODE_SOUND_STROBE,
        MODE_INTERVAL_STROBE,
        MODE_TORCH,
        MODE_SOS
    }

    private Context context;
    private Scheduler workerScheduler;
    private Disposable disposableModeState;
    private final BehaviorSubject<Boolean> toggleStateObservable = BehaviorSubject.create();

    @Nullable
    private ModuleBase currentModule;
    @Nullable
    private ModeBase currentMode;

    public LightManager(Context context,
                        Scheduler workerScheduler) {
        this.workerScheduler = workerScheduler;
        this.context = context;
    }

    public Observable<Boolean> toggleStateStream() {
        return toggleStateObservable;
    }

    public boolean isTurnedOn() {
        return toggleStateObservable.getValue() == Boolean.TRUE;
    }

    public boolean isSupported() {
        return requireModule().isSupported();
    }

    public void turnOn() {
        if (isTurnedOn()) return;

        // Check that the module is supported
        if (!requireModule().isSupported()) {
            Toast.makeText(context,
                    R.string.toast_module_not_supported,
                    Toast.LENGTH_LONG).show();

            return;
        }

        // Check that the module is available
        if (!requireModule().isAvailable()) {
            Toast.makeText(context,
                    R.string.toast_module_not_available,
                    Toast.LENGTH_LONG).show();

            return;
        }

        // Check tht we have all permission for module and mode
        if (!requireModule().checkPermissions() || !requireMode().checkPermissions()) {
            return;
        }

        // Listen mode light state and set to module
        disposableModeState = requireMode()
                .brightnessObservable()
                .subscribe(brightness -> requireModule().setBrightness(brightness));

        requireModule().init();
        requireMode().start();

        setToggleState(true);

        WidgetProviderButton.updateWidgets(context);
    }

    private void setToggleState(boolean turnedOn) {
        if (turnedOn) {
            ForegroundService.startService(context);
        } else {
            ForegroundService.stopService(context);
        }

        toggleStateObservable.onNext(turnedOn);
    }

    public void turnOff() {
        if (!isTurnedOn()) return;

        requireMode().stop();
        requireModule().release();

        setToggleState(false);

        // Free observable
        disposableModeState.dispose();

        WidgetProviderButton.updateWidgets(context);
    }

    @NonNull
    private ModuleBase requireModule() {

        if (currentModule == null) {
            throw new IllegalStateException(Module.class.getName()
                    + " not set in " + getClass().getName());
        }

        return currentModule;
    }

    @NonNull
    private ModeBase requireMode() {

        if (currentMode == null) {
            throw new IllegalStateException(Mode.class.getName()
                    + " not set in " + getClass().getName());
        }

        return currentMode;
    }

    public void setMode(Mode mode) {

        boolean isWasTurnedOn = isTurnedOn();

        turnOff();

        if (mode == Mode.MODE_INTERVAL_STROBE) {
            currentMode = new IntervalStrobeMode(workerScheduler);
        } else if (mode == Mode.MODE_SOS) {
            currentMode = new SosMode(workerScheduler);
        } else if (mode == Mode.MODE_SOUND_STROBE) {
            currentMode = new SoundStrobeMode(context, workerScheduler);
        } else {
            currentMode = new TorchMode();
        }

        // Restart if was before
        if (isWasTurnedOn) {
            turnOn();
        }
    }

    public void setModule(Module module) {

        boolean isWasTurnedOn = isTurnedOn();

        turnOff();

        // Create new module
        if (module == Module.MODULE_SCREEN) {
            currentModule = new ScreenModule(context);
        } else if (module == Module.MODULE_CAMERA_FLASHLIGHT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                currentModule = new CameraFlashModuleV23(context);
            } else {
                currentModule = new CameraFlashModuleV16(context);
            }
        }

        // Restart if was before
        if (isWasTurnedOn) {
            turnOn();
        }
    }
}
