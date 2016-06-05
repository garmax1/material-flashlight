package co.garmax.materialflashlight.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.modes.ModeBase
import co.garmax.materialflashlight.modes.ModeService

/**
 * Emit light flow for screen module
 */
class ScreenModuleActivity : AppCompatActivity() {

    @Bind(R.id.layout_content)
    lateinit var mLayoutContent: View

    /**
     * Receive and handle commands from screen module
     */
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            if (intent.getBooleanExtra(EXTRA_FINISH, false)) {
                finish()
            } else {
                // Get brightness value
                val brightness = intent.getIntExtra(EXTRA_BRIGHTNESS_PERCENT, 100);

                setBrightness(brightness)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_module)
        ButterKnife.bind(this)

        // Disable appbar
        supportActionBar?.hide()

        // Set max brightness
        val lp = window.attributes;
        lp.screenBrightness = 1f;
        window.attributes = lp;
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).
                registerReceiver(mBroadcastReceiver, IntentFilter(ACTION_SCREEN_MODULE));
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @OnClick(R.id.fab)
    fun onFabClick() {
        ModeService.setMode(this, ModeBase.MODE_OFF)
        finish()
    }

    private fun setBrightness(percent: Int) {

        val color = Color.argb(255 * percent / 100, 0, 0, 0)

        mLayoutContent.setBackgroundColor(color)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //TODO close main activity on back button as well

        ModeService.setMode(this, ModeBase.MODE_OFF)

        finish()
    }

    companion object {
        const val ACTION_SCREEN_MODULE = "action_screen_module";

        const val EXTRA_BRIGHTNESS_PERCENT = "extra_brightness_percent";
        const val EXTRA_FINISH = "extra_finish";
    }
}