package co.garmax.materialflashlight.ui

import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    open val isInImmersiveMode = false

    override fun onResume() {
        super.onResume()

        if (isInImmersiveMode) setFullscreen() else exitFullscreen()
    }

    private fun setFullscreen() {
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN

        if (Build.VERSION.SDK_INT >= 19) {
            flags =
                flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        requireActivity().window.decorView.systemUiVisibility = flags
    }

    private fun exitFullscreen() {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}