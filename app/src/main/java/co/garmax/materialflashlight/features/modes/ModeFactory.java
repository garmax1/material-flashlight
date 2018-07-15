package co.garmax.materialflashlight.features.modes;

import android.content.Context;

import io.reactivex.Scheduler;

public class ModeFactory {

    private final Scheduler workerScheduler;
    private final Context context;

    public ModeFactory(Scheduler workerScheduler, Context context) {
        this.workerScheduler = workerScheduler;
        this.context = context;
    }

    public ModeBase getMode(ModeBase.Mode mode) {
        if (mode == ModeBase.Mode.MODE_INTERVAL_STROBE) {
            return new IntervalStrobeMode(workerScheduler);
        } else if (mode == ModeBase.Mode.MODE_SOS) {
            return new SosMode(workerScheduler);
        } else if (mode == ModeBase.Mode.MODE_SOUND_STROBE) {
            return new SoundStrobeMode(context, workerScheduler);
        } else if (mode == ModeBase.Mode.MODE_TORCH) {
            return new TorchMode();
        }

        throw new IllegalArgumentException(mode + " mode not implemented");
    }
}
