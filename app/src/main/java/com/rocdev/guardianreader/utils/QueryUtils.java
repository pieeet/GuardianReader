package com.rocdev.guardianreader.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.database.Contract;
import com.rocdev.guardianreader.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by piet on 27-12-16.
 * This class is only meant to hold static variables and methods
 */

public class QueryUtils {

    private static final String RESPONSE = "response";
    private static final String EDITOR_PICKS = "editorsPicks";
    private static final String RESULTS = "results";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {}


    /**
     * @return a list of {@link Article} objects that has been built up from parsing a JSON response.
     */
    public static ArrayList<Article> extractArticles(String urlStr, boolean isEditorsPick) {
        StringBuilder output = new StringBuilder();
        URL url = makeUrl(urlStr);
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                output.append(scanner.next());
            } else {
                return null;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        ArrayList<Article> articles = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(output.toString());
            JSONObject response = root.getJSONObject(RESPONSE);
            JSONArray results;
            if (isEditorsPick) {
                results = response.getJSONArray(EDITOR_PICKS);
            } else {
                results = response.getJSONArray(RESULTS);
            }
            for (int i = 0; i < results.length(); i++) {
                try {
                    articles.add(new Article(results.getJSONObject(i)));
                } catch (JSONException ignored) {
                    // just skip the article
                }
            }
        } catch (JSONException ignored) {}
        return articles;
    }

    static ArrayList<Article> extractSavedArticles(Context context) {
        Cursor cursor = context.getContentResolver().query(Contract.ArticleEntry.CONTENT_URI,
                null, null, null, null);
        return makeListFromCursor(cursor);
    }

    private static URL makeUrl(String urlStr) {
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static ArrayList<Article> makeListFromCursor(Cursor cursor) {
        ArrayList<Article> articles = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long _id = cursor.getLong(cursor.getColumnIndex(Contract.ArticleEntry._ID));
                String title = cursor.getString(cursor.getColumnIndex(Contract.ArticleEntry.COLUMN_ARTICLE_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(Contract.ArticleEntry.COLUMN_ARTICLE_DATE));
                String url = cursor.getString(cursor.getColumnIndex(Contract.ArticleEntry.COLUMN_ARTICLE_URL));
                String section = cursor.getString(cursor.getColumnIndex(Contract.ArticleEntry.COLUMN_ARTICLE_SECTION));
                String thumbUrl = cursor.getString(cursor.getColumnIndex(Contract.ArticleEntry.COLUMN_THUMB_URL));
                articles.add(new Article(_id, title, date, url, section, thumbUrl));
            }
            cursor.close();
        }
        return articles;
    }

    public static long insertArticle(Article article, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.ArticleEntry.COLUMN_ARTICLE_DATE, article.getDate());
        contentValues.put(Contract.ArticleEntry.COLUMN_ARTICLE_SECTION, article.getSection());
        contentValues.put(Contract.ArticleEntry.COLUMN_ARTICLE_TITLE, article.getTitle());
        contentValues.put(Contract.ArticleEntry.COLUMN_ARTICLE_URL, article.getUrl());
        contentValues.put(Contract.ArticleEntry.COLUMN_THUMB_URL, article.getThumbUrl());
        Uri uri = context.getContentResolver().insert(Contract.ArticleEntry.CONTENT_URI, contentValues);

        long id = ContentUris.parseId(uri);
        if (id < 1) {
            Toast.makeText(context, R.string.save_article_error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.save_article_success, Toast.LENGTH_SHORT).show();
            article.set_ID(id);
        }

        return ContentUris.parseId(uri);
    }


    public static int insertWidgetArticles(Context context, List<Article> articles, int widgetId) {

        ContentValues[] contentValuesArray = new ContentValues[articles.size()];
        int index = 0;
        for (Article article: articles) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.WidgetArticleEntry.COLUMN_ARTICLE_DATE, article.getDate());
            contentValues.put(Contract.WidgetArticleEntry.COLUMN_ARTICLE_SECTION, article.getSection());
            contentValues.put(Contract.WidgetArticleEntry.COLUMN_ARTICLE_TITLE, article.getTitle());
            contentValues.put(Contract.WidgetArticleEntry.COLUMN_ARTICLE_URL, article.getUrl());
            contentValues.put(Contract.WidgetArticleEntry.COLUMN_THUMB_URL, article.getThumbUrl());
            contentValues.put(Contract.WidgetArticleEntry.COLUMN_WIDGET_ID, widgetId);
            contentValuesArray[index] = contentValues;
            index++;
        }
        Uri uri = Uri.withAppendedPath(Contract.WidgetArticleEntry.CONTENT_URI, String.valueOf(widgetId));
        return context.getContentResolver().bulkInsert(uri,
                contentValuesArray);
    }


    public static int deleteWidgetArticles(Context context, int widgetId) {
        Uri uri = Uri.withAppendedPath(Contract.WidgetArticleEntry.CONTENT_URI,
                String.valueOf(widgetId));
        int rowsDeleted = context.getContentResolver().delete(uri, null,
                null);
        return rowsDeleted;
    }

    public static List<Article> getWidgetArticlesFromDatabase(Context context, int widgetId) {
        Uri uri = Uri.withAppendedPath(Contract.WidgetArticleEntry.CONTENT_URI,
                String.valueOf(widgetId));
        Cursor cursor = context.getContentResolver().query(uri, null,
                Contract.WidgetArticleEntry.COLUMN_WIDGET_ID + " = ?",
                new String[] {String.valueOf(widgetId)}, null);
        return makeListFromCursor(cursor);

    }



    public static int deleteArticle(Article article, Context context) {
        Uri uri = Uri.withAppendedPath(Contract.ArticleEntry.CONTENT_URI,
                String.valueOf(article.get_ID()));

        int deletedRows = context.getContentResolver().delete(uri, null, null);

        if (deletedRows < 1) {
            Toast.makeText(context, R.string.delete_article_error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.delete_article_success, Toast.LENGTH_SHORT).show();
        }
        return deletedRows;
    }


}