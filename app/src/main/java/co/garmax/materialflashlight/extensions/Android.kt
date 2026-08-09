package co.garmax.materialflashlight.extensions

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import co.garmax.materialflashlight.R

val Int.asDp get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.applyFabWindowInsets() {
    val baseMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        view.updateLayoutParams<MarginLayoutParams> {
            marginEnd = baseMargin + navigationBars.right
            bottomMargin = baseMargin + navigationBars.bottom
        }
        insets
    }
    if (isAttachedToWindow) {
        ViewCompat.requestApplyInsets(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun <T> liveDataOf(defValue: T? = null) = MutableLiveData<T>().apply {
    defValue?.apply { value = this }
}

fun <T : Any?, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
    liveData.observe(
        when {
            this is Fragment && view != null -> viewLifecycleOwner
            else -> this
        },
        Observer(body)
    )

fun <T : Any, L : LiveData<T>> LifecycleOwner.observeNotNull(liveData: L, body: (T) -> Unit) =
    liveData.observe(
        when {
            this is Fragment && view != null -> viewLifecycleOwner
            else -> this
        },
        { it?.let(body) }
    )