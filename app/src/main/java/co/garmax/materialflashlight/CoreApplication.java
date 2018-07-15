package co.garmax.materialflashlight;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.support.v4.app.Fragment;

import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import co.garmax.materialflashlight.di.AppComponent;
import co.garmax.materialflashlight.di.DaggerAppComponent;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;

public class CoreApplication extends Application
        implements HasActivityInjector, HasSupportFragmentInjector, HasServiceInjector, HasBroadcastReceiverInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject
    DispatchingAndroidInjector<Service> serviceInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverInjector;

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

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return broadcastReceiverInjector;
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
        AppComponent component = DaggerAppComponent.builder().context(this).build();
        if (SDK_INT >= N) {
            component.lightTileServiceComponent().inject(this);
        } else {
            component.inject(this);
        }
    }
}
