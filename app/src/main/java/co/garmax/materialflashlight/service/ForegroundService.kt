package co.garmax.materialflashlight.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.features.LightManager
import org.koin.android.ext.android.inject
import org.koin.core.context.GlobalContext

/**
 * Keeps the process alive while the light is on.
 */
class ForegroundService : Service() {

    private val lightManager: LightManager by inject()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return super.onStartCommand(intent, flags, startId)

        val command = intent.getIntExtra(EXTRA_COMMAND, COMMAND_STOP)

        if (command == COMMAND_START) {
            startForegroundWithNotification()
            lightManager.turnOn()
            if (!lightManager.isTurnedOn) {
                stop()
            }
        } else {
            lightManager.turnOff()
            stop()
        }

        return START_STICKY
    }

    private fun stop() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForegroundWithNotification() {
        val stopIntent = Intent(applicationContext, ForegroundService::class.java).apply {
            putExtra(EXTRA_COMMAND, COMMAND_STOP)
        }

        val pendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_light_notification)
            .setContentTitle(getString(R.string.notification_light))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .setWhen(System.currentTimeMillis())
            .addAction(
                R.drawable.ic_power_off,
                getString(R.string.notification_tap_to_turn_off),
                pendingIntent
            )
            .build()

        createNotificationChannel()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_description)
                setShowBadge(false)
            }
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
            val lightManager = GlobalContext.get().get<LightManager>()
            if (!lightManager.ensureRuntimePermissions()) return

            Intent(context, ForegroundService::class.java).apply {
                putExtra(EXTRA_COMMAND, COMMAND_START)
            }.let {
                ContextCompat.startForegroundService(context, it)
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
