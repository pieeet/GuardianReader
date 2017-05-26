package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.database.Cursor;

import com.rocdev.guardianreader.database.Contract;
import com.rocdev.guardianreader.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piet on 27-12-16.
 * This class is only meant to hold static variables and methods
 */

class QueryUtils {

    private static final String RESPONSE = "response";
    private static final String EDITOR_PICKS = "editorsPicks";
    private static final String RESULTS = "results";
    private static final String WEB_TITLE = "webTitle";
    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String WEB_URL = "webUrl";
    private static final String SECTION_NAME = "sectionName";
    private static final String FIELDS = "fields";
    private static final String THUMBNAIL = "thumbnail";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {}


    /**
     * @return a list of {@link Article} objects that has been built up from parsing a JSON response.
     */
    static ArrayList<Article> extractArticles(String urlStr, boolean isEditorsPick) {
        StringBuilder output = new StringBuilder();
        URL url = makeUrl(urlStr);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        } catch (IOException ignored) {} finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
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
                JSONObject article = results.getJSONObject(i);
                String webTitle = article.getString(WEB_TITLE);
                String webPublicationDate = article.getString(WEB_PUBLICATION_DATE);
                String webUrl = article.getString(WEB_URL);
                String sectionName = article.getString(SECTION_NAME);
                String thumbnail = null;
                // there might not be a thumbnail
                try {
                    JSONObject fields = article.getJSONObject(FIELDS);
                    thumbnail = fields.getString(THUMBNAIL);
                } catch (Exception ignored) {}
                articles.add(new Article(webTitle, webPublicationDate, webUrl, sectionName, thumbnail));
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


}
