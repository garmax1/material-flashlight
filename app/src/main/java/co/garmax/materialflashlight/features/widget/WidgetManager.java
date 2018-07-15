package co.garmax.materialflashlight.features.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class WidgetManager {

    private final Context context;

    public WidgetManager(Context context) {
        this.context = context;
    }

    public void updateWidgets() {
        // Update widgets
        int[] idsWidgetButton = AppWidgetManager.getInstance(context).
                getAppWidgetIds(new ComponentName(context, WidgetProviderButton.class));

        Intent intentButton = new Intent(context, WidgetProviderButton.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idsWidgetButton);
        context.sendBroadcast(intentButton);
    }
}
