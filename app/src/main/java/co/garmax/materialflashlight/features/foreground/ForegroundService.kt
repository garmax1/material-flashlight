package co.garmax.materialflashlight.features.foreground

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.features.LightManager
import org.koin.android.ext.android.inject

/**
 * System won't kill our app with foreground service
 */
class ForegroundService : Service() {

    private val lightManager: LightManager by inject()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return super.onStartCommand(intent, flags, startId)

        val mode = intent.getIntExtra(EXTRA_MODE, MODE_STOP)

        // Handle mode
        if (mode == MODE_LIGHT_OFF) {
            lightManager.turnOff()
        } else if (mode == MODE_STOP) {
            stop()
        } else if (mode == MODE_START) {
            startForeground()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    // Start foreground service with notification
    private fun startForeground() {
        val notifyBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        notifyBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_light_notification)
            .setWhen(System.currentTimeMillis())
            .setContentText(getString(R.string.notification_tap_to_turn_off))
            .setContentTitle(getString(R.string.notification_light))
        val notificationIntent = buildIntent(applicationContext, MODE_LIGHT_OFF)
        val pendingIntent = PendingIntent.getService(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        notifyBuilder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        startForeground(NOTIFICATION_ID, notifyBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name: CharSequence = getString(R.string.channel_name)
        val description = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val EXTRA_MODE = "extra_mode"
        private const val CHANNEL_ID = "MaterialFlashLight.Notification.Channel"
        const val MODE_STOP = 0
        const val MODE_START = 1
        const val MODE_LIGHT_OFF = 2

        fun startService(context: Context) {
            context.startService(buildIntent(context, MODE_START))
        }

        fun stopService(context: Context) {
            context.startService(buildIntent(context, MODE_STOP))
        }

        private fun buildIntent(context: Context?, mode: Int): Intent {
            val intent = Intent(context, ForegroundService::class.java)
            intent.putExtra(EXTRA_MODE, mode)
            return intent
        }
    }
}