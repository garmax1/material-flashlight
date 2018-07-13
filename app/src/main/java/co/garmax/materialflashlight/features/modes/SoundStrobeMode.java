package co.garmax.materialflashlight.features.modes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.content.ContextCompat;

import java.util.concurrent.TimeUnit;

import co.garmax.materialflashlight.ui.PermissionsActivity;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Interrupted light depending on the around noise volume
 */
public class SoundStrobeMode extends ModeBase {

    private static final long CHECK_AMPLITUDE_PERIOD = 50L;
    private static final int INCREASE_STEP = 150;
    private static final int MAX_AMPLITUDE = 32767;

    private Scheduler workerScheduler;
    private Context context;
    private Disposable disposable;

    // Audio staff
    private AudioRecord audioRecord;
    private int bufferSize;
    private int maxAmplitude;
    private int minAmplitude;

    public SoundStrobeMode(Context context, Scheduler workerScheduler) {
        this.context = context;
        this.workerScheduler = workerScheduler;
    }

    @Override
    public boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            PermissionsActivity.startActivity(context,
                    new String[]{Manifest.permission.RECORD_AUDIO});

            return false;
        }

        return true;
    }

    @Override
    public void start() {
        bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioRecord.startRecording();

        minAmplitude = 0;
        maxAmplitude = 0;

        disposable = Observable.interval(0,
                CHECK_AMPLITUDE_PERIOD,
                TimeUnit.MILLISECONDS,
                workerScheduler)
                .subscribe(any -> setBrightness(amplitudePercentage(getAmplitude())));

        setLightState(true);
    }

    @Override
    public void stop() {
        if (disposable != null) {
            disposable.dispose();
        }

        if (audioRecord != null) {
            audioRecord.stop();
        }

        setLightState(false);
    }

    private int getAmplitude() {
        short[] buffer = new short[bufferSize];
        audioRecord.read(buffer, 0, bufferSize);
        short max = 0;
        for (short s : buffer) {
            if (s > max) {
                max = s;
            }
        }
        return (int) max;
    }

    private int amplitudePercentage(int curAmplitude) {
        // Reduce amplitude min\max tunnel
        // because min\max value can be above or below initial avg value
        // and limit with 0 and max amplitude value
        maxAmplitude = Math.max(maxAmplitude - INCREASE_STEP, 0);
        minAmplitude = Math.min(minAmplitude + INCREASE_STEP, MAX_AMPLITUDE);

        // Save min max values
        maxAmplitude = Math.max(maxAmplitude, curAmplitude);
        minAmplitude = Math.min(minAmplitude, curAmplitude);

        // If min and max equal, exit to prevent dividing by zero
        if (minAmplitude == maxAmplitude) {
            return 0;
        }

        // Calculate percentage of current amplitude of difference max and min amplitude
        int avgAmplitude = (curAmplitude - minAmplitude) * 100 /
                (maxAmplitude - minAmplitude);

        Timber.d("Sound amplitude min: %d, max: %d, cur: %d; avg: %d", minAmplitude, maxAmplitude,
                curAmplitude, avgAmplitude);

        return avgAmplitude;
    }
}