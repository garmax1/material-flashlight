package co.garmax.materialflashlight.features.modes

import android.content.Context
import co.garmax.materialflashlight.features.modes.ModeBase
import io.reactivex.Scheduler

class ModeFactory(private val workerScheduler: Scheduler, private val context: Context) {

    fun getMode(mode: ModeBase.Mode): ModeBase {
        if (mode === ModeBase.Mode.MODE_INTERVAL_STROBE) {
            return IntervalStrobeMode(workerScheduler)
        } else if (mode === ModeBase.Mode.MODE_SOS) {
            return SosMode(workerScheduler)
        } else if (mode === ModeBase.Mode.MODE_SOUND_STROBE) {
            return SoundStrobeMode(context, workerScheduler)
        } else if (mode === ModeBase.Mode.MODE_TORCH) {
            return TorchMode()
        }
        throw IllegalArgumentException("$mode mode not implemented")
    }
}