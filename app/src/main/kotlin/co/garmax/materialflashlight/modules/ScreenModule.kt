package co.garmax.materialflashlight.modules

import android.app.Activity
import android.content.Context

/**
 * Module for device screen
 */
//TODO implement module
class ScreenModule(context: Context) : ModuleBase(context) {

    override fun turnOn() {

    }

    override fun turnOff() {

    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun isSupported(): Boolean {
        return true
    }

    override fun setBrightnessVolume(percent: Int) {

    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun checkPermissions(requestCode: Int, activity: Activity): Boolean {
        return true
    }
}