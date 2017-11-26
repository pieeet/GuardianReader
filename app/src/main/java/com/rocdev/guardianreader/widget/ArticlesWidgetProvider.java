package com.rocdev.guardianreader.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.activities.MainActivity;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.QueryUtils;

/**
 * Implementation of App Widget functionality.
 */
public class ArticlesWidgetProvider extends AppWidgetProvider {

    private static final String TAG = ArticlesWidgetProvider.class.getSimpleName();
    private static final String ACTION_SYNC_CLICKED = "com.rocdev.guardianreader.widget.sync_button_clicked";
    public static final String KEY_WIDGET_ID = "com.rocdev.guardianreader.widget.app_widget_id";
    private static final int WIDGET_ID_INVALID = -1;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d(TAG, "updateAppWidget triggered. appWidgetId: " + appWidgetId);

        //construct RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.articles_widget);
        int sectionIndex = context.getSharedPreferences(WidgetConfigActivity.PREFS_NAME, 0)
                .getInt(String.valueOf(appWidgetId), 0);

        Log.d(TAG, "updateAppWidget triggered. sectionIndex: " + sectionIndex);
        //set title
        Section section = Section.values()[sectionIndex];
        String sectionTitle = context.getResources().getString(section.getTitle());
        views.setTextViewText(R.id.tv_widget_section_title, sectionTitle);

        //set adapter
        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))); /* ??? */
        views.setRemoteAdapter(R.id.lv_widget_articles, intent);

        // Set the MainActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.setAction(MainActivity.ACTION_INTENT_FROM_WIDGET);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0,
                appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_widget_articles, appPendingIntent);
        views.setEmptyView(R.id.lv_widget_articles, R.id.tv_widget_articles_empty_view);

        // set PendingIntent on refresh button to start service
        Intent syncIntent = new Intent(context, WidgetIntentService.class);
        //see https://goo.gl/hhkFL2
        syncIntent.setAction(WidgetIntentService.ACTION_REFRESH_WIDGET + appWidgetId);
        PendingIntent syncPendingIntent = PendingIntent.getService(context, 0,
                syncIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.ib_refresh_button, syncPendingIntent);

        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                ArticlesWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.lv_widget_articles);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager,
                                     int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    // onUpdate is called despite WidgetConfigActivity (not according to docs)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate triggered");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            SharedPreferences prefs = context.getSharedPreferences(WidgetConfigActivity.PREFS_NAME,
                    Context.MODE_PRIVATE);
            if (prefs != null) {
                int id = prefs.getInt(String.valueOf(appWidgetId), WIDGET_ID_INVALID);
                if (id != WIDGET_ID_INVALID) {
                    startService(context, appWidgetId);
                }
            }
        }
    }

    static void startService(Context context, int widgetId) {
        Log.d(TAG, "startService triggered");
        SharedPreferences sharedPreferences = context.getSharedPreferences(WidgetConfigActivity
                .PREFS_NAME, Context.MODE_PRIVATE);
        int sectionIndex = sharedPreferences.getInt(String.valueOf(widgetId), 0);
        WidgetIntentService.startActionUpdateArticles(context, sectionIndex, widgetId);
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
        for (int appWidgetId : appWidgetIds) {
            prefs.edit().remove(String.valueOf(appWidgetId)).apply();
            QueryUtils.deleteWidgetArticles(context, appWidgetId);
        }
    }


}
