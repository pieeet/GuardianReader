package com.rocdev.guardianreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by piet on 24-05-17.
 *
 */

class DbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "guardianreader.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 2;

    private static final String SQL_CREATE_ARTICLES_TABLE = "CREATE TABLE "
            + Contract.ArticleEntry.TABLE_NAME + " ("
            + Contract.ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Contract.ArticleEntry.COLUMN_ARTICLE_TITLE + " TEXT NOT NULL, "
            + Contract.ArticleEntry.COLUMN_ARTICLE_DATE + " TEXT NOT NULL, "
            + Contract.ArticleEntry.COLUMN_ARTICLE_URL + " TEXT NOT NULL, "
            + Contract.ArticleEntry.COLUMN_ARTICLE_SECTION + " TEXT NOT NULL, "
            + Contract.ArticleEntry.COLUMN_THUMB_URL + " TEXT);";

    private static final String SQL_CREATE_WIDGET_ARTICLES = "CREATE TABLE "
            + Contract.WidgetArticleEntry.TABLE_NAME + " ("
            + Contract.WidgetArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Contract.WidgetArticleEntry.COLUMN_ARTICLE_TITLE + " TEXT NOT NULL, "
            + Contract.WidgetArticleEntry.COLUMN_ARTICLE_DATE + " TEXT NOT NULL, "
            + Contract.WidgetArticleEntry.COLUMN_ARTICLE_URL + " TEXT NOT NULL, "
            + Contract.WidgetArticleEntry.COLUMN_ARTICLE_SECTION + " TEXT NOT NULL, "
            + Contract.WidgetArticleEntry.COLUMN_THUMB_URL + " TEXT"
            + Contract.WidgetArticleEntry.COLUMN_WIDGET_ID + " INTEGER NOT NULL);";



    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WIDGET_ARTICLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {


        switch(oldVersion) {
            case 1:
                sqLiteDatabase.execSQL(SQL_CREATE_WIDGET_ARTICLES);
                // for next version do not put breaks
                // see https://stackoverflow.com/questions/8133597/android-upgrading-db-version-and-adding-new-table

        }
    }
}
