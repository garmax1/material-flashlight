package co.garmax.materialflashlight.modes

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.widget.Toast
import co.garmax.materialflashlight.CustomApplication
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.modules.ModuleManager
import java.util.concurrent.ExecutorService
import javax.inject.Inject

/**
 * Used to work with light manager in background
 */
class ModeService : Service() {

    private var mCurrentMode: ModeBase? = null

    @Inject lateinit var mModuleManager: ModuleManager
    @Inject lateinit var mExecutorService: ExecutorService

    override fun onCreate() {
        super.onCreate()

        (application as CustomApplication).applicationComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent == null) {
            return super.onStartCommand(intent, flags, startId)
        }

        // Get mode
        val mode = intent.getIntExtra(EXTRA_MODE, ModeBase.MODE_OFF)

        // Execute on background thread
        mExecutorService.execute(fun () {

            // Handle mode
            if (mode == ModeBase.MODE_OFF) {

                stop()

            } else {
                startForeground()

                // Start module
                if (!mModuleManager.isRunning()) {
                    mModuleManager.start()

                    // Show error if module not available now
                    if (!mModuleManager.isAvailable()) {
                        Toast.makeText(applicationContext, R.string.toast_module_not_available, Toast.LENGTH_LONG).show()

                        return
                    }
                }
                // Stop previous mode
                else {
                    mCurrentMode?.stop()
                }

                // Start new module
                if (mode == ModeBase.MODE_TORCH) {
                    mCurrentMode = TorchMode(mModuleManager)
                } else if (mode == ModeBase.MODE_INTERVAL_STROBE) {
                    mCurrentMode = IntervalStrobeMode(mModuleManager)
                } else if (mode == ModeBase.MODE_SOUND_STROBE) {
                    mCurrentMode = SoundStrobeMode(mModuleManager)
                }

                mCurrentMode!!.start()
            }
        })

        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {

        mCurrentMode?.stop()

        mModuleManager.stop()

        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Start foreground service with notification
     */
    fun startForeground() {
        val notifyBuilder = NotificationCompat.Builder(applicationContext)
        notifyBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_light_notification)
                .setWhen(System.currentTimeMillis())
                .setContentText(getString(R.string.notification_tap_to_turn_off))
                .setContentTitle(getString(R.string.notification_light))

        val notificationIntent = buildIntent(applicationContext, ModeBase.MODE_OFF)
        val pendingIntent = PendingIntent.getService(applicationContext, 0, notificationIntent, 0)
        notifyBuilder.setContentIntent(pendingIntent)

        startForeground(NOTIFICATION_ID, notifyBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mModuleManager.isRunning()) {
            stop()
        }
    }

    companion object {

        private const val EXTRA_MODE = "extra_mode"
        private const val NOTIFICATION_ID = 1

        fun setMode(context: Context, mode: Int) {
            context.startService(buildIntent(context, mode))
        }

        /**
         * Build intent with command for service
         */
        private fun buildIntent(context: Context, mode: Int): Intent {
            val intent = Intent(context, ModeService::class.java)
            intent.putExtra(EXTRA_MODE, mode)

            return intent;
        }
    }
}
