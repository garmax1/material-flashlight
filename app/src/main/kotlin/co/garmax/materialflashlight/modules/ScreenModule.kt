package co.garmax.materialflashlight.modules

import android.app.Activity
import android.content.Context

/**
 * Module for device screen
 */
//TODO implement module
class ScreenModule(context: Context) : ModuleBase(context) {

    override fun turnOn() {
        throw UnsupportedOperationException()
    }

    override fun turnOff() {
        throw UnsupportedOperationException()
    }

    override fun isAvailable(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isSupported(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun setBrightnessVolume(percent: Int) {
        throw UnsupportedOperationException()
    }

    override fun start() {
        throw UnsupportedOperationException()
    }

    override fun stop() {
        throw UnsupportedOperationException()
    }

    override fun checkPermissions(requestCode: Int, activity: Activity): Boolean {
        throw UnsupportedOperationException()
    }
}