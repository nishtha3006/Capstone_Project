package com.example.nishtha.capstone.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


public class SongProvider extends ContentProvider{
    static final int FAV = 100;
    static final int SEARCH = 101;
    static final int SONG_TITLE_ART = 102;


    private static final SQLiteQueryBuilder FAV_QUERY_BUILDER;
    private static final SQLiteQueryBuilder SEARCH_QUERY_BUILDER;

    static{
        SEARCH_QUERY_BUILDER = new SQLiteQueryBuilder();
        SEARCH_QUERY_BUILDER.setTables(SongContract.Song.TABLE_NAME);
        FAV_QUERY_BUILDER = new SQLiteQueryBuilder();
        FAV_QUERY_BUILDER.setTables(SongContract.Favourite.TABLE_NAME);
    }

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SongDbHelper songDbHelper;

    //title = ? AND artist = ?
    private static final String TitleAndArtistSelect = SongContract.Favourite.COLUMN_TITLE + " = ? AND " +
            SongContract.Favourite.COLUMN_ARTIST + " = ? ";


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String contentAuthority = SongContract.CONTENT_AUTHORITY;

        matcher.addURI(contentAuthority, SongContract.PATH_SEARCH, SEARCH);
        matcher.addURI(contentAuthority, SongContract.PATH_FAV, FAV);
        matcher.addURI(contentAuthority, SongContract.PATH_FAV + "/*/*", SONG_TITLE_ART);

        return matcher;
    }

    public SongProvider() {
    }

    @Override
    public boolean onCreate() {
        songDbHelper = new SongDbHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = songDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int Deleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        switch (match) {
            case SEARCH:
                Deleted = db.delete(SongContract.Song.TABLE_NAME, selection, selectionArgs);
                break;
            case SONG_TITLE_ART:
                Deleted = db.delete(SongContract.Favourite.TABLE_NAME, TitleAndArtistSelect,selectionArgs);
                Log.d("hello","song has been deleted "+ Deleted);
                break;
            case FAV:
                Deleted = db.delete(SongContract.Favourite.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (Deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return Deleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAV:
                return SongContract.Favourite.CONTENT_TYPE;
            case SEARCH :
                return SongContract.Song.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = songDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case SEARCH: {
                try {
                    long _id = db.insertOrThrow(SongContract.Song.TABLE_NAME, null, values);
                    if ( _id > 0 )
                        returnUri = SongContract.Favourite.buildSongUri(_id);
                } catch (SQLiteConstraintException exception) {
                }
                break;
            }
            case FAV: {
                try {
                    long _id = db.insertOrThrow(SongContract.Favourite.TABLE_NAME, null, values);
                    if ( _id > 0 )
                        returnUri = SongContract.Favourite.buildSongUri(_id);
                } catch (SQLiteConstraintException exception) {
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case SEARCH: {
                retCursor = songDbHelper.getReadableDatabase().query(
                        SongContract.Song.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            }
            case FAV: {
                retCursor = songDbHelper.getReadableDatabase().query(
                        SongContract.Favourite.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            }
            case SONG_TITLE_ART: {
                retCursor = getSongByArtistTitle(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = songDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case SEARCH:
                rowsUpdated = db.update(SongContract.Song.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAV:
                rowsUpdated = db.update(SongContract.Favourite.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = songDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SEARCH:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        try {
                            long _id = db.insertOrThrow(SongContract.Song.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            } } catch (SQLiteConstraintException e) {
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                Log.d("hello",returnCount+" songs has been added to the song table");
                return returnCount;
            case FAV:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        try {
                            long _id = db.insertOrThrow(SongContract.Favourite.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            } } catch (SQLiteConstraintException e) {
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getSongByArtistTitle(Uri uri, String[] projection, String sortOrder) {
        String title = SongContract.Favourite.getTitleFromUri(uri);
        String artist = SongContract.Favourite.getArtistFromUri(uri);

        String selection = TitleAndArtistSelect;
        String[] selectionArgs = new String[]{title, artist};

        return FAV_QUERY_BUILDER.query(songDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}
