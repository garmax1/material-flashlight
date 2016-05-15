package co.garmax.materialflashlight.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.*
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import co.garmax.materialflashlight.BuildConfig
import co.garmax.materialflashlight.CustomApplication
import co.garmax.materialflashlight.Preferences
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.modes.ModeBase
import co.garmax.materialflashlight.modes.ModeService
import co.garmax.materialflashlight.modes.SoundStrobeMode
import co.garmax.materialflashlight.modules.FlashModule
import co.garmax.materialflashlight.modules.ModuleBase
import co.garmax.materialflashlight.modules.ModuleManager
import co.garmax.materialflashlight.modules.ScreenModule
import javax.inject.Inject

class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,
        ModuleManager.OnLightStateChangedListener {

    @Bind(R.id.image_appbar)
    lateinit var mImageAppbar: ImageView
    @Bind(R.id.switch_keep_screen_on)
    lateinit var mSwitchKeepScreenOn: SwitchCompat
    @Bind(R.id.switch_auto_turn_on)
    lateinit var mSwitchAutoTurnOn: SwitchCompat
    @Bind(R.id.radio_torch)
    lateinit var mRadioTorch: RadioButton
    @Bind(R.id.radio_interval_strobe)
    lateinit var mRadioIntervalStrobe: RadioButton
    @Bind(R.id.radio_sound_strobe)
    lateinit var mRadioSoundStrobe: RadioButton
    @Bind(R.id.radio_camera_flashlight)
    lateinit var mRadioCameraFlashlight: RadioButton
    @Bind(R.id.radio_screen)
    lateinit var mRadioScreen: RadioButton
    @Bind(R.id.fab)
    lateinit var mFab: FloatingActionButton
    @Bind(R.id.text_version)
    lateinit var mTextVersion: TextView

    @Inject
    lateinit var mModuleManager: ModuleManager
    @Inject
    lateinit var mPreferences: Preferences

    private lateinit var mAnimatedDrawableDay: AnimatedVectorDrawableCompat
    private lateinit var mAnimatedDrawableNight: AnimatedVectorDrawableCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        (application as CustomApplication).applicationComponent.inject(this)

        mAnimatedDrawableDay = AnimatedVectorDrawableCompat.create(this, R.drawable.avc_appbar_day)
                as AnimatedVectorDrawableCompat
        mAnimatedDrawableNight = AnimatedVectorDrawableCompat.create(this, R.drawable.avc_appbar_night)
                as AnimatedVectorDrawableCompat

        setupLayout(savedInstanceState)

        mModuleManager.setOnLightStateChangedListener(this)

        // Handle auto turn on
        if (savedInstanceState == null && mPreferences.isAutoTurnOn) {
            turnOn()
        }
        // Restore state on recreation
        else {
            setState(mModuleManager.isRunning(), false)
        }
    }

    private fun setupLayout(savedInstanceState: Bundle?) {

        mRadioSoundStrobe.setOnCheckedChangeListener(this)
        mRadioIntervalStrobe.setOnCheckedChangeListener(this)
        mRadioTorch.setOnCheckedChangeListener(this)
        mSwitchKeepScreenOn.setOnCheckedChangeListener(this)
        mSwitchAutoTurnOn.setOnCheckedChangeListener(this)
        mRadioCameraFlashlight.setOnCheckedChangeListener(this)
        mRadioScreen.setOnCheckedChangeListener(this)

        mTextVersion.text = getString(R.string.text_version, BuildConfig.VERSION_NAME)

        // Restore settings
        if (savedInstanceState == null) {
            mSwitchKeepScreenOn.isChecked = mPreferences.isKeepScreenOn
            mSwitchAutoTurnOn.isChecked = mPreferences.isAutoTurnOn

            // Set module
            when (mPreferences.module) {
                ModuleBase.MODULE_CAMERA_FLASHLIGHT -> {
                    mRadioCameraFlashlight.isChecked = true
                }
                ModuleBase.MODULE_SCREEN -> {
                    mRadioScreen.isChecked = true
                }
            }

            // Set mode
            when (mPreferences.mode) {
                ModeBase.MODE_INTERVAL_STROBE -> {
                    mRadioIntervalStrobe.isChecked = true
                }
                ModeBase.MODE_TORCH -> {
                    mRadioTorch.isChecked = true
                }
                ModeBase.MODE_SOUND_STROBE -> {
                    mRadioSoundStrobe.isChecked = true
                }

            }
        }
    }

    override fun stateChanged(turnedOn: Boolean) {
        setState(turnedOn, true)
    }

    private fun setState(turnedOn: Boolean, animated: Boolean) {
        runOnUiThread(
                {
                    if (turnedOn) {
                        // Fab image
                        mFab.setImageResource(R.drawable.ic_power_on);

                        // Appbar image
                        if (animated) {
                            mImageAppbar.setImageDrawable(mAnimatedDrawableDay)
                            mAnimatedDrawableDay.start()
                        } else {
                            mImageAppbar.setImageResource(R.drawable.vc_appbar_day)
                        }
                    } else {
                        // Fab image
                        mFab.setImageResource(R.drawable.ic_power_off);

                        // Appbar image
                        if (animated) {
                            mImageAppbar.setImageDrawable(mAnimatedDrawableNight)

                            mAnimatedDrawableNight.start()
                        } else {
                            mImageAppbar.setImageResource(R.drawable.vc_appbar_night)
                        }
                    }
                }
        )
    }

    @OnClick(R.id.fab, R.id.layout_keep_screen_on, R.id.layout_auto_turn_on)
    internal fun onClick(view: View) {
        when (view.id) {
            R.id.fab -> {

                // Turn off light
                if (mModuleManager.isRunning()) {
                    ModeService.setMode(this, ModeBase.MODE_OFF)
                }
                // Turn on light
                else {
                    turnOn()
                }
            }

            R.id.layout_keep_screen_on ->
                mSwitchKeepScreenOn.toggle()

            R.id.layout_auto_turn_on ->
                mSwitchAutoTurnOn.toggle()
        }
    }

    private fun changeModule(module: Int) {
        mPreferences.module = module

        if (module == ModuleBase.MODULE_CAMERA_FLASHLIGHT) {
            mModuleManager.module = FlashModule(this);
        } else if (module == ModuleBase.MODULE_SCREEN) {
            mModuleManager.module = ScreenModule(this);
        } else {
            throw IllegalArgumentException("Unknown module type " + module)
        }
    }

    private fun turnOn() {
        // Show toast using app context
        if (!mModuleManager.isSupported()) {
            Toast.makeText(applicationContext, R.string.toast_module_not_supported, Toast.LENGTH_LONG).show()

            // Stop if was started
            if (mModuleManager.isAvailable()) {
                mModuleManager.stop();
            }

        } else {

            // Exit if we don't have permission for the module
            if (!mModuleManager.checkPermissions(RC_MODULE_PERMISSIONS, this)) return

            // Exit if we don't have permission for sound strobe mode
            if(mPreferences.mode == ModeBase.MODE_SOUND_STROBE &&
                    !SoundStrobeMode.checkPermissions(RC_MODE_PERMISSIONS, this)) return

            ModeService.setMode(this, mPreferences.mode)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.switch_keep_screen_on -> {
                mPreferences.isKeepScreenOn = isChecked
            }
            R.id.switch_auto_turn_on -> {
                mPreferences.isAutoTurnOn = isChecked
            }
            R.id.radio_sound_strobe -> {
                if (isChecked) changeMode(ModeBase.MODE_SOUND_STROBE)
            }
            R.id.radio_interval_strobe -> {
                if (isChecked) changeMode(ModeBase.MODE_INTERVAL_STROBE)
            }
            R.id.radio_torch -> {
                if (isChecked) changeMode(ModeBase.MODE_TORCH)
            }
            R.id.radio_camera_flashlight ->
                if (isChecked) changeModule(ModuleBase.MODULE_CAMERA_FLASHLIGHT)

            R.id.radio_screen ->
                if (isChecked) changeModule(ModuleBase.MODULE_SCREEN)
        }
    }

    private fun changeMode(mode: Int) {
        mPreferences.mode = mode

        // Start new mode
        if (mModuleManager.isRunning()) turnOn()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Stop service if user exit with back button
        if (mModuleManager.isRunning()) {
            ModeService.setMode(this, ModeBase.MODE_OFF)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_MODULE_PERMISSIONS || requestCode == RC_MODE_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                turnOn();
            } else if(mModuleManager.isRunning()) {
                ModeService.setMode(this, ModeBase.MODE_OFF)
            }
        }
    }

    companion object {
        private const val RC_MODULE_PERMISSIONS = 0;
        private const val RC_MODE_PERMISSIONS = 1;
    }
}
