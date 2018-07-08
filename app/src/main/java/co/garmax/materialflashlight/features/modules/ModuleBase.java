package co.garmax.materialflashlight.features.modules;

import android.support.annotation.NonNull;

import co.garmax.materialflashlight.features.modes.ModeBase;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class ModuleBase {

    private ModeBase currentMode;
    private boolean isTurnedOn = false;
    private Disposable disposableLightState;
    private Disposable disposableBrightness;

    private Subject<Boolean> turnStateObservable = PublishSubject.create();

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

        if (isTurnedOn) {
            currentMode.start();
        }
    }

    /**
     * Turn on module
     */
    public void turnOn() {
        if(!isTurnedOn) {
            if (currentMode == null) {
                throw new IllegalStateException("Mode is null in the current module");
            }
            currentMode.start();
            isTurnedOn = true;
            turnStateObservable.onNext(isTurnedOn);
        }
    }

    /**
     * Turn off module
     */
    public void turnOff() {
        if (isTurnedOn) {
            isTurnedOn = false;
            invalidateMode();
            turnStateObservable.onNext(isTurnedOn);
        }
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
        return isTurnedOn;
    }
}
