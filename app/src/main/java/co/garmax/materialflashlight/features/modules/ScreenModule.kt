package co.garmax.materialflashlight.features.modules

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import co.garmax.materialflashlight.ui.root.RootActivity
import timber.log.Timber

/**
 * Module for device screen
 */
class ScreenModule internal constructor(private val context: Context) : ModuleBase {

    override val isAvailable get() = true
    override val isSupported get() = true

    private var previousScreenBrightness = -1
    private var previousBrightnessMode = -1

    override fun init() {
        // Save initial values
        try {
            previousScreenBrightness = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
            previousBrightnessMode = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            )
        } catch (e: SettingNotFoundException) {
            Timber.e(e, "Can't read screen brightnessObservable settings")
        }

        // Set system values
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255)
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )

        // Open activity with light screen
        context.startActivity(
            Intent(
                context,
                RootActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun release() {
        // Restore system values
        if (previousScreenBrightness >= 0) {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, previousScreenBrightness
            )
        }
        if (previousBrightnessMode >= 0) {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE, previousBrightnessMode
            )
        }
    }

    override fun setBrightness(percents: Int) {
        // Pass value to the screen light
        val intent = Intent(ACTION_SCREEN_MODULE)
        intent.putExtra(EXTRA_BRIGHTNESS_PERCENT, percents)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {
            Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.let {
                context.startActivity(it)
            }

            return false
        }
        return true
    }

    companion object {
        const val ACTION_SCREEN_MODULE = "action_screen_module"
        const val EXTRA_BRIGHTNESS_PERCENT = "extra_brightness_percent"
    }
}