package com.example.nishtha.capstone.Query;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nishtha.capstone.DetailSongFragment;
import com.example.nishtha.capstone.Song;
import com.example.nishtha.capstone.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FetchTopTracksByArtist extends AsyncTask<String,Void,String>{

    OkHttpClient okClient = new OkHttpClient();
    Song[] songs;
    DetailSongFragment fragment;
    Context context;

    public FetchTopTracksByArtist(DetailSongFragment fragment, Context context){
        this.fragment=fragment;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        final String BASE_URL = "http://ws.audioscrobbler.com/2.0/?";

        final String PARAMS1 = "method=artist.gettoptracks";
        final String API_KEY = "api_key="+ Utility.LAST_FM_API_KEY;
        final String PARAMS2 = "format=json";

        String tracks_url = BASE_URL+PARAMS1+"&artist="+params[0]+"&"+API_KEY+"&"+PARAMS2;
       // http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist=cher&api_key=YOUR_API_KEY&format=json
        try{
            Log.d("hello",tracks_url);
            Request request = new Request.Builder()
                    .url(tracks_url)
                    .build();
            Response response = null;
            try {
                response = okClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            Log.e("hello", e.getMessage());
        }
        return null;
    }

    private void getSongs(String response) throws JSONException {
        try {
            JSONObject toptracks = new JSONObject(response);
            JSONArray track_array = toptracks.getJSONObject("toptracks").getJSONArray("track");
            int size = Math.min(track_array.length(), 6);
            songs = new Song[size];
            for (int i = 0; i < size; i++) {
                JSONObject temp = track_array.getJSONObject(i);
                Song temp_song = new Song();
                temp_song.setTitle(temp.getString("name"));
                temp_song.setArtist(temp.getJSONObject("artist").getString("name"));
                temp_song.setImage_url(temp.getJSONArray("image").getJSONObject(2).getString("#text"));
                temp_song.setSong_url(temp.getString("url"));
                songs[i] = temp_song;
            }
        } catch (Exception e) {
            Log.d("hello", "NJKDSJNCDS  " + e.getMessage() + " here");
        }
    }
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.d("hello", "in post");
        try{
            getSongs(response);
        }catch(Exception e){
            Log.e("hello",e.getMessage());
        }
        fragment.setTrailerAdapter(context,songs);
    }
}
