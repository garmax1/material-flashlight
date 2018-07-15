package co.garmax.materialflashlight.di;

import android.content.Context;

import javax.inject.Singleton;

import co.garmax.materialflashlight.features.foreground.ForegroundService;
import co.garmax.materialflashlight.features.LightManager;
import co.garmax.materialflashlight.features.SettingsRepository;
import co.garmax.materialflashlight.features.foreground.ForegroundServiceManager;
import co.garmax.materialflashlight.features.modes.ModeFactory;
import co.garmax.materialflashlight.features.modules.ModuleFactory;
import co.garmax.materialflashlight.features.widget.WidgetManager;
import co.garmax.materialflashlight.features.widget.WidgetProviderButton;
import co.garmax.materialflashlight.ui.PermissionsActivity;
import co.garmax.materialflashlight.ui.RootActivity;
import co.garmax.materialflashlight.ui.RootModule;
import co.garmax.materialflashlight.utils.PostExecutionThread;
import co.garmax.materialflashlight.utils.ResourceProvider;
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

    @Provides
    @Singleton
    static WidgetManager widgetManager(Context context) {
        return new WidgetManager(context);
    }

    @Provides
    @Singleton
    static ModuleFactory moduleFactory(Context context) {
        return new ModuleFactory(context);
    }

    @Provides
    @Singleton
    static ModeFactory modeFactory(Context context, Scheduler workerScheduler) {
        return new ModeFactory(workerScheduler, context);
    }

    @Singleton
    @Provides
    static LightManager lightManager(Context context,
                                     WidgetManager widgetManager,
                                     SettingsRepository settingsRepository,
                                     ModuleFactory moduleFactory,
                                     ModeFactory modeFactory) {
        LightManager lightManager = new LightManager(widgetManager,
                new ForegroundServiceManager(context),
                new ResourceProvider(context));

        lightManager.setModule(moduleFactory.getModule(settingsRepository.getModule()));
        lightManager.setMode(modeFactory.getMode(settingsRepository.getMode()));

        return lightManager;
    }

    @ActivityScope
    @ContributesAndroidInjector(modules = RootModule.class)
    abstract RootActivity rootActivity();

    @ContributesAndroidInjector
    abstract PermissionsActivity permissionsActivity();

    @ContributesAndroidInjector
    abstract ForegroundService foregroundService();

    @ContributesAndroidInjector
    abstract WidgetProviderButton widgetProviderButton();
}
