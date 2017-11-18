package com.rocdev.guardianreader.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.QueryUtils;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WidgetIntentService extends IntentService {


    // ACTIONS

    private static final String ACTION_UPDATE_ARTICLES =
            "com.rocdev.guardianreader.widget.action.UPDATE_ARTICLES";


    // EXTRA_KEYS

    private static final String EXTRA_SECTION_INDEX =
            "com.rocdev.guardianreader.widget.extra.SECTION_URL";

    private static final String EXTRA_WIDGET_ID =
            "com.rocdev.guardianreader.widget.extra.WIDGET_ID";



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
        Section section = Section.values()[sectionIndex];
        intent.setAction(ACTION_UPDATE_ARTICLES);
        intent.putExtra(EXTRA_SECTION_INDEX, sectionIndex);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            switch (action) {
                case ACTION_UPDATE_ARTICLES:
                    final int sectionIndex = intent.getIntExtra(EXTRA_SECTION_INDEX, 0);
                    final int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
                    handleActionUpdateArticles(sectionIndex, widgetId);
                    break;
            }
        }
    }

    /**
     * Handle action Fetch articles in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateArticles(int sectionIndex, int widgetId) {
        Section section = Section.values()[sectionIndex];
        boolean isEditorPicks = sectionIndex <= Section.HEADLINES_INT.ordinal();
        List<Article> articles = QueryUtils.extractArticles(section.getUrl(), isEditorPicks);
        QueryUtils.deleteWidgetArticles(this, widgetId);
        QueryUtils.insertWidgetArticles(this, articles, widgetId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ArticlesWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_articles);
        ArticlesWidgetProvider.updateArticleWidgets(this, appWidgetManager, appWidgetIds);
    }


}