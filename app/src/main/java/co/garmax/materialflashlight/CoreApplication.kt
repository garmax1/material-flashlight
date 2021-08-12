package co.garmax.materialflashlight

import android.app.Application
import co.garmax.materialflashlight.di.coreModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class CoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CoreApplication)
            modules(coreModule)
        }

        // Logs
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}