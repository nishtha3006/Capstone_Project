package com.example.nishtha.capstone.Query;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nishtha.capstone.Data.SongContract;
import com.example.nishtha.capstone.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FetchTopTracks extends AsyncTask<String,Void,Void> {
    String LOG_TAG="hello";
    HttpURLConnection urlConnection=null;
    BufferedReader reader;
    String moviesJsonStr;
    Context mcontex;
    public FetchTopTracks(Context context){
        mcontex=context;
    }
    @Override
    protected Void doInBackground(String... params) {
        try{
            final String BASE_URL = "http://ws.audioscrobbler.com/2.0/?";
            final String PARAMS1 = "method=geo.gettoptracks";
            final String API_KEY = "api_key="+ Utility.LAST_FM_API_KEY;
            final String PARAMS2 = "format=json";
            final String LFTopTracksURL = BASE_URL + PARAMS1 + "&country=" + params[0] + "&" + API_KEY + "&" + PARAMS2;

            URL url = new URL(LFTopTracksURL);
            Log.v("hello", "THE URL IS: " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.d("hello","null inputStream");
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                Log.d("hello", "buffer is empty..");
            }
            moviesJsonStr = buffer.toString();
            getSongsData(moviesJsonStr);

        }catch(Exception e){
                Log.e(LOG_TAG,e.getMessage());
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                try{
                    if(reader!=null){
                        reader.close();
                    }
                }catch (Exception e){
                    Log.e(LOG_TAG,"reader didnt close properly");
                }
            }
            return null;
    }

    private void getSongsData(String song_details){
        Log.d("hello","data is + "+song_details);
        try {
            JSONObject object = new JSONObject(song_details);
            if(object.has("error")) {
                Log.d("hello", "has error");
                int errorCode = object.getInt("error");
                if(errorCode == 6) {
                    // Country param error
                    Log.d("hello", "country params error");
                    ContentValues[] errors = new ContentValues[1];
                    ContentValues error = new ContentValues();
                    error.put("error", 6);
                    errors[0] = error;
                    return;
                }
            }
            JSONArray array = object.getJSONObject("tracks").getJSONArray("track");
            int size = Math.min(array.length(),12);
            ContentValues[] values = new ContentValues[size];
            Log.d("hello", "size is" + size);
            for(int i = 0; i < size; i++) {
                JSONObject songObj = array.getJSONObject(i);
                String title = songObj.getString("name");
                String artist = songObj.getJSONObject("artist").getString("name");
                String imageUrl = songObj.getJSONArray("image").getJSONObject(2).getString("#text");

                ContentValues value = new ContentValues();
                value.put(SongContract.Song.COLUMN_TITLE, title);
                value.put(SongContract.Song.COLUMN_ARTIST, artist);
                value.put(SongContract.Song.COLUMN_IMAGE_URL, imageUrl);
                values[i] = value;
            }
            int inserted = 0;
            if(values != null) {
                Log.d("hello","values are not null");
                inserted= mcontex.getContentResolver().bulkInsert(SongContract.Song.CONTENT_URI, values);
            }
            Log.d("hello ",inserted+" rows has been inserted");
            return;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }
}
