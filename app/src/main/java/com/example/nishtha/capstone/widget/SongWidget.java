package com.example.nishtha.capstone.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.example.nishtha.capstone.DetailSong;
import com.example.nishtha.capstone.R;


public class SongWidget extends AppWidgetProvider {

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main_screen);

        views.setRemoteAdapter(R.id.quote_widget_list, new Intent(context, SongWidgetService.class));

        Intent clickIntentTemplate = new Intent(context, DetailSong.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.quote_widget_list, clickPendingIntentTemplate);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

