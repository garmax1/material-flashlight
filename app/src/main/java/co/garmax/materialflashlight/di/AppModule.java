package co.garmax.materialflashlight.di;

import android.content.Context;

import javax.inject.Singleton;

import co.garmax.materialflashlight.features.ForegroundService;
import co.garmax.materialflashlight.features.LightManager;
import co.garmax.materialflashlight.features.SettingsRepository;
import co.garmax.materialflashlight.ui.PermissionsActivity;
import co.garmax.materialflashlight.ui.RootActivity;
import co.garmax.materialflashlight.ui.RootModule;
import co.garmax.materialflashlight.utils.PostExecutionThread;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module(includes = AndroidSupportInjectionModule.class)
public abstract class AppModule {

    @Provides
    @Singleton
    static PostExecutionThread postExecutionThread() {
        return AndroidSchedulers::mainThread;
    }

    @Provides
    @Singleton
    static Scheduler workerScheduler() {
        return Schedulers.computation();
    }

    @Singleton
    @Provides
    static LightManager lightManager(Context context,
                                     SettingsRepository settingsRepository,
                                     Scheduler workerScheduler) {
        return new LightManager(context, workerScheduler, settingsRepository);
    }

    @ActivityScope
    @ContributesAndroidInjector(modules = RootModule.class)
    abstract RootActivity rootActivity();

    @ActivityScope
    @ContributesAndroidInjector
    abstract PermissionsActivity permissionsActivity();

    @ContributesAndroidInjector
    @ServiceScope
    abstract ForegroundService foregroundService();
}
