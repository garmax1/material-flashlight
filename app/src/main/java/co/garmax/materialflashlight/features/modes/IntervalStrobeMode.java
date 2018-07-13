package co.garmax.materialflashlight.features.modes;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

/**
 * Interrupted light with equal interval
 */
public class IntervalStrobeMode extends ModeBase {

    private static final int STROBE_PERIOD = 300;
    private static final int DELAY_PERIOD = 200;
    private Disposable disposable;
    private Scheduler workerScheduler;

    public IntervalStrobeMode(Scheduler workerScheduler) {
        this.workerScheduler = workerScheduler;
    }

    @Override
    public void start() {
        disposable = Observable.interval(0,
                STROBE_PERIOD + DELAY_PERIOD,
                TimeUnit.MILLISECONDS,
                workerScheduler)
                .doOnNext(any -> setLightState(true))
                .delay(STROBE_PERIOD, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setLightState(false))
                .delay(DELAY_PERIOD, TimeUnit.MILLISECONDS)
                .subscribe(any -> {});
    }

    @Override
    public void stop() {
        if(disposable != null) {
            disposable.dispose();
        }
        setLightState(false);
    }

    @Override
    public boolean checkPermissions() {
        return true;
    }
}
