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
    private static final int DATABASE_VERSION = 1;

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_ARTICLES_TABLE = "CREATE TABLE " + Contract.ArticleEntry.TABLE_NAME + " ("
                + Contract.ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.ArticleEntry.COLUMN_ARTICLE_TITLE + " TEXT NOT NULL, "
                + Contract.ArticleEntry.COLUMN_ARTICLE_DATE + " TEXT NOT NULL, "
                + Contract.ArticleEntry.COLUMN_ARTICLE_URL + " TEXT NOT NULL, "
                + Contract.ArticleEntry.COLUMN_ARTICLE_SECTION + " TEXT NOT NULL, "
                + Contract.ArticleEntry.COLUMN_THUMB_URL + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
