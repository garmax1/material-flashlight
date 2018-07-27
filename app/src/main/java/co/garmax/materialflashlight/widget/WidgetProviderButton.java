package co.garmax.materialflashlight.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.RemoteViews;

import javax.inject.Inject;

import co.garmax.materialflashlight.R;
import co.garmax.materialflashlight.features.LightManager;
import dagger.android.AndroidInjection;
import timber.log.Timber;

public class WidgetProviderButton extends AppWidgetProvider {

    public static final String ACTION_WIDGET_BUTTON_CLICK
            = "co.garmax.materialflashlight.action.WIDGET_BUTTON_CLICK";

    @Inject
    LightManager lightManager;

    @Inject
    WidgetManager widgetManager;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        AndroidInjection.inject(this, context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.view_widget_button);

            // Set on intent to handle onclick
            views.setOnClickPendingIntent(R.id.button_widget, getPendingSelfIntent(context,
                    ACTION_WIDGET_BUTTON_CLICK));

            // Set image according to current state
            if (lightManager.isTurnedOn()) {
                setWidgetImage(context, views, R.id.button_widget, R.drawable.ic_widget_button_on);
            } else {
                setWidgetImage(context, views, R.id.button_widget, R.drawable.ic_widget_button_off);
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
        super.onReceive(context, intent);

        if (ACTION_WIDGET_BUTTON_CLICK.equals(intent.getAction())) {

            if (lightManager.isTurnedOn()) {
                lightManager.turnOff();
            } else {
                lightManager.turnOn();
            }

            widgetManager.updateWidgets();
        }
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void setWidgetImage(Context context, RemoteViews remoteViews, int viewRes, int imageRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            remoteViews.setImageViewResource(viewRes, imageRes);
        } else {
            Drawable drawable = ContextCompat.getDrawable(context, imageRes);

            if (drawable == null) {

                Timber.e("Can't get drawable for resId %d and set for widget", imageRes);
                return;
            }

            drawable = DrawableCompat.wrap(drawable).mutate();

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            remoteViews.setImageViewBitmap(viewRes, bitmap);
        }
    }
}
