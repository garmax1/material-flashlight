package co.garmax.materialflashlight;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.v4.app.Fragment;

import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import co.garmax.materialflashlight.di.DaggerAppComponent;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class CoreApplication extends Application
        implements HasActivityInjector, HasSupportFragmentInjector, HasServiceInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject
    DispatchingAndroidInjector<Service> serviceInjector;

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

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceInjector;
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
