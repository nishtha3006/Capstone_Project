package com.example.nishtha.capstone.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SongDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "SongsDatabase.db";

    public SongDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to store fav songs.
        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE " + SongContract.Favourite.TABLE_NAME + " (" +
                SongContract.Favourite._ID + " INTEGER, " +
                SongContract.Favourite.COLUMN_TITLE + " TEXT UNIQUE NOT NULL, " +
                SongContract.Favourite.COLUMN_ARTIST + " TEXT NOT NULL, " +
                SongContract.Favourite.COLUMN_IMAGE_URL + " TEXT, " +
                "PRIMARY KEY ( " + SongContract.Favourite.COLUMN_TITLE + " ) " +
                " );";

        // Create table to store most popular
        final String SQL_CREATE_SONG_TABLE = "CREATE TABLE " + SongContract.Song.TABLE_NAME + " (" +
                SongContract.Song._ID + " INTEGER, " +
                SongContract.Song.COLUMN_TITLE + " TEXT NOT NULL, " +
                SongContract.Song.COLUMN_ARTIST + " TEXT NOT NULL, " +
                SongContract.Song.COLUMN_IMAGE_URL + " TEXT, " +
                "PRIMARY KEY ( " + SongContract.Song.COLUMN_TITLE + " ) " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAV_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SongContract.Favourite.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SongContract.Song.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
