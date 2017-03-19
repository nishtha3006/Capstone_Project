package com.example.nishtha.capstone.Query;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nishtha.capstone.DetailSongFragment;
import com.example.nishtha.capstone.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FetchLyrics extends AsyncTask<String,Void,String> {

    OkHttpClient okClient = new OkHttpClient();
    DetailSongFragment fragment;
    Context mcontext;

    public FetchLyrics(DetailSongFragment fragment, Context mcontext){
        this.fragment=fragment;
        this.mcontext = mcontext;
    }

    @Override
    protected String doInBackground(String... params) {
        final String BASE_URL = "http://api.musixmatch.com/ws/1.1/";

        final String PARAMS1 = "track.lyrics.get?track_id=";
        final String PARAMS2 = "matcher.track.get?";
        final String PARAMS3 = "q_artist=";
        final String PARAMS4 = "q_track=";
        final String CALLBACK = "callback=callback";
        final String API_KEY = "apikey="+ Utility.MUSIC_MATCH_API_KEY;
        try{
            String track_url = BASE_URL + PARAMS2 + PARAMS3 + params[1] + "&"
                    + PARAMS4 + params[0] + "&" + API_KEY;
            long trackId = getTrackId(getData(track_url));
            if(trackId == -1){
                return "No lyrics";
            }
            String lyrics_url = BASE_URL + PARAMS1 + trackId
                    + "&" + CALLBACK + "&" + API_KEY;
            String lyrics = getLyrics(getData(lyrics_url));
            Log.d("hello", lyrics);
            return lyrics;
        }catch (Exception e){
            Log.e("hello", e.getMessage());
        }
        return null;
    }

    public String getData(String url) {
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            response = okClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String lyrics) {
        super.onPostExecute(lyrics);
        fragment.setLyrics(lyrics);

    }

    public long getTrackId(String response){
        try {
            JSONObject object = new JSONObject(response);
            JSONObject track = object.getJSONObject("message").getJSONObject("body").getJSONObject("track");
            long trackId = track.getLong("track_id");
            int has_lyrics = track.getInt("has_lyrics");
            if(has_lyrics == 0) {
                return -1;
            }
            return trackId;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getLyrics(String string) {
        try {
            JSONObject object = new JSONObject(string);
            JSONObject lyricsObj = object.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics");
            String lyrics_song = lyricsObj.getString("lyrics_body");
            return lyrics_song;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
