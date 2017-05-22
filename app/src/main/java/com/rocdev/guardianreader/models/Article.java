package com.rocdev.guardianreader.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by piet on 27-12-16.
 *
 */

public class Article implements Parcelable {
    private String title;
    private String date;
    private String url;
    private String section;
    private String thumbUrl;



    public Article(String title, String date, String url, String section, String thumbUrl) {
        this.title = title;
        this.date = date;
        this.url = url;
        this.section = section;
        this.thumbUrl = thumbUrl;
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