package co.garmax.materialflashlight.ui.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.extensions.observeNotNull
import co.garmax.materialflashlight.features.modules.ModuleBase
import co.garmax.materialflashlight.service.ForegroundService
import co.garmax.materialflashlight.ui.light.LightFragment
import co.garmax.materialflashlight.ui.main.MainFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity() {

    private val viewModel by viewModel<RootViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_root)

        if (savedInstanceState == null) {
            replaceFragment(viewModel.isAutoTurnOn)

            if (viewModel.isAutoTurnOn) {
                ForegroundService.startService(this)
            }
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        observeNotNull(viewModel.liveDataLightToggle) { replaceFragment(it) }
    }

    private fun replaceFragment(isTunedOn: Boolean) {
        val fragment = if (isTunedOn && viewModel.lightModule == ModuleBase.Module.MODULE_SCREEN) {
            LightFragment()
        } else {
            MainFragment()
        }

        val fragmentCurrent = supportFragmentManager.findFragmentById(R.id.layout_container)

        if (fragmentCurrent == null || fragment.javaClass != fragmentCurrent.javaClass) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_container, fragment, fragment.javaClass.name)
                .commit()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        ForegroundService.stopService(this)
    }
}
