package co.garmax.materialflashlight.features.modes;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

/**
 * Implement SOS
 */
public class SosMode extends ModeBase {

    private static final int STROBE_SHORT = 400;
    private static final int STROBE_LONG = 900;
    private static final int DELAY_SHORT = 400;
    private static final int DELAY_LONG = 2500;

    private static final int SOS_PERIOD = (STROBE_SHORT * 3 + DELAY_SHORT * 3) * 2
            + 2 * DELAY_SHORT + 3 * STROBE_LONG
            + DELAY_LONG;

    private Disposable disposableInterval;
    private Scheduler workerScheduler;

    public SosMode(Scheduler workerScheduler) {
        this.workerScheduler = workerScheduler;
    }

    @Override
    public void start() {
        disposableInterval = Observable.interval(0,
                SOS_PERIOD,
                TimeUnit.MILLISECONDS,
                workerScheduler)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 short
                .delay(DELAY_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 short
                .delay(DELAY_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 short
                .delay(DELAY_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_LONG, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 long
                .delay(DELAY_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_LONG, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 long
                .delay(DELAY_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_LONG, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 long
                .delay(DELAY_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 short
                .delay(DELAY_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 short
                .delay(DELAY_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MAX_LIGHT_VOLUME))
                .delay(STROBE_SHORT, TimeUnit.MILLISECONDS)
                .doOnNext(any -> setBrightness(MIN_LIGHT_VOLUME))// 1 short
                .delay(DELAY_LONG, TimeUnit.MILLISECONDS)
                .subscribe(any -> {
                });
    }

    @Override
    public void stop() {
        setBrightness(MIN_LIGHT_VOLUME);
        if (disposableInterval != null) {
            disposableInterval.dispose();
        }
    }

    @Override
    public boolean checkPermissions() {
        return true;
    }
}
