package co.garmax.materialflashlight.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.inject.Inject;

import co.garmax.materialflashlight.R;
import co.garmax.materialflashlight.features.LightManager;
import co.garmax.materialflashlight.features.SettingsRepository;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.disposables.Disposable;

public class RootActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject
    LightManager lightManager;

    @Inject
    SettingsRepository settingsRepository;

    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        // Hide shadow in pre lollipop
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.image_status_mask).setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            replaceFragment(settingsRepository.isAutoTurnOn());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        disposable = lightManager.toggleStateStream()
                .subscribe(this::replaceFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();

        disposable.dispose();
    }

    private void replaceFragment(boolean isTunedOn) {
        // If module is screen and turned on
        Fragment fragment = (isTunedOn
                && settingsRepository.getModule() == LightManager.Module.MODULE_SCREEN)
                ? new LightFragment() : new MainFragment();

        Fragment fragmentCurrent = getSupportFragmentManager()
                .findFragmentById(R.id.layout_container);

        // Change fragment only if fragment is different
        if(fragmentCurrent == null
                || !fragment.getClass().equals(fragmentCurrent.getClass())) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_container,
                            fragment,
                            fragment.getClass().getName())
                    .commit();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Stop service if user close app
        lightManager.turnOff();
    }
}
