package co.garmax.materialflashlight.ui.light

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import co.garmax.materialflashlight.databinding.FragmentLightBinding
import co.garmax.materialflashlight.features.LightManager
import co.garmax.materialflashlight.features.modules.ScreenModule
import co.garmax.materialflashlight.repositories.SettingsRepository
import co.garmax.materialflashlight.ui.BaseFragment
import org.koin.android.ext.android.inject

/**
 * Light simulation screen
 */
class LightFragment : BaseFragment() {

    override val isInImmersiveMode = true

    private val lightManager: LightManager by inject()

    private val settingsRepository: SettingsRepository by inject()

    private val binding get() = _binding!!
    private var _binding: FragmentLightBinding? = null

    /**
     * Receive and handle commands from screen module
     */
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get brightnessObservable value
            val brightness: Int = intent.getIntExtra(ScreenModule.EXTRA_BRIGHTNESS_PERCENT, 100)
            setBrightness(brightness)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.keepScreenOn = settingsRepository.isKeepScreenOn
        binding.fab.setOnClickListener { lightManager.turnOff() }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(
            requireContext()
        ).registerReceiver(
            broadcastReceiver, IntentFilter(ScreenModule.ACTION_SCREEN_MODULE)
        )
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    override fun onStop() {
        super.onStop()

        // Turn off light because screen not visible and for this mode it makes no sense
        if (lightManager.isTurnedOn) lightManager.turnOff()
    }

    private fun setBrightness(percent: Int) {
        val color = 255 * percent / 100
        binding.layoutRoot.setBackgroundColor(Color.rgb(color, color, color))
    }
}