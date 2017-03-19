package com.example.nishtha.capstone.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class SongContract {
    public static final String CONTENT_AUTHORITY="com.example.nishtha.capstone";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SEARCH = "search";

    public static final class Song implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH;

        // Table name
        public static final String TABLE_NAME = "search";

        // Columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ARTIST = "artist";
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static Uri buildSongUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final String PATH_FAV = "fav";

    public static final class Favourite implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;

        // Table name
        public static final String TABLE_NAME = "favourite";

        // Columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ARTIST = "artist";
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static Uri buildSongUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getArtistFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildTitleArtistUri(String title, String artist) {
            return CONTENT_URI.buildUpon().appendPath(title).appendPath(artist).build();
        }
    }

}
