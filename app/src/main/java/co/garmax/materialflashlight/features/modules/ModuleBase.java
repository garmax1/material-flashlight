package co.garmax.materialflashlight.features.modules;

import android.support.annotation.NonNull;

import co.garmax.materialflashlight.features.modes.ModeBase;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class ModuleBase {

    private ModeBase currentMode;
    private Disposable disposableLightState;
    private Disposable disposableBrightness;

    private BehaviorSubject<Boolean> turnStateObservable = BehaviorSubject.create();

    public Observable<Boolean> turnState() {
        return turnStateObservable;
    }

    public void setMode(@NonNull ModeBase mode) {
        // Clear previous mode
        invalidateMode();

        this.currentMode = mode;

        disposableBrightness = currentMode.brightness()
                .subscribe(this::setBrightness);

        disposableLightState = currentMode.lightState()
                .subscribe(light -> {
                    if (light) {
                        lightOn();
                    } else {
                        lightOff();
                    }
                });

        if (isTurnedOn()) {
            // Check that we can
            if (!checkPermissions()) {
                turnOff();
            } else {
                currentMode.start();
            }
        }
    }

    /**
     * Turn on module
     */
    public void turnOn() {
        if (currentMode == null) {
            throw new IllegalStateException("Mode is null in the current module");
        }
        currentMode.start();
        turnStateObservable.onNext(true);
    }

    /**
     * Turn off module
     */
    public void turnOff() {
        invalidateMode();
        turnStateObservable.onNext(false);
    }

    /**
     * Turn on light in the module
     */
    protected abstract void lightOn();

    /**
     * Turn off light in the module
     */
    protected abstract void lightOff();

    /**
     * Can we use this the module now or not.
     */
    public abstract boolean isAvailable();

    /**
     * Hardware support the module or not.
     */
    public abstract boolean isSupported();

    /**
     * Set light brightness in percents
     */
    protected abstract void setBrightness(int percents);

    /**
     * Check if module request runtime permissions and call permissions dialog if needed
     * Return true if permission do not required otherwise false
     */
    protected abstract boolean checkModulePermissions();

    /**
     * Check module permission and for current mode
     */
    public boolean checkPermissions() {
        return checkModulePermissions() && currentMode.checkPermissions();
    }

    private void invalidateMode() {
        if (currentMode != null) {
            currentMode.stop();
            disposableLightState.dispose();
            disposableBrightness.dispose();
        }
    }

    public boolean isTurnedOn() {
        return turnStateObservable.getValue() == Boolean.TRUE;
    }
}
