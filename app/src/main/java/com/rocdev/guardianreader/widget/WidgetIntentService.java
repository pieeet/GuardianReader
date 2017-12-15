package com.rocdev.guardianreader.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rocdev.guardianreader.database.Contract;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.ArticlesUriBuilder;
import com.rocdev.guardianreader.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WidgetIntentService extends IntentService {

    private static final String TAG = WidgetIntentService.class.getSimpleName();

    // ACTIONS
    public static final String ACTION_UPDATE_ARTICLES =
            "com.rocdev.guardianreader.widget.action.UPDATE_ARTICLES";
    private static final String ACTION_SAVE_ARTICLES =
            "com.rocdev.guardianreader.widget.action.SAVE_ARTICLES";
    private static final String ACTION_UPDATE_WIDGET =
            "com.rocdev.guardianreader.widget.action.UPDATE_WIDGETS";
    public static final String ACTION_REFRESH_WIDGET =
            "com.rocdev.guardianreader.widget.action.SYNC_PRESSED";
    public static String PREF_KEY_LAST_SYNCED =
            "com.rocdev.guardianreader.widget.action.LAST_SYNCED";

    // EXTRA_KEYS
    public static final String EXTRA_SECTION_INDEX =
            "com.rocdev.guardianreader.widget.extra.SECTION_URL";
    public static final String EXTRA_WIDGET_ID =
            "com.rocdev.guardianreader.widget.extra.WIDGET_ID";
    private static final String EXTRA_WIDGET_ARTICLES =
            "com.rocdev.guardianreader.widget.extra.ARTICLES";
    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    /**
     * Starts this service to perform action Fetch Articles with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateArticles(Context context, int sectionIndex, int widgetId) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(ACTION_UPDATE_ARTICLES);
        intent.putExtra(EXTRA_SECTION_INDEX, sectionIndex);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        context.startService(intent);
    }

    public static void startActionSaveArticles(Context context, int widgetId, List<Article> articles) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(ACTION_SAVE_ARTICLES);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        intent.putParcelableArrayListExtra(EXTRA_WIDGET_ARTICLES,
                (ArrayList<? extends Parcelable>) articles);
        context.startService(intent);
    }

    public static void startActionUpdateWidget(Context context, int widgetId) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        final String action = intent.getAction();
        if (action == null) return;

        //see https://goo.gl/hhkFL2
        if (action.startsWith(ACTION_REFRESH_WIDGET)) {
            refreshWidgetFromButton(action);

        } else {
            final int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
            switch (action) {
                case ACTION_UPDATE_ARTICLES:
                    final int sectionIndex = intent.getIntExtra(EXTRA_SECTION_INDEX,
                            Contract.INVALID_SECTION_INDEX);
                    handleActionUpdateArticles(sectionIndex, widgetId);
                    break;
                case ACTION_SAVE_ARTICLES:
                    List<Article> articles = intent.getParcelableArrayListExtra(EXTRA_WIDGET_ARTICLES);
                    handleActionSaveArticles(widgetId, articles);
                    break;
                case ACTION_UPDATE_WIDGET:
                    handleActionUpdateWidget(widgetId);
                    break;
                default:
                    throw new RuntimeException(action + "is not a valid action");
            }
        }
    }

    private void refreshWidgetFromButton(String action) {
        int appWidgetId = -1;
        try {
            appWidgetId = Integer.parseInt(action
                    .substring(ACTION_REFRESH_WIDGET.length()));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        SharedPreferences prefs = getSharedPreferences(WidgetConfigActivity.
                PREFS_NAME, MODE_PRIVATE);

        // restrict use of sync button to 1 call per minute
        long lastSync = prefs.getLong(PREF_KEY_LAST_SYNCED + appWidgetId, -1);
        long now = System.currentTimeMillis();
        if (lastSync == -1 || now - lastSync > 1000*60) {
            prefs.edit().putLong(PREF_KEY_LAST_SYNCED + appWidgetId, now).apply();
            int sectionIndex = prefs.getInt(String.valueOf(appWidgetId),
                    Contract.INVALID_SECTION_INDEX);
            handleActionUpdateArticles(sectionIndex, appWidgetId);
        }
    }

    /**
     * Handle action Fetch mArticles in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateArticles(int sectionIndex, int appWidgetId) {
        if (sectionIndex == Contract.INVALID_SECTION_INDEX) return;
        boolean isEditorPicks = sectionIndex <= Section.HEADLINES_INT.ordinal();
        int currentPage = 1;
        Uri uri = ArticlesUriBuilder.buildUriWithParams(currentPage, sectionIndex, null);
        List<Article> articles = QueryUtils.extractArticles(uri.toString(), isEditorPicks);
        //TODO uncomment for production
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(WidgetIntentService.this);
        mFirebaseAnalytics.logEvent("api_call_widget", null);
        startActionSaveArticles(this, appWidgetId, articles);
    }

    private void handleActionSaveArticles(int appWidgetId, List<Article> articles) {
        if (articles == null || articles.isEmpty()) return;
        QueryUtils.deleteWidgetArticles(this, appWidgetId);
        QueryUtils.insertWidgetArticles(this, articles, appWidgetId);
        startActionUpdateWidget(this, appWidgetId);
    }

    private void handleActionUpdateWidget(int widgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ArticlesWidgetProvider.updateAppWidget(this, appWidgetManager, widgetId);
    }
}