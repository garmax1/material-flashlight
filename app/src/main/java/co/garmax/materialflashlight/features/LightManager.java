package co.garmax.materialflashlight.features;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import co.garmax.materialflashlight.R;
import co.garmax.materialflashlight.features.foreground.ForegroundServiceManager;
import co.garmax.materialflashlight.features.modes.ModeBase;
import co.garmax.materialflashlight.features.modules.ModuleBase;
import co.garmax.materialflashlight.widget.WidgetManager;
import co.garmax.materialflashlight.utils.ResourceProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class LightManager {

    private final ResourceProvider resourceProvider;
    private final ForegroundServiceManager foregroundServiceManager;
    private final WidgetManager widgetManager;

    private Disposable disposableModeState;
    private final BehaviorSubject<Boolean> toggleStateObservable
            = BehaviorSubject.createDefault(Boolean.FALSE);

    @Nullable
    private ModuleBase currentModule;
    @Nullable
    private ModeBase currentMode;

    public LightManager(WidgetManager widgetManager,
                        ForegroundServiceManager foregroundServiceManager,
                        ResourceProvider resourceProvider) {
        this.widgetManager = widgetManager;
        this.foregroundServiceManager = foregroundServiceManager;
        this.resourceProvider = resourceProvider;
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
            resourceProvider.showToast(R.string.toast_module_not_supported);

            return;
        }

        // Check that the module is available
        if (!requireModule().isAvailable()) {
            resourceProvider.showToast(R.string.toast_module_not_available);

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

        widgetManager.updateWidgets();
    }

    private void setToggleState(boolean turnedOn) {
        if (turnedOn) {
            foregroundServiceManager.startService();
        } else {
            foregroundServiceManager.stopService();
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

        widgetManager.updateWidgets();
    }

    @NonNull
    private ModuleBase requireModule() {

        if (currentModule == null) {
            throw new IllegalStateException(ModuleBase.Module.class.getName()
                    + " not set in " + getClass().getName());
        }

        return currentModule;
    }

    @NonNull
    private ModeBase requireMode() {

        if (currentMode == null) {
            throw new IllegalStateException(ModeBase.Mode.class.getName()
                    + " not set in " + getClass().getName());
        }

        return currentMode;
    }

    public void setMode(ModeBase mode) {

        boolean isWasTurnedOn = isTurnedOn();

        turnOff();

        currentMode = mode;

        // Restart if was before
        if (isWasTurnedOn) {
            turnOn();
        }
    }

    public void setModule(ModuleBase module) {

        boolean isWasTurnedOn = isTurnedOn();

        turnOff();

        currentModule = module;

        // Restart if was before
        if (isWasTurnedOn) {
            turnOn();
        }
    }
}
