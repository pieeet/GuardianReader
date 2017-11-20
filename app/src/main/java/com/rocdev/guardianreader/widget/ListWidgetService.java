package com.rocdev.guardianreader.widget;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.database.Contract;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.ArticleLoader;
import com.rocdev.guardianreader.utils.QueryUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Article> mArticles;
    private int mAppWidgetId;
    private Section section;


    private static final String TAG = ListWidgetService.class.getSimpleName();

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
        Log.d(TAG, "onDataSetChanged triggered");
        mArticles = QueryUtils.getWidgetArticlesFromDatabase(mContext, mAppWidgetId);
        if (mArticles == null) Log.d(TAG, "mArticles = null");
        else if(mArticles.isEmpty()) Log.d(TAG, "mArticles is empty");
        for (Article article: mArticles) {
            Log.d(TAG, article.getTitle());
        }
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
            try {
                Bitmap b = Picasso.with(mContext).load(mArticles.get(i).getThumbUrl()).get();
                rv.setImageViewBitmap(R.id.iv_widget_item_thumb, b);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        return false;
    }



}
