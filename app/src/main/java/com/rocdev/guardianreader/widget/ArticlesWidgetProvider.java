package com.rocdev.guardianreader.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.rocdev.guardianreader.R;

/**
 * Implementation of App Widget functionality.
 */
public class ArticlesWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))); /* ??? */
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.articles_widget);
        views.setRemoteAdapter(R.id.lv_widget_articles, intent);
        views.setEmptyView(R.id.lv_widget_articles, R.id.tv_widget_articles_empty_view);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views); /* ???unnecessary???*/
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_widget_articles);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WidgetConfigActivity
                .PREFS_NAME, Context.MODE_PRIVATE);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            int sectionIndex = sharedPreferences.getInt(String.valueOf(appWidgetId), 0);
            WidgetIntentService.startActionUpdateArticles(context, sectionIndex, appWidgetId);
        }
    }

    public static void updateArticleWidgets(Context context, AppWidgetManager appWidgetManager,
                                            int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}
