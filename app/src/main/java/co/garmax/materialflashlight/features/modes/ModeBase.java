package co.garmax.materialflashlight.features.modes;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class ModeBase {

    private Subject<Boolean> lightStateSubject = PublishSubject.create();
    private Subject<Integer> brightnessSubject = PublishSubject.create();

    /**
     * Start mode. Light will turned on\off depends on mode implmentation.
     */
    public abstract void start();

    /**
     * Stop mode/ Light will turned off.
     */
    public abstract void stop();

    /**
     * Check runtime permission for the mode
     * @return true if all needed permission granted, false - if permission requested
     */
    public abstract boolean checkPermissions();

    /**
     * Stream of light state
     */
    public Observable<Boolean> lightState(){
        return lightStateSubject;
    }

    /**
     * Stream of brightness volume
     */
    public Observable<Integer> brightness(){
        return brightnessSubject;
    }

    /**
     * Change light state
     */
    protected void setLightState(boolean lightState){
        lightStateSubject.onNext(lightState);
    }

    /**
     * Change brightness state
     */
    protected void setBrightness(int percentage){
        brightnessSubject.onNext(percentage);
    }
}
