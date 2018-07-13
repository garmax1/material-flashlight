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
import co.garmax.materialflashlight.features.modules.ScreenModule;
import dagger.android.support.AndroidSupportInjection;

public class LightFragment extends BaseFragment {

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.layout_container)
    View layoutContainer;

    @Inject
    LightManager lightManager;


    /**
     * Receive and handle commands from screen module
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;

            // Get brightness value
            int brightness = intent.getIntExtra(ScreenModule.EXTRA_BRIGHTNESS_PERCENT, 100);

            setBrightness(brightness);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImmersiveMode(true);
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
    }

    @OnClick(R.id.fab)
    void onClick() {
        lightManager.turnOff();
    }

    private void setBrightness(int percent) {

        int color = Color.argb(255 * percent / 100, 0, 0, 0);

        // Change color when animation finished
        //TODO if(mInitialized)
        layoutContainer.setBackgroundColor(color);
    }
}
