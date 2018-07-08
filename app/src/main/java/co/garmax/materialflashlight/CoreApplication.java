package co.garmax.materialflashlight;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import co.garmax.materialflashlight.di.DaggerAppComponent;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class CoreApplication extends Application
        implements HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        // Debug
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        setupUtils();
        setupDagger();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    private void setupUtils() {
        // Debug
        LeakCanary.install(this);

        // Logs
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void setupDagger() {
        DaggerAppComponent
                .builder()
                .context(this)
                .build()
                .inject(this);
    }
}
