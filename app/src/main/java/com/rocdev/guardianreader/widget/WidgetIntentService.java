package com.rocdev.guardianreader.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.QueryUtils;
import com.rocdev.guardianreader.utils.UriBuilder;

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

    private static final String ACTION_UPDATE_ARTICLES =
            "com.rocdev.guardianreader.widget.action.UPDATE_ARTICLES";

    private static final String ACTION_SAVE_ARTICLES =
            "com.rocdev.guardianreader.widget.action.SAVE_ARTICLES";

    private static final String ACTION_UPDATE_WIDGETS =
            "com.rocdev.guardianreader.widget.action.UPDATE_WIDGETS";


    // EXTRA_KEYS

    private static final String EXTRA_SECTION_INDEX =
            "com.rocdev.guardianreader.widget.extra.SECTION_URL";

    private static final String EXTRA_WIDGET_ID =
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
        intent.setAction(ACTION_UPDATE_WIDGETS);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
            if (action != null) {
                switch (action) {
                    case ACTION_UPDATE_ARTICLES:
                        final int sectionIndex = intent.getIntExtra(EXTRA_SECTION_INDEX,
                                0);
                        handleActionUpdateArticles(sectionIndex, widgetId);
                        break;
                    case ACTION_SAVE_ARTICLES:
                        int appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
                        List<Article> articles = intent.getParcelableArrayListExtra(EXTRA_WIDGET_ARTICLES);
                        handleActionSaveArticles(appWidgetId, articles);
                        break;
                    case ACTION_UPDATE_WIDGETS:
                        handleActionUpdateWidgets(widgetId);
                        break;
                    default:
                        throw new RuntimeException(action + "is not a valid action");
                }
            }
        }
    }

    /**
     * Handle action Fetch mArticles in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateArticles(int sectionIndex, int appWidgetId) {
//        Section section = Section.values()[sectionIndex];
//        boolean isEditorPicks = sectionIndex <= Section.HEADLINES_INT.ordinal();
//        mArticles = QueryUtils.extractArticles(section.getUrl(), isEditorPicks);
//        Log.d(TAG, "handleActionUpdateArticles triggered");
//
//        startActionSaveArticles(this, mAppWidgetId);
        new DownloaderTask().execute(sectionIndex, appWidgetId);

    }

    private void handleActionSaveArticles(int appWidgetId, List<Article> articles) {
        QueryUtils.deleteWidgetArticles(this, appWidgetId);
        QueryUtils.insertWidgetArticles(this, articles, appWidgetId);
        Log.d(TAG, "handleActionSaveeArticles triggered");
        Log.d(TAG, "no of mArticles: " + articles.size());
        startActionUpdateWidget(this, appWidgetId);
    }

    private void handleActionUpdateWidgets(int widgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.lv_widget_articles);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ArticlesWidgetProvider.class));
        ArticlesWidgetProvider.updateArticleWidgets(this, appWidgetManager, appWidgetIds);
        Log.d(TAG, "handleActionUpdateWidgets triggered");
    }



    class DownloaderTask extends AsyncTask<Integer, Void, List<Article>> {

        int mAppWidgetId;


        @Override
        protected List<Article> doInBackground(Integer... integers) {
            int sectionIndex = integers[0];
            mAppWidgetId = integers[1];
            boolean isEditorPicks = sectionIndex <= Section.HEADLINES_INT.ordinal();
            int currentPage = 1;
            Uri uri = UriBuilder.buildUriWithParams( currentPage, sectionIndex,null);
            return QueryUtils.extractArticles(uri.toString(), isEditorPicks);
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            startActionSaveArticles(WidgetIntentService.this, mAppWidgetId, articles);
        }
    }





}