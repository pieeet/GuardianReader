package com.rocdev.guardianreader.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by piet on 24-05-17.
 *
 */

public class Contract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private Contract() {}


    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    static final String CONTENT_AUTHORITY = "com.rocdev.guardianreader";


    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.rocdev.inventoryTracker/articles/ is a valid path for
     * looking at article data. content://com.rocdev.inventoryTracker/suppliers/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "suppliers".
     */
    static final String PATH_ARTICLES = "articles";


    public static final class ArticleEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ARTICLES);

//        /**
//         * The MIME type of the {@link #CONTENT_URI} for a list of articles.
//         */
//        public static final String CONTENT_LIST_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLES;

//        /**
//         * The MIME type of the {@link #CONTENT_URI} for a single article.
//         */
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLES;


        /** Name of database table for articles */
        final static String TABLE_NAME = "articles";

        /**
         * Unique ID number for the article (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;



        /**
         * title of the article
         *
         * Type: TEXT
         */
        public static final String COLUMN_ARTICLE_TITLE = "title";


        /**
         * date of the article
         *
         * Type: TEXT
         */
        public static final String COLUMN_ARTICLE_DATE = "date";


        /**
         * url of the article
         *
         * Type: TEXT
         */
        public static final String COLUMN_ARTICLE_URL = "url";


        /**
         * url of the article
         *
         * Type: TEXT
         */
        public static final String COLUMN_ARTICLE_SECTION = "section";


        /**
         * url of the article
         *
         * Type: TEXT
         */
        public static final String COLUMN_THUMB_URL = "thumbnail";

    }

}
