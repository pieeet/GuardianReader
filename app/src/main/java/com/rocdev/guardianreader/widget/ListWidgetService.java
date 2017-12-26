package com.rocdev.guardianreader.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.activities.MainActivity;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.utils.ArticleDateUtils;
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
    private int mSectionIndex;


//    private static final String TAG = ListWidgetService.class.getSimpleName();

    ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        SharedPreferences prefs = mContext.getSharedPreferences(WidgetConfigActivity.PREFS_NAME,
                Context.MODE_PRIVATE);
        mSectionIndex = prefs.getInt(String.valueOf(mAppWidgetId), 0);
    }


    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        mArticles = QueryUtils.getWidgetArticlesFromDatabase(mContext, mAppWidgetId);
    }

    @Override
    public void onDestroy() {
        if (mArticles != null) mArticles.clear();
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
            try {
                Article article = mArticles.get(i);
                rv = new RemoteViews(mContext.getPackageName(),
                        R.layout.widget_list_item_relative_layout);
                rv.setTextViewText(R.id.titleTextView, article.getTitle().replace("\n", ""));
                rv.setTextViewText(R.id.dateTextView, ArticleDateUtils
                        .formatDateTime(article.getDate()));
                rv.setTextViewText(R.id.sectionTextView, article.getSection());
                try {
                    Bitmap b = Picasso.with(mContext).load(mArticles.get(i).getThumbUrl()).get();
                    rv.setImageViewBitmap(R.id.thumbnail, b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //pass extras to intent
                Bundle extras = new Bundle();
                extras.putInt(MainActivity.EXTRA_SECTION_INDEX, mSectionIndex);
                extras.putInt(MainActivity.EXTRA_APP_WIDGET_ID, mAppWidgetId);
                extras.putParcelable(MainActivity.EXTRA_ARTICLE, article);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                rv.setOnClickFillInIntent(R.id.article_item_container, fillInIntent);
            } catch (Exception ignored) {}
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
