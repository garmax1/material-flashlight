package co.garmax.materialflashlight.features.modes;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Module generate event for light volume according to implementation
 * (torch, interval, microphone volume)
 */
public abstract class ModeBase {

    static int MAX_LIGHT_VOLUME = 100;
    static int MIN_LIGHT_VOLUME = 0;

    /**
     * Volume of the light
     */
    private Subject<Integer> lightVolumeSubject = PublishSubject.create();

    /**
     * Start mode. Light will be turned on\off depends on mode implementation.
     */
    public abstract void start();

    /**
     * Stop mode. Light will be turned off.
     */
    public abstract void stop();

    /**
     * Check runtime permission for the mode
     * @return true if all needed permission granted, false - if permission requested
     */
    public abstract boolean checkPermissions();

    /**
     * Stream of brightnessObservable volume
     */
    public Observable<Integer> brightnessObservable(){
        return lightVolumeSubject;
    }

    /**
     * Change brightnessObservable state
     */
    void setBrightness(int percentage){
        lightVolumeSubject.onNext(percentage);
    }
}
