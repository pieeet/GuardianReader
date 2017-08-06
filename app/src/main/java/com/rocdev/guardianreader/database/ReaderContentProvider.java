package com.rocdev.guardianreader.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by piet on 24-05-17.
 *
 */

public class ReaderContentProvider extends ContentProvider {

    private DbHelper mDbHelper;

//    private static final String LOG_TAG = ReaderContentProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the articles table
     */
    private static final int ARTICLES = 100;

    /**
     * URI matcher code for the content URI for a single article in the articles table
     */
    private static final int ARTICLE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        //Articles
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY,
                Contract.PATH_ARTICLES, ARTICLES);
        //Article
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY,
                Contract.PATH_ARTICLES + "/#", ARTICLE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case ARTICLES:
                cursor = db.query(Contract.ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Cannot query unknown URI: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ARTICLES:
                return insertArticle(uri, contentValues);
            default:
                throw new UnsupportedOperationException("Cannot insert unknown URI: " + uri);
        }
    }

    private Uri insertArticle(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(Contract.ArticleEntry.TABLE_NAME, null, contentValues);
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case (ARTICLE_ID):
                return deleteArticle(uri);
            default:
                throw new UnsupportedOperationException("Cannot delete unknown URI: " + uri);
        }
    }


    private int deleteArticle(Uri uri) {
        String selection = Contract.ArticleEntry._ID + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(Contract.ArticleEntry.TABLE_NAME, selection, selectionArgs);
        if (rowsDeleted > 0) {
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }


    // Not used
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
