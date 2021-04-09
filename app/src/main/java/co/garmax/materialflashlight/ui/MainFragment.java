package co.garmax.materialflashlight.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.garmax.materialflashlight.BuildConfig;
import co.garmax.materialflashlight.R;
import co.garmax.materialflashlight.features.LightManager;
import co.garmax.materialflashlight.features.SettingsRepository;
import co.garmax.materialflashlight.features.modes.ModeBase;
import co.garmax.materialflashlight.features.modes.ModeFactory;
import co.garmax.materialflashlight.features.modules.ModuleBase;
import co.garmax.materialflashlight.features.modules.ModuleFactory;
import co.garmax.materialflashlight.utils.PostExecutionThread;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static co.garmax.materialflashlight.features.modes.ModeBase.Mode.MODE_INTERVAL_STROBE;
import static co.garmax.materialflashlight.features.modes.ModeBase.Mode.MODE_SOS;
import static co.garmax.materialflashlight.features.modes.ModeBase.Mode.MODE_SOUND_STROBE;
import static co.garmax.materialflashlight.features.modes.ModeBase.Mode.MODE_TORCH;

public class MainFragment extends BaseFragment {

    @BindView(R.id.image_appbar)
    ImageView imageAppbar;
    @BindView(R.id.switch_keep_screen_on)
    SwitchCompat switchKeepScreenOn;
    @BindView(R.id.switch_auto_turn_on)
    SwitchCompat switchAutoTurnOn;
    @BindView(R.id.radio_sos)
    RadioButton radioSos;
    @BindView(R.id.radio_torch)
    RadioButton radioTorch;
    @BindView(R.id.radio_interval_strobe)
    RadioButton radioIntervalStrobe;
    @BindView(R.id.radio_sound_strobe)
    RadioButton radioSoundStrobe;
    @BindView(R.id.radio_camera_flashlight)
    RadioButton radioCameraFlashlight;
    @BindView(R.id.radio_screen)
    RadioButton radioScreen;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.text_version)
    TextView textVersion;
    @BindView(R.id.layout_content)
    View layoutContent;

    @Inject
    SettingsRepository settingsRepository;
    @Inject
    LightManager lightManager;
    @Inject
    ModeFactory modeFactory;
    @Inject
    ModuleFactory moduleFactory;
    @Inject
    PostExecutionThread postExecutionThread;

    private AnimatedVectorDrawableCompat animatedDrawableDay;
    private AnimatedVectorDrawableCompat animatedDrawableNight;
    private Disposable disposableLightToggle;
    private ValueAnimator backgroundColorAnimation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        animatedDrawableDay = AnimatedVectorDrawableCompat
                .create(requireContext(), R.drawable.avc_appbar_day);
        animatedDrawableNight = AnimatedVectorDrawableCompat
                .create(requireContext(), R.drawable.avc_appbar_night);

        setupLayout(savedInstanceState);
    }

    private void setupLayout(@Nullable Bundle savedInstanceState) {

        // Handle toggle of the light
        disposableLightToggle = lightManager.toggleStateStream()
                .observeOn(postExecutionThread.getScheduler())
                .subscribe(isLightOn -> setState(isLightOn, true));

        layoutContent.setBackgroundColor(ContextCompat.getColor(requireContext(),
                lightManager.isTurnedOn() ? R.color.green : R.color.colorPrimaryLight));

        if (savedInstanceState == null) {
            // Set module
            switch (settingsRepository.getModule()) {
                case MODULE_CAMERA_FLASHLIGHT:
                    radioCameraFlashlight.setChecked(true);
                    break;
                case MODULE_SCREEN:
                    radioScreen.setChecked(true);
                    break;
            }

            // Set mode
            switch (settingsRepository.getMode()) {
                case MODE_INTERVAL_STROBE:
                    radioIntervalStrobe.setChecked(true);
                    break;
                case MODE_TORCH:
                    radioTorch.setChecked(true);
                    break;
                case MODE_SOUND_STROBE:
                    radioSoundStrobe.setChecked(true);
                    break;
                case MODE_SOS:
                    radioSos.setChecked(true);
                    break;
            }
        }
        // Restore state on recreation
        else {
            setState(lightManager.isTurnedOn(), false);
        }

        boolean isKeepScreenOn = settingsRepository.isKeepScreenOn();

        switchKeepScreenOn.setChecked(isKeepScreenOn);
        fab.setKeepScreenOn(isKeepScreenOn);
        switchAutoTurnOn.setChecked(settingsRepository.isAutoTurnOn());

        radioSoundStrobe.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    if (isChecked) changeMode(MODE_SOUND_STROBE);
                }
        );
        radioIntervalStrobe.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    if (isChecked) changeMode(MODE_INTERVAL_STROBE);
                }
        );
        radioTorch.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    if (isChecked) changeMode(MODE_TORCH);
                }
        );
        radioSos.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    if (isChecked) changeMode(MODE_SOS);
                }
        );
        radioCameraFlashlight.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    if (isChecked) changeModule(ModuleBase.Module.MODULE_CAMERA_FLASHLIGHT);
                }
        );
        radioScreen.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    if (isChecked) changeModule(ModuleBase.Module.MODULE_SCREEN);
                }
        );

        switchKeepScreenOn.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> {
                    settingsRepository.setKeepScreenOn(isChecked);
                    fab.setKeepScreenOn(isChecked);
                });
        switchAutoTurnOn.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> settingsRepository.setAutoTurnOn(isChecked));
        textVersion.setText(getString(R.string.text_version, BuildConfig.VERSION_NAME));

    }

    private void setState(boolean isLightOn, boolean animated) {
        Timber.d("Light toggle %s, animated %s", isLightOn, animated);
        if (isLightOn) {
            // Fab image
            fab.setImageResource(R.drawable.ic_power_on);

            // Appbar image
            if (animated) {
                imageAppbar.setImageDrawable(animatedDrawableDay);
                animatedDrawableDay.start();
                animateBackground(R.color.colorPrimaryLight, R.color.green);
            } else {
                imageAppbar.setImageResource(R.drawable.vc_appbar_day);
                layoutContent.setBackgroundResource(R.color.green);
            }
        } else {
            // Fab image
            fab.setImageResource(R.drawable.ic_power_off);

            // Appbar image
            if (animated) {
                imageAppbar.setImageDrawable(animatedDrawableNight);
                animatedDrawableNight.start();
                animateBackground(R.color.green, R.color.colorPrimaryLight);
            } else {
                imageAppbar.setImageResource(R.drawable.vc_appbar_night);
                layoutContent.setBackgroundResource(R.color.colorPrimaryLight);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        disposableLightToggle.dispose();
    }

    @OnClick({R.id.fab, R.id.layout_keep_screen_on, R.id.layout_auto_turn_on})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (lightManager.isTurnedOn()) {
                    lightManager.turnOff();
                } else {
                    lightManager.turnOn();
                }
                break;
            case R.id.layout_keep_screen_on:
                switchKeepScreenOn.toggle();
                break;
            case R.id.layout_auto_turn_on:
                switchAutoTurnOn.toggle();
                break;
        }
    }

    private void animateBackground(@ColorRes int fromColorResId, @ColorRes int toColorResId) {
        int colorFrom = ContextCompat.getColor(requireContext(), fromColorResId);
        int colorTo = ContextCompat.getColor(requireContext(), toColorResId);

        if(backgroundColorAnimation != null && backgroundColorAnimation.isRunning()) {
            backgroundColorAnimation.cancel();
        }

        backgroundColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        backgroundColorAnimation.setDuration(getResources().getInteger(R.integer.animation_time));
        backgroundColorAnimation.addUpdateListener(animator ->
                layoutContent.setBackgroundColor((int) animator.getAnimatedValue()));

        backgroundColorAnimation.start();
    }

    private void changeMode(ModeBase.Mode mode) {
        settingsRepository.setMode(mode);

        lightManager.setMode(modeFactory.getMode(mode));
    }

    private void changeModule(ModuleBase.Module module) {
        settingsRepository.setModule(module);

        lightManager.setModule(moduleFactory.getModule(module));
    }
}
