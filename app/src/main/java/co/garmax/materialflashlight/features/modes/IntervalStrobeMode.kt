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
        if (disposable != null) {
            disposable!!.dispose()
        }
    }

    override fun checkPermissions(): Boolean {
        return true
    }

    companion object {
        private const val STROBE_PERIOD = 300
        private const val DELAY_PERIOD = 200
    }
}