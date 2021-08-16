package co.garmax.materialflashlight.ui.main

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import co.garmax.materialflashlight.BuildConfig
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.databinding.FragmentMainBinding
import co.garmax.materialflashlight.extensions.observeNotNull
import co.garmax.materialflashlight.features.modes.ModeBase.Mode
import co.garmax.materialflashlight.features.modules.ModuleBase.Module
import co.garmax.materialflashlight.service.ForegroundService
import co.garmax.materialflashlight.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainFragment : BaseFragment() {

    private val viewModel by viewModel<MainViewModel>()

    private val binding get() = _binding!!
    private var _binding: FragmentMainBinding? = null

    private var animatedDrawableDay: AnimatedVectorDrawableCompat? = null
    private var animatedDrawableNight: AnimatedVectorDrawableCompat? = null
    private var backgroundColorAnimation: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout(savedInstanceState)
        setupViewModel()
    }

    private fun setupLayout(savedInstanceState: Bundle?) {

        animatedDrawableDay =
            AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avc_appbar_day)
        animatedDrawableNight =
            AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avc_appbar_night)

        with(binding) {

            fab.setOnClickListener {
                if (viewModel.isLightTurnedOn) {
                    ForegroundService.stopService(requireContext())
                } else {
                    ForegroundService.startService(requireContext())
                }
            }

            layoutKeepScreenOn.setOnClickListener { binding.switchKeepScreenOn.toggle() }

            layoutAutoTurnOn.setOnClickListener { binding.switchAutoTurnOn.toggle() }

            layoutContent.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (viewModel.isLightTurnedOn) R.color.green else R.color.colorPrimaryLight
                )
            )

            if (savedInstanceState == null) {
                // Set module
                when (viewModel.lightModule) {
                    Module.MODULE_CAMERA_FLASHLIGHT -> radioCameraFlashlight.isChecked = true
                    Module.MODULE_SCREEN -> radioScreen.isChecked = true
                }
                when (viewModel.lightMode) {
                    Mode.MODE_INTERVAL_STROBE -> radioIntervalStrobe.isChecked = true
                    Mode.MODE_TORCH -> radioTorch.isChecked = true
                    Mode.MODE_SOUND_STROBE -> radioSoundStrobe.isChecked = true
                    Mode.MODE_SOS -> radioSos.isChecked = true
                    else -> {
                    }
                }
            } else {
                setState(viewModel.isLightTurnedOn, false)
            }

            switchKeepScreenOn.isChecked = viewModel.isKeepScreenOn
            fab.keepScreenOn = viewModel.isKeepScreenOn

            switchAutoTurnOn.isChecked = viewModel.isAutoTurnedOn

            radioSoundStrobe.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.setMode(Mode.MODE_SOUND_STROBE)
            }
            radioIntervalStrobe.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.setMode(Mode.MODE_INTERVAL_STROBE)
            }
            radioTorch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.setMode(Mode.MODE_TORCH)
            }
            radioSos.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.setMode(Mode.MODE_SOS)
            }
            radioCameraFlashlight.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.setModule(Module.MODULE_CAMERA_FLASHLIGHT)
            }
            radioScreen.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.setModule(Module.MODULE_SCREEN)
            }

            switchKeepScreenOn.setOnCheckedChangeListener { _, isChecked ->
                viewModel.isKeepScreenOn = isChecked
                fab.keepScreenOn = isChecked
            }

            switchAutoTurnOn.setOnCheckedChangeListener { _, isChecked ->
                viewModel.isAutoTurnedOn = isChecked
            }

            textVersion.text = getString(R.string.text_version, BuildConfig.VERSION_NAME)
        }
    }

    private fun setupViewModel() {
        // Handle toggle of the light
        observeNotNull(viewModel.liveDataLightToggle) { setState(it, true) }
    }

    private fun setState(isLightOn: Boolean, animated: Boolean) {
        Timber.d("Light toggle %s, animated %s", isLightOn, animated)

        with(binding) {
            if (isLightOn) {
                // Fab image
                fab.setImageResource(R.drawable.ic_power_on)

                // Appbar image
                if (animated) {
                    imageAppbar.setImageDrawable(animatedDrawableDay)
                    animatedDrawableDay?.start()
                    animateBackground(R.color.colorPrimaryLight, R.color.green)
                } else {
                    imageAppbar.setImageResource(R.drawable.vc_appbar_day)
                    layoutContent.setBackgroundResource(R.color.green)
                }
            } else {
                // Fab image
                fab.setImageResource(R.drawable.ic_power_off)

                // Appbar image
                if (animated) {
                    imageAppbar.setImageDrawable(animatedDrawableNight)
                    animatedDrawableNight?.start()
                    animateBackground(R.color.green, R.color.colorPrimaryLight)
                } else {
                    imageAppbar.setImageResource(R.drawable.vc_appbar_night)
                    layoutContent.setBackgroundResource(R.color.colorPrimaryLight)
                }
            }
        }
    }

    private fun animateBackground(@ColorRes fromColorResId: Int, @ColorRes toColorResId: Int) {
        val colorFrom: Int = ContextCompat.getColor(requireContext(), fromColorResId)
        val colorTo: Int = ContextCompat.getColor(requireContext(), toColorResId)

        if (backgroundColorAnimation?.isRunning == true) {
            backgroundColorAnimation?.cancel()
        }

        backgroundColorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
                duration = resources.getInteger(R.integer.animation_time).toLong()
                addUpdateListener { animator: ValueAnimator ->
                    binding.layoutContent.setBackgroundColor(
                        animator.animatedValue as Int
                    )
                }
                start()
            }
    }
}