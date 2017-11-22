package com.rocdev.guardianreader.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.activities.MainActivity;
import com.rocdev.guardianreader.utils.QueryUtils;

/**
 * Implementation of App Widget functionality.
 */
public class ArticlesWidgetProvider extends AppWidgetProvider {

    private static final String TAG = ArticlesWidgetProvider.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))); /* ??? */
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.articles_widget);
        views.setRemoteAdapter(R.id.lv_widget_articles, intent);
        // Set the MainActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.setAction(MainActivity.EXTRA_KEY_SECTION_FROM_WIDGET);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0,
                appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_widget_articles, appPendingIntent);

        views.setEmptyView(R.id.lv_widget_articles, R.id.tv_widget_articles_empty_view);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            startService(context, appWidgetId);
        }
    }

    static void startService(Context context, int widgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WidgetConfigActivity
                .PREFS_NAME, Context.MODE_PRIVATE);
        int sectionIndex = sharedPreferences.getInt(String.valueOf(widgetId), 0);
        WidgetIntentService.startActionUpdateArticles(context, sectionIndex, widgetId);
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
        SharedPreferences prefs = context.getSharedPreferences(WidgetConfigActivity.PREFS_NAME,
                Context.MODE_PRIVATE);
        for (int appWidgetId: appWidgetIds) {
            prefs.edit().remove(String.valueOf(appWidgetId)).apply();
            QueryUtils.deleteWidgetArticles(context, appWidgetId);
        }
    }
}
