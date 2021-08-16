package co.garmax.materialflashlight.ui.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.extensions.observeNotNull
import co.garmax.materialflashlight.features.modules.ModuleBase
import co.garmax.materialflashlight.ui.light.LightFragment
import co.garmax.materialflashlight.ui.main.MainFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity() {

    private val viewModel by viewModel<RootViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        if (savedInstanceState == null) {
            replaceFragment(viewModel.isAutoTurnOn)

            // Handle auto turn on
            if (viewModel.isAutoTurnOn) viewModel.toggleLight(true)
        }

        setupViewModel()
    }

    private fun setupViewModel() {
        observeNotNull(viewModel.liveDataLightToggle) { replaceFragment(it) }
    }

    private fun replaceFragment(isTunedOn: Boolean) {
        // If module is screen and turned on
        val fragment = if (isTunedOn && viewModel.lightModule == ModuleBase.Module.MODULE_SCREEN) {
            LightFragment()
        } else {
            MainFragment()
        }

        val fragmentCurrent = supportFragmentManager.findFragmentById(R.id.layout_container)

        // Change fragment only if fragment is different
        if (fragmentCurrent == null || fragment.javaClass != fragmentCurrent.javaClass) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_container, fragment, fragment.javaClass.name)
                .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Stop service if user close app
        viewModel.toggleLight(false)
    }
}