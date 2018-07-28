package co.garmax.materialflashlight.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.garmax.materialflashlight.R;
import co.garmax.materialflashlight.features.LightManager;
import co.garmax.materialflashlight.features.SettingsRepository;
import co.garmax.materialflashlight.features.modules.ScreenModule;

public class LightFragment extends BaseFragment {

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.layout_root)
    View layoutRoot;

    @Inject
    LightManager lightManager;

    @Inject
    SettingsRepository settingsRepository;

    /**
     * Receive and handle commands from screen module
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;

            // Get brightnessObservable value
            int brightness = intent.getIntExtra(ScreenModule.EXTRA_BRIGHTNESS_PERCENT, 100);

            setBrightness(brightness);
        }
    };

    @Override
    boolean isInImmersiveMode() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(requireContext()).
                registerReceiver(broadcastReceiver,
                        new IntentFilter(ScreenModule.ACTION_SCREEN_MODULE));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Turn off light because screen not visible and for this mode it makes no sense
        if(lightManager.isTurnedOn()) {
            lightManager.turnOff();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_light, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        fab.setKeepScreenOn(settingsRepository.isKeepScreenOn());
    }

    @OnClick(R.id.fab)
    void onClick() {
        lightManager.turnOff();
    }

    private void setBrightness(int percent) {

        int color = 255 * percent / 100;

        layoutRoot.setBackgroundColor(Color.rgb(color, color, color));
    }
}
