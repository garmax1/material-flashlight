package co.garmax.materialflashlight.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Used to encapsulate resource retrieving with context
 */
public class ResourceProvider {

    private final Context context;

    public ResourceProvider(Context context) {
        this.context = context;
    }

    public void showToast(@StringRes int stringResId) {
        Toast.makeText(context, stringResId, Toast.LENGTH_LONG).show();
    }

}
