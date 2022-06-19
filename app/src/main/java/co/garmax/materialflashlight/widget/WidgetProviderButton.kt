package co.garmax.materialflashlight.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.features.LightManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetProviderButton : AppWidgetProvider(), KoinComponent {

    private val lightManager: LightManager by inject()

    private val widgetManager: WidgetManager by inject()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.view_widget_button)

            // Set on intent to handle onclick
            views.setOnClickPendingIntent(R.id.button_widget, getPendingSelfIntent(context))

            // Set image according to current state
            if (lightManager.isTurnedOn) {
                setWidgetImage(context, views, R.id.button_widget, R.drawable.ic_widget_button_on)
            } else {
                setWidgetImage(context, views, R.id.button_widget, R.drawable.ic_widget_button_off)
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_WIDGET_BUTTON_CLICK == intent.getAction()) {
            if (lightManager.isTurnedOn) lightManager.turnOff() else lightManager.turnOn()

            widgetManager.updateWidgets()
        }
    }

    private fun getPendingSelfIntent(context: Context): PendingIntent {
        // An explicit intent directed at the current class (the "self").
        val intent = Intent(context, javaClass).apply {
            action = ACTION_WIDGET_BUTTON_CLICK
        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    private fun setWidgetImage(
        context: Context,
        remoteViews: RemoteViews,
        viewRes: Int,
        imageRes: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            remoteViews.setImageViewResource(viewRes, imageRes)
        } else {
            ContextCompat.getDrawable(context, imageRes)?.let {
                val drawable = DrawableCompat.wrap(it).mutate()
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )

                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                remoteViews.setImageViewBitmap(viewRes, bitmap)
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_BUTTON_CLICK =
            "co.garmax.materialflashlight.action.WIDGET_BUTTON_CLICK"
    }
}