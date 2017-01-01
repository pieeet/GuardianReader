package com.rocdev.guardianreader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by piet on 27-12-16.
 *
 */

public class QueryUtils {


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Article> extractArticles(String urlStr, boolean isEditorsPick) {
        StringBuilder output = new StringBuilder();
        URL url = makeUrl(urlStr);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        InputStream in = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Create an empty ArrayList that we can start adding Articles to
        ArrayList<Article> articles = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {


            // build up a list of Article objects with the corresponding data.
            JSONObject root = new JSONObject(output.toString());
            JSONObject response = root.getJSONObject("response");
            JSONArray results = null;
            if (isEditorsPick) {
                results = response.getJSONArray("editorsPicks");
            } else {
                results = response.getJSONArray("results");
            }



            for (int i = 0; i < results.length(); i++) {
                JSONObject article = results.getJSONObject(i);
                String webTitle = article.getString("webTitle");
                String webPublicationDate = article.getString("webPublicationDate");
                String webUrl = article.getString("webUrl");
                String sectionName = article.getString("sectionName");
                String thumbnail = null;
                try {
                    JSONObject fields = article.getJSONObject("fields");
                    thumbnail = fields.getString("thumbnail");
                } catch (Exception e) {}

                articles.add(new Article(webTitle, webPublicationDate, webUrl, sectionName, thumbnail));

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Article JSON results", e);
        }

        // Return the list of Articles
        return articles;
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


}
