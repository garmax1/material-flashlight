package co.garmax.materialflashlight.features.modes

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Interrupted light with equal interval
 */
class IntervalStrobeMode(private val workerScheduler: Scheduler) : ModeBase() {
    private var disposable: Disposable? = null

    override fun start() {
        disposable = Observable.interval(
            0,
            (STROBE_PERIOD + DELAY_PERIOD).toLong(),
            TimeUnit.MILLISECONDS,
            workerScheduler
        )
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_PERIOD.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) }
            .delay(DELAY_PERIOD.toLong(), TimeUnit.MILLISECONDS)
            .subscribe { any: Long? -> }
    }

    override fun stop() {
        setBrightness(MIN_LIGHT_VOLUME)
        disposable?.dispose()
        disposable = null
    }

    override fun checkPermissions(): Boolean {
        return true
    }

    fun updateStrobe(timeOn: Int, timeOff: Int) {
        STROBE_PERIOD = if (timeOn <= 0) DEFAULT_STROBE_PERIOD else timeOn
        DELAY_PERIOD = if (timeOff <= 0) DEFAULT_DELAY_PERIOD else timeOff

        if (disposable != null) {
            stop()
            start()
        }
    }

    companion object {
        const val DEFAULT_STROBE_PERIOD = 300
        const val DEFAULT_DELAY_PERIOD = 200

        var STROBE_PERIOD = DEFAULT_STROBE_PERIOD
        var DELAY_PERIOD = DEFAULT_DELAY_PERIOD
    }
}