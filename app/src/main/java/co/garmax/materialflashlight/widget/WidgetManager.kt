package co.garmax.materialflashlight.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import co.garmax.materialflashlight.widget.WidgetProviderButton

class WidgetManager(private val context: Context) {

    fun updateWidgets() {
        // Update widgets
        val idsWidgetButton = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, WidgetProviderButton::class.java))
        val intentButton = Intent(context, WidgetProviderButton::class.java)
            .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idsWidgetButton)
        context.sendBroadcast(intentButton)
    }

}