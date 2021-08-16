package co.garmax.materialflashlight.extensions

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

val Int.asDp get() = (this * Resources.getSystem().displayMetrics.density).toInt()

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