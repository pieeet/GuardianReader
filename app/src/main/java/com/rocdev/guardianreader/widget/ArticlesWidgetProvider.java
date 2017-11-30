package com.rocdev.guardianreader.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.activities.MainActivity;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.QueryUtils;

/**
 * Implementation of App Widget functionality.
 */
public class ArticlesWidgetProvider extends AppWidgetProvider {

    private static final String TAG = ArticlesWidgetProvider.class.getSimpleName();

    private static final String ACTION_TIMER_TRIGGERED =
            "com.rocdev.guardianreader.set_widget_refresh_timer";
    public static final String ACTION_SET_REFRESH_RATE_TIMER =
            "com.rocdev.guardianreader.set_widget_refresh_rate_timer";

    private static final String PREF_DEFAULT_WIDGET_REFRESH_RATE = "2";

    private static final int WIDGET_ID_INVALID = -1;
    private static final int NO_REFRESH = -1;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        //construct RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.articles_widget);
        int sectionIndex = context.getSharedPreferences(WidgetConfigActivity.PREFS_NAME, 0)
                .getInt(String.valueOf(appWidgetId), 0);

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

        //If list is empty
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

    // onUpdate is called despite WidgetConfigActivity (not according to docs)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences(WidgetConfigActivity.PREFS_NAME,
                Context.MODE_PRIVATE);
        if (prefs == null) return;
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            int id = prefs.getInt(String.valueOf(appWidgetId), WIDGET_ID_INVALID);
            if (id != WIDGET_ID_INVALID) {
                startService(context, appWidgetId);
            }
        }
    }

    static void startService(Context context, int widgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WidgetConfigActivity
                .PREFS_NAME, Context.MODE_PRIVATE);
        int sectionIndex = sharedPreferences.getInt(String.valueOf(widgetId), 0);
        WidgetIntentService.startActionUpdateArticles(context, sectionIndex, widgetId);
    }

    // first widget added
    @Override
    public void onEnabled(Context context) {
        // see https://goo.gl/BjfHfo Google example
        int alarmType = AlarmManager.ELAPSED_REALTIME;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int interval = Integer.parseInt(prefs.getString(
                context.getString(R.string.pref_key_widget_refresh_rate),
                PREF_DEFAULT_WIDGET_REFRESH_RATE));
        // if user chose No Refresh do nothing.
        if (interval == NO_REFRESH) return;

        // The AlarmManager, like most system services, isn't created by application code, but
        // requested from the system.
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        // setRepeating takes a start delay and period between alarms as arguments.
        // The below code fires after 1 hour, and repeats every 1 hour.  This is very
        // useful for demonstration purposes, but horrendous for production.  Don't be that dev.
        if (alarmManager != null) {
            alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime() + interval,
                    interval, createTimerPendingIntent(context));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction() == null) return;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.
                getPackageName(), ArticlesWidgetProvider.class.getName()));
        switch (intent.getAction()) {
            case ACTION_TIMER_TRIGGERED:
                onUpdate(context, appWidgetManager, appWidgetIds);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
            case ACTION_SET_REFRESH_RATE_TIMER:
                if (appWidgetIds != null && appWidgetIds.length > 0)
                    onEnabled(context);
                break;
        }
    }

    private PendingIntent createTimerPendingIntent(Context context) {
        Intent setTimerIntent = new Intent(context, ArticlesWidgetProvider.class);
        setTimerIntent.setAction(ACTION_TIMER_TRIGGERED);
        return PendingIntent.getBroadcast(context,
                0, setTimerIntent, 0);
    }

    @Override
    public void onDisabled(Context context) {
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) alarmManager.cancel(createTimerPendingIntent(context));
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
