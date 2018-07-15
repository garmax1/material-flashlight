package co.garmax.materialflashlight.features;

import android.app.Service;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

import javax.inject.Inject;

import co.garmax.materialflashlight.CoreApplication;
import co.garmax.materialflashlight.R;
import dagger.Subcomponent;
import dagger.android.AndroidInjection;
import dagger.android.ContributesAndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import io.reactivex.disposables.Disposable;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LightTileService extends TileService {

    @Inject
    LightManager lightManager;

    private Disposable disposableToggleState;

    public LightTileService() {
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public void onClick() {
        super.onClick();

        // switch from active to passive based on tiles current state
        switch (getQsTile().getState()) {
            case Tile.STATE_ACTIVE:
                setCurrentState(Tile.STATE_INACTIVE);
                lightManager.turnOff();
                break;
            case Tile.STATE_INACTIVE:
                setCurrentState(Tile.STATE_ACTIVE);
                lightManager.turnOn();
                break;
            case Tile.STATE_UNAVAILABLE:
                break;
        }
    }

    private void setCurrentState(int state) {
        Tile tile = getQsTile();
        tile.setState(state);
        switch (state) {
            case Tile.STATE_ACTIVE:
                tile.setIcon(Icon.createWithResource(getApplicationContext(),
                        R.drawable.ic_quick_settings_active));
                break;
            case Tile.STATE_INACTIVE:
                tile.setIcon(Icon.createWithResource(getApplicationContext(),
                        R.drawable.ic_quick_settings_inactive));
                break;
            case Tile.STATE_UNAVAILABLE:
                tile.setIcon(Icon.createWithResource(getApplicationContext(),
                        R.drawable.ic_quick_settings_unavailable));
                break;
        }

        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();

        disposableToggleState = lightManager.toggleStateStream()
                .subscribe(isTurnedOn ->
                        setCurrentState(isTurnedOn ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE));

        if (!lightManager.isSupported()) {
            setCurrentState(Tile.STATE_UNAVAILABLE);
        }
    }

    @Override
    public void onStopListening() {
        super.onStopListening();

        if (disposableToggleState != null) {
            disposableToggleState.dispose();
        }
    }

    @Subcomponent(modules = Module.class)
    public interface Component {
        void inject(CoreApplication app);

        DispatchingAndroidInjector<Service> injector();
    }

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract LightTileService lightTileService();
    }
}