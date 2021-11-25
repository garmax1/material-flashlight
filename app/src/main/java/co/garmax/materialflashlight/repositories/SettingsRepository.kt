package co.garmax.materialflashlight.repositories

import android.content.Context
import co.garmax.materialflashlight.features.modes.IntervalStrobeMode
import co.garmax.materialflashlight.features.modes.ModeBase
import co.garmax.materialflashlight.features.modules.ModuleBase

class SettingsRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var isKeepScreenOn: Boolean
        get() = sharedPreferences.getBoolean(KEEP_SCREEN_ON, false)
        set(isKeepScreenOn) {
            sharedPreferences.edit().putBoolean(KEEP_SCREEN_ON, isKeepScreenOn).apply()
        }

    var isAutoTurnedOn: Boolean
        get() = sharedPreferences.getBoolean(AUTO_TURN_ON, false)
        set(isAutoTurnOn) {
            sharedPreferences.edit().putBoolean(AUTO_TURN_ON, isAutoTurnOn).apply()
        }

    var mode: ModeBase.Mode
        get() {
            val mode = sharedPreferences.getString(MODE, null)
            return mode?.let { ModeBase.Mode.valueOf(it) } ?: ModeBase.Mode.MODE_TORCH
        }
        set(mode) {
            sharedPreferences.edit().putString(MODE, mode.name).apply()
        }

    var module: ModuleBase.Module
        get() {
            val module = sharedPreferences.getString(MODULE, null)
            return module?.let { ModuleBase.Module.valueOf(it) }
                ?: ModuleBase.Module.MODULE_CAMERA_FLASHLIGHT
        }
        set(module) {
            sharedPreferences.edit().putString(MODULE, module.name).apply()
        }

    var strobeOnPeriod: Int
        get() {
            return sharedPreferences.getInt(STROBE_ON_PERIOD, IntervalStrobeMode.DEFAULT_STROBE_PERIOD)
        }
        set(value) {
            sharedPreferences.edit().putInt(STROBE_ON_PERIOD, value).apply()
        }

    var strobeOffPeriod: Int
        get() {
            return sharedPreferences.getInt(STROBE_OFF_PERIOD, IntervalStrobeMode.DEFAULT_DELAY_PERIOD)
        }
        set(value) {
            sharedPreferences.edit().putInt(STROBE_OFF_PERIOD, value).apply()
        }

    companion object {
        private const val KEEP_SCREEN_ON = "keep_screen_on"
        private const val MODE = "mode_name"
        private const val MODULE = "module_name"
        private const val AUTO_TURN_ON = "auto_turn_on"
        private const val STROBE_ON_PERIOD = "strobe_on_period"
        private const val STROBE_OFF_PERIOD = "strobe_off_period"
    }
}