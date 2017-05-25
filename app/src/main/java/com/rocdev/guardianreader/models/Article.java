package com.rocdev.guardianreader.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by piet on 27-12-16.
 *
 */

public class Article implements Parcelable {
    private long _ID;
    private String title;
    private String date;
    private String url;
    private String section;
    private String thumbUrl;



    public Article(String title, String date, String url, String section, String thumbUrl) {
        this._ID = -1;
        this.title = title;
        this.date = date;
        this.url = url;
        this.section = section;
        this.thumbUrl = thumbUrl;
    }

    /**
     *
     * @param _ID from saved article
     * @param title
     * @param date
     * @param url
     * @param section
     * @param thumbUrl
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }


    protected Article(Parcel in) {
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