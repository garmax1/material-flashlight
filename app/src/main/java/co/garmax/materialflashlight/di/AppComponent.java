package co.garmax.materialflashlight.di;

import android.content.Context;

import javax.inject.Singleton;

import co.garmax.materialflashlight.CoreApplication;
import co.garmax.materialflashlight.features.LightTileService;
import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    LightTileService.Component lightTileServiceComponent();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context context);
        AppComponent build();
    }

    void inject(CoreApplication app);
}
