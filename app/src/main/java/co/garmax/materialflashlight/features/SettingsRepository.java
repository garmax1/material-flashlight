package co.garmax.materialflashlight.features;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

public class SettingsRepository {

    private static final String KEEP_SCREEN_ON = "keep_screen_on";
    private static final String MODE = "mode";
    private static final String MODULE = "module";
    private static final String AUTO_TURN_ON = "auto_turn_on";

    private SharedPreferences sharedPreferences;

    @Inject
    public SettingsRepository(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isKeepScreenOn() {
        return sharedPreferences.getBoolean(KEEP_SCREEN_ON, false);
    }

    public void setKeepScreenOn(boolean isKeepScreenOn) {
        sharedPreferences.edit().putBoolean(KEEP_SCREEN_ON, isKeepScreenOn).apply();
    }

    public boolean isAutoTurnOn() {
        return sharedPreferences.getBoolean(AUTO_TURN_ON, false);
    }

    public void setAutoTurnOn(boolean isAutoTurnOn) {
        sharedPreferences.edit().putBoolean(AUTO_TURN_ON, isAutoTurnOn).apply();
    }

    public LightManager.Mode getMode() {
        String mode = sharedPreferences.getString(MODE, LightManager.Mode.MODE_TORCH.toString());

        return LightManager.Mode.valueOf(mode);
    }

    public void setMode(LightManager.Mode mode) {
        sharedPreferences.edit().putString(MODE, mode.name()).apply();
    }

    public LightManager.Module getModule() {
        String module = sharedPreferences.getString(MODULE,
                LightManager.Module.MODULE_CAMERA_FLASHLIGHT.toString());

        return LightManager.Module.valueOf(module);
    }

    public void setModule(LightManager.Module module) {
        sharedPreferences.edit().putString(MODULE, module.name()).apply();
    }
}
