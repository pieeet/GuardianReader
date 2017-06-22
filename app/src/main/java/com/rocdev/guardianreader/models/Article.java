package com.rocdev.guardianreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piet on 27-12-16.
 *
 */

public class Article implements Parcelable {

    //Keys jsonObject
    private static final String WEB_TITLE = "webTitle";
    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String WEB_URL = "webUrl";
    private static final String SECTION_NAME = "sectionName";
    private static final String FIELDS = "fields";
    private static final String THUMBNAIL = "thumbnail";

    private long _ID;
    private String title;
    private String date;
    private String url;
    private String section;
    private String thumbUrl;


    /**
     *
     * @param jsonObject The retrieved JSONObject from api-call
     * @throws JSONException JSONException thrown
     */
    public Article(JSONObject jsonObject) throws JSONException {
        this._ID = -1;
        title = jsonObject.getString(WEB_TITLE);
        date = jsonObject.getString(WEB_PUBLICATION_DATE);
        url = jsonObject.getString(WEB_URL);
        section = jsonObject.getString(SECTION_NAME);
        thumbUrl = null;
        // there might not be a thumbnail
        try {
            JSONObject fields = jsonObject.getJSONObject(FIELDS);
            thumbUrl = fields.getString(THUMBNAIL);
        } catch (Exception ignored) {
        }
    }

    /**
     * @param _ID      from saved article
     * @param title    title
     * @param date     date
     * @param url      url article
     * @param section  section
     * @param thumbUrl url thumbnail
     */
    public Article(long _ID, String title, String date, String url, String section, String thumbUrl) {
        this._ID = _ID;
        this.title = title;
        this.date = date;
        this.url = url;
        this.section = section;
        this.thumbUrl = thumbUrl;
    }

    public long get_ID() {
        return _ID;
    }

    public void set_ID(long _ID) {
        this._ID = _ID;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getSection() {
        return section;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }



    private Article(Parcel in) {
        _ID = in.readLong();
        title = in.readString();
        date = in.readString();
        url = in.readString();
        section = in.readString();
        thumbUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_ID);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(url);
        dest.writeString(section);
        dest.writeString(thumbUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}