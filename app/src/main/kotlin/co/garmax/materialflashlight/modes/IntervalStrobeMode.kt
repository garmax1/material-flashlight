package co.garmax.materialflashlight.modes

import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import co.garmax.materialflashlight.modules.ModuleManager

/**
 * Interrupted light with equal interval
 */
class IntervalStrobeMode(moduleManager: ModuleManager) : ModeBase(moduleManager) {

    private val mHandlerThread: HandlerThread
    private var mHandler: Handler ? = null

    init {
        mHandlerThread = HandlerThread(THREAD_NAME)
    }

    private val mStrobeTask = object : Runnable {

        var isStopped: Boolean = false

        override fun run() {
            // Exit if stopped
            if (isStopped) return

            moduleManager.turnOn();
            Thread.sleep(STROBE_DELAY)

            // Exit if stopped
            if (isStopped) return

            moduleManager.turnOff();

            mHandler!!.postDelayed(this, STROBE_PERIOD)
        }
    }

    override fun start() {
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper);
        Process.setThreadPriority(mHandlerThread.threadId,
                Process.THREAD_PRIORITY_BACKGROUND)
        mHandler!!.postDelayed(mStrobeTask, STROBE_PERIOD)
    }

    override fun stop() {
        mStrobeTask.isStopped = true
        mHandler!!.removeCallbacks(mStrobeTask)
        mHandlerThread.quit()
    }

    companion object {
        private const val THREAD_NAME = "IntervalStrobeThread";
        private const val STROBE_PERIOD = 300L;
        private const val STROBE_DELAY = 200L;
    }
}
