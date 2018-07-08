package co.garmax.materialflashlight.features.modes;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Interrupted light with equal interval
 */
public class IntervalStrobeMode extends ModeBase {

    private static final int STROBE_PERIOD = 300;
    private static final int DELAY_PERIOD = 200;
    private Disposable disposable;

    @Override
    public void start() {
        long l = System.currentTimeMillis();//TODO
        disposable = Observable.interval(0, STROBE_PERIOD + DELAY_PERIOD, TimeUnit.MILLISECONDS)
                .doOnNext(any ->{
                    setLightState(true);
                    Timber.e("true - %s",System.currentTimeMillis()-l);
                })
                .delay(STROBE_PERIOD, TimeUnit.MILLISECONDS)
                .doOnNext(any -> {
                    setLightState(false);
                    Timber.e("false - %s",System.currentTimeMillis()-l);
                })
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
