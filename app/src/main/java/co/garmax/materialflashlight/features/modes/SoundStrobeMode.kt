package co.garmax.materialflashlight.features.modes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import co.garmax.materialflashlight.ui.PermissionsActivity
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Interrupted light depending on the around noise volume
 */
class SoundStrobeMode(
    private val context: Context,
    private val workerScheduler: Scheduler
) : ModeBase() {

    private var disposableInterval: Disposable? = null

    // Audio staff
    private var audioRecord: AudioRecord? = null
    private var bufferSize = 0
    private var maxAmplitude = 0
    private var minAmplitude = 0

    override fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionsActivity.startActivity(context, arrayOf(Manifest.permission.RECORD_AUDIO))
            return false
        }
        return true
    }

    override fun start() {
        bufferSize = AudioRecord.getMinBufferSize(
            8000, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, bufferSize
        ).apply {
            startRecording()
        }
        minAmplitude = 0
        maxAmplitude = 0
        disposableInterval = Observable.interval(
            0,
            CHECK_AMPLITUDE_PERIOD,
            TimeUnit.MILLISECONDS,
            workerScheduler
        )
            .subscribe { any: Long? -> setBrightness(amplitudePercentage(amplitude)) }

        setBrightness(MAX_AMPLITUDE)
    }

    override fun stop() {
        setBrightness(MIN_LIGHT_VOLUME)
        disposableInterval?.dispose()

        audioRecord?.stop()
    }

    private val amplitude: Int
        get() {
            val buffer = ShortArray(bufferSize)
            audioRecord?.read(buffer, 0, bufferSize)
            var max: Short = 0
            for (s in buffer) {
                if (s > max) {
                    max = s
                }
            }
            return max.toInt()
        }

    private fun amplitudePercentage(curAmplitude: Int): Int {
        // Reduce amplitude min\max tunnel
        // because min\max value can be above or below initial avg value
        // and limit with 0 and max amplitude value
        maxAmplitude = Math.max(maxAmplitude - INCREASE_STEP, 0)
        minAmplitude = Math.min(minAmplitude + INCREASE_STEP, MAX_AMPLITUDE)

        // Save min max values
        maxAmplitude = Math.max(maxAmplitude, curAmplitude)
        minAmplitude = Math.min(minAmplitude, curAmplitude)

        // If min and max equal, exit to prevent dividing by zero
        if (minAmplitude == maxAmplitude) {
            return 0
        }

        // Calculate percentage of current amplitude of difference max and min amplitude
        val avgAmplitude = (curAmplitude - minAmplitude) * 100 / (maxAmplitude - minAmplitude)
        Timber.d("Sound amplitude min: $minAmplitude, max: $maxAmplitude, cur: $curAmplitude; avg: $avgAmplitude")
        return avgAmplitude
    }

    companion object {
        private const val CHECK_AMPLITUDE_PERIOD = 50L
        private const val INCREASE_STEP = 150
        private const val MAX_AMPLITUDE = 32767
    }
}