package co.garmax.materialflashlight.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import co.garmax.materialflashlight.R;
import co.garmax.materialflashlight.features.LightManager;
import co.garmax.materialflashlight.features.foreground.ForegroundServiceManager;
import co.garmax.materialflashlight.features.modes.ModeBase;
import co.garmax.materialflashlight.features.modules.ModuleBase;
import co.garmax.materialflashlight.widget.WidgetManager;
import co.garmax.materialflashlight.utils.ResourceProvider;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LightManagerTests extends BaseTest {

    @Mock
    ResourceProvider resourceProvider;
    @Mock
    WidgetManager widgetManager;
    @Mock
    ForegroundServiceManager foregroundServiceManager;
    @Mock
    ModeBase mode;
    @Mock
    ModuleBase module;

    private LightManager lightManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        lightManager = new LightManager(widgetManager,
                foregroundServiceManager,
                resourceProvider);

        lightManager.setModule(module);
        lightManager.setMode(mode);

        when(mode.checkPermissions()).thenReturn(true);
        when(mode.brightnessObservable()).thenReturn(Observable.just(0));
        when(module.checkPermissions()).thenReturn(true);
        when(module.isAvailable()).thenReturn(true);
        when(module.isSupported()).thenReturn(true);
    }

    @After
    public void tearDownClass() {
        RxAndroidPlugins.reset();
    }

    @Test
    public void moduleNotSupported() {
        // when
        when(module.isSupported()).thenReturn(false);
        lightManager.turnOn();

        // then
        verify(resourceProvider, times(1)).showToast(R.string.toast_module_not_supported);
    }

    @Test
    public void moduleNotAvailable() {
        // when
        when(module.isAvailable()).thenReturn(false);
        lightManager.turnOn();

        // then
        verify(resourceProvider, times(1)).showToast(R.string.toast_module_not_available);
    }

    @Test
    public void toggle() {
        // when
        lightManager.turnOn();

        // then
        verify(widgetManager, times(1)).updateWidgets();
        verify(foregroundServiceManager, times(1)).startService();
        verify(module, times(1)).init();
        verify(mode, times(1)).start();
        assertEquals(lightManager.isTurnedOn(), true);

        // when
        lightManager.turnOff();

        // then
        verify(widgetManager, times(2)).updateWidgets();
        verify(foregroundServiceManager, times(1)).stopService();
        verify(module, times(1)).release();
        verify(mode, times(1)).stop();
        assertEquals(lightManager.isTurnedOn(), false);
    }
}