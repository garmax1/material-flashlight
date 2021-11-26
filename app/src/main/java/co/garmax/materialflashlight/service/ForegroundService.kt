package co.garmax.materialflashlight.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.features.LightManager
import org.koin.android.ext.android.inject

/**
 * Service with notification
 */
class ForegroundService : Service() {

    private val lightManager: LightManager by inject()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return super.onStartCommand(intent, flags, startId)

        val command = intent.getIntExtra(EXTRA_COMMAND, COMMAND_STOP)

        if (command == COMMAND_START) {
            startForeground()
            lightManager.turnOn()
        } else {
            lightManager.turnOff()
            stop()
        }

        return START_STICKY
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    // Start foreground service with notification
    private fun startForeground() {

        val intent = Intent(applicationContext, ForegroundService::class.java).apply {
            putExtra(EXTRA_COMMAND, COMMAND_STOP)
        }

        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_light_notification)
            .setContentTitle(getString(R.string.notification_light))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setWhen(System.currentTimeMillis())
            .addAction(
                R.drawable.ic_power_off,
                getString(R.string.notification_tap_to_turn_off),
                pendingIntent
            )

        createNotificationChannel()

        startForeground(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val EXTRA_COMMAND = "extra_command"
        private const val CHANNEL_ID = "main"

        private const val COMMAND_STOP = 0
        private const val COMMAND_START = 1

        fun startService(context: Context) {
            Intent(context, ForegroundService::class.java).apply {
                putExtra(EXTRA_COMMAND, COMMAND_START)
            }.let {
                context.startService(it)
            }
        }

        fun stopService(context: Context) {
            Intent(context, ForegroundService::class.java).apply {
                putExtra(EXTRA_COMMAND, COMMAND_STOP)
            }.let {
                context.startService(it)
            }
        }
    }
}