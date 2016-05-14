package co.garmax.materialflashlight

import android.app.Application
import co.garmax.materialflashlight.dagger.ApplicationComponent
import co.garmax.materialflashlight.dagger.ApplicationModule
import co.garmax.materialflashlight.dagger.ContextModule
import co.garmax.materialflashlight.dagger.DaggerApplicationComponent
import timber.log.Timber

class CustomApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule())
                .contextModule(ContextModule(applicationContext))
                .build();

        Timber.plant(Timber.DebugTree());
    }
}
