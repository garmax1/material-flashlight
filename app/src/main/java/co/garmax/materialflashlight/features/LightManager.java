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
    private SettingsRepository settingsRepository;
    private Scheduler workerScheduler;
    private Disposable disposableLightState;

    @Nullable
    private ModuleBase currentModule;

    public LightManager(Context context,
                        Scheduler workerScheduler,
                        SettingsRepository settingsRepository) {
        this.workerScheduler = workerScheduler;
        this.context = context;
        this.settingsRepository = settingsRepository;
    }

    public Observable<Boolean> turnStateStream() {
        return requireModule().turnState();
    }

    public boolean isTurnedOn() {
        return requireModule().isTurnedOn();
    }

    public void turnOn() {
        if(isTurnedOn()) return;

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

        // Check tht we have all permission for module
        if(requireModule().checkPermissions()) {
            // Listen state to start\stop foreground service
            disposableLightState = turnStateStream().subscribe(isTurnedOn->{
                if(isTurnedOn) {
                    ForegroundService.startService(context);
                } else {
                    ForegroundService.stopService(context);
                }
            });
            requireModule().turnOn();
        }
    }

    public void turnOff() {
        if (!requireModule().isTurnedOn()) return;

        requireModule().turnOff();

        // Free observable
        disposableLightState.dispose();

    }

    @NonNull
    private ModuleBase requireModule() {

        if(currentModule == null) {
            setModule(settingsRepository.getModule());
            setMode(settingsRepository.getMode());
        }

        return currentModule;
    }

    public void setMode(Mode mode) {

        ModeBase modeBase;

        if (mode == Mode.MODE_INTERVAL_STROBE) {
            modeBase = new IntervalStrobeMode(workerScheduler);
        } else if(mode == Mode.MODE_SOS) {
            modeBase = new SosMode(workerScheduler);
        } else if(mode == Mode.MODE_SOUND_STROBE) {
            modeBase = new SoundStrobeMode(context, workerScheduler);
        } else {
            modeBase = new TorchMode();
        }

        requireModule().setMode(modeBase);
    }

    public void setModule(Module module) {
        // Turn off current module
        if (currentModule != null && currentModule.isTurnedOn()) {
            currentModule.turnOff();
        }

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

        // Set mode to new module
        setMode(settingsRepository.getMode());
    }
}
