package co.garmax.materialflashlight.modules

import android.app.Activity
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * Work with hardware light modules
 */
class ModuleManager {

    interface OnLightStateChangedListener {
        fun stateChanged(turnedOn: Boolean)
    }

    private var mIsRunning: Boolean = false

    private var mOnLightStateChangedListener: WeakReference<OnLightStateChangedListener>? = null

    var module: ModuleBase ? = null

    fun turnOff() {
        if(!isAvailable()) {
            Timber.w("turnOff() called when module not available");
            return;
        }

        module!!.turnOff()
    }

    fun turnOn() {
        if(!isAvailable()) {
            Timber.w("turnOn() called when module not available");
            return;
        }

        module!!.turnOn()
    }

    fun start() {
        module!!.start()

        if (module!!.isAvailable()) {
            mIsRunning = true
            mOnLightStateChangedListener?.get()?.stateChanged(mIsRunning)
        } else {
            Timber.e("Module not started");
        }
    }

    fun stop() {
        if(!isAvailable()) {
            Timber.w("stop() called when module not available");
            return;
        }

        module!!.stop()
        mIsRunning = false
        mOnLightStateChangedListener?.get()?.stateChanged(mIsRunning)
    }

    fun isRunning(): Boolean {
        return mIsRunning
    }

    fun isSupported(): Boolean {
        return module!!.isSupported()
    }

    fun isAvailable(): Boolean {
        return module!!.isAvailable()
    }

    fun setBrightnessVolume(volume: Int) {
        if(!isAvailable()) {
            Timber.w("setBrightnessVolume() called when module not available");
            return;
        }

        module!!.setBrightnessVolume(volume)
    }

    fun setOnLightStateChangedListener(onLightStateChangedListener: OnLightStateChangedListener) {
        mOnLightStateChangedListener = WeakReference(onLightStateChangedListener)
    }

    fun checkPermissions(requestCode: Int, activity: Activity): Boolean {
        return module!!.checkPermissions(requestCode, activity)
    }
}
