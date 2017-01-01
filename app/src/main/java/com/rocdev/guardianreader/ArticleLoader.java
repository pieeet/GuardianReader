package com.rocdev.guardianreader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by piet on 27-12-16.
 *
 */

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private String mUrl;
    private boolean mIsEditorsPick;


    public ArticleLoader(Context context, String url, boolean isEditorsPick) {
        super(context);
        mUrl = url;
        mIsEditorsPick = isEditorsPick;
    }

    @Override
    public List<Article> loadInBackground() {

        return QueryUtils.extractArticles(mUrl, mIsEditorsPick);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
