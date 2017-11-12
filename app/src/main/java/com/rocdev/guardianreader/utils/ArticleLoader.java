package com.rocdev.guardianreader.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.rocdev.guardianreader.database.Contract;
import com.rocdev.guardianreader.models.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piet on 27-12-16.
 *
 */

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private final String mUrl;
    private final boolean mIsEditorsPick;

    private ArrayList<Article> articles;


    public ArticleLoader(Context context, String url, boolean isEditorsPick) {
        super(context);
        mUrl = url;
        mIsEditorsPick = isEditorsPick;
    }

    @Override
    public List<Article> loadInBackground() {
        if (mUrl.equals(Contract.ArticleEntry.CONTENT_URI.toString())) {
            return QueryUtils.extractSavedArticles(getContext());
        }
        return QueryUtils.extractArticles(mUrl, mIsEditorsPick);
    }

    @Override
    protected void onStartLoading() {
        if (articles != null ) {
            deliverResult(articles);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Article> data) {
        articles = (ArrayList<Article>) data;
        super.deliverResult(data);

    }
}
