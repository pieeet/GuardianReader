package com.rocdev.guardianreader.widget;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.ArticleLoader;
import com.rocdev.guardianreader.utils.QueryUtils;

import java.util.List;

/**
 * Created by piet on 12-11-17.
 *
 */

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory,
        LoaderManager.LoaderCallbacks<List<Article>> {
    private Context mContext;
    private List<Article> mArticles;
    private int mAppWidgetId;
    private Section section;

    ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    @Override
    public void onCreate() {

        SharedPreferences prefs = mContext.getSharedPreferences(WidgetConfigActivity.PREFS_NAME,
                Context.MODE_PRIVATE);
        int sectionIndex = prefs.getInt(String.valueOf(mAppWidgetId), 0);
        section = Section.values()[sectionIndex];

    }

    @Override
    public void onDataSetChanged() {


    }

    @Override
    public void onDestroy() {

        mArticles.clear();
    }

    @Override
    public int getCount() {
        if (mArticles == null) return 0;
        return mArticles.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rv = null;
        if (mArticles != null) {
            rv = new RemoteViews(mContext.getPackageName(), R.layout.articles_widget_listitem);
            rv.setTextViewText(R.id.tv_widget_item_name, mArticles.get(i).getTitle());
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        return new ArticleLoader(mContext, section.getUrl(),
                section.ordinal() <= Section.HEADLINES_INT.ordinal());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {

        mArticles = articles;
        onDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {

    }





}
