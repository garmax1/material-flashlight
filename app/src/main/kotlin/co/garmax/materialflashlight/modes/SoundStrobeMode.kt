package co.garmax.materialflashlight.modes

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import co.garmax.materialflashlight.modules.ModuleManager
import timber.log.Timber

/**
 * Interrupted light depending on the around noise volume
 */
class SoundStrobeMode(moduleManager: ModuleManager) : ModeBase(moduleManager) {

    private val mAudioRecord: AudioRecord
    internal var mBufferSize: Int
    private val mHandlerThread: HandlerThread
    private var mHandler: Handler ? = null

    init {
        mHandlerThread = HandlerThread(THREAD_NAME)

        mBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
    }

    private val mStrobeTask = object : Runnable {

        var isStopped:Boolean = false
        private var mMaxAmplitude: Int = 0
        private var mMinAmplitude: Int = 0

        override fun run() {
            // Do not run task if stopped
            if(!isStopped) {
                moduleManager.setBrightnessVolume(amplitudePercentage(getAmplitude()));

                mHandler!!.postDelayed(this, CHECK_AMPLITUDE_PERIOD)
            }
        }

        private fun getAmplitude(): Int {
            var buffer = ShortArray(mBufferSize)
            mAudioRecord.read(buffer, 0, mBufferSize);
            var max: Short = 0
            for (s in buffer) {
                if (s > max) {
                    max = s;
                }
            }
            return max.toInt();
        }

        private fun amplitudePercentage(curAmplitude: Int): Int {
            // Reduce amplitude min\max tunnel
            // because min\max value can be above or below initial avg value
            // and limit with 0 and max amplitude value
            mMaxAmplitude = Math.max(mMaxAmplitude - INCREASE_STEP, 0)
            mMinAmplitude = Math.min(mMinAmplitude + INCREASE_STEP, MAX_AMPLITUDE)

            // Save min max values
            mMaxAmplitude = Math.max(mMaxAmplitude, curAmplitude)
            mMinAmplitude = Math.min(mMinAmplitude, curAmplitude)

            // If min and max equal, exit to prevent dividing by zero
            if(mMinAmplitude == mMaxAmplitude) {
                return 0;
            }

            // Calculate percentage of current amplitude of difference max and min amplitude
            var avgAmplitude = (curAmplitude - mMinAmplitude) * 100 /
                    (mMaxAmplitude - mMinAmplitude)

            Timber.d("Sound amplitude min: %d, max: %d, cur: %d; avg: %d", mMinAmplitude, mMaxAmplitude,
                    curAmplitude, avgAmplitude)

            return avgAmplitude;
        }
    }

    override fun start() {
        mAudioRecord.startRecording();
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper);
        Process.setThreadPriority(mHandlerThread.threadId,
                Process.THREAD_PRIORITY_BACKGROUND)
        mHandler!!.postDelayed(mStrobeTask, CHECK_AMPLITUDE_PERIOD)
    }

    override fun stop() {
        mAudioRecord.stop();
        mStrobeTask.isStopped = true
        mHandler!!.removeCallbacks(mStrobeTask)
        mHandlerThread.quit()
    }

    companion object {
        private const val THREAD_NAME = "SoundStrobeThread";
        // Period we read amplitude
        private const val CHECK_AMPLITUDE_PERIOD = 50L;
        private const val INCREASE_STEP = 150;
        private const val MAX_AMPLITUDE = 32767;

        fun checkPermissions(requestCode: Int, activity: Activity): Boolean {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), requestCode);

                return false;
            }

            return true;
        }
    }
}