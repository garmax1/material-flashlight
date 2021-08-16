package co.garmax.materialflashlight.features.modes

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Implement SOS
 */
class SosMode(private val workerScheduler: Scheduler) : ModeBase() {

    private var disposableInterval: Disposable? = null

    override fun start() {
        disposableInterval = Observable.interval(
            0,
            SOS_PERIOD.toLong(),
            TimeUnit.MILLISECONDS,
            workerScheduler
        )
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 short
            .delay(DELAY_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 short
            .delay(DELAY_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 short
            .delay(DELAY_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_LONG.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 long
            .delay(DELAY_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_LONG.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 long
            .delay(DELAY_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_LONG.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 long
            .delay(DELAY_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 short
            .delay(DELAY_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 short
            .delay(DELAY_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MAX_LIGHT_VOLUME) }
            .delay(STROBE_SHORT.toLong(), TimeUnit.MILLISECONDS)
            .doOnNext { any: Long? -> setBrightness(MIN_LIGHT_VOLUME) } // 1 short
            .delay(DELAY_LONG.toLong(), TimeUnit.MILLISECONDS)
            .subscribe { any: Long? -> }
    }

    override fun stop() {
        setBrightness(MIN_LIGHT_VOLUME)

        disposableInterval?.dispose()
    }

    override fun checkPermissions(): Boolean {
        return true
    }

    companion object {
        private const val STROBE_SHORT = 400
        private const val STROBE_LONG = 900
        private const val DELAY_SHORT = 400
        private const val DELAY_LONG = 2500
        private const val SOS_PERIOD =
            (STROBE_SHORT * 3 + DELAY_SHORT * 3) * 2 + 2 * DELAY_SHORT + 3 * STROBE_LONG + DELAY_LONG
    }
}