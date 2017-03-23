package com.example.nishtha.capstone;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utility {

    //Enter your last-fm api key and uncomment below line
    //public static final String LAST_FM_API_KEY = "LAST_FM_API_KEY";

    // Enter you music match api key and uncomment below line
    //public static final String MUSIC_MATCH_API_KEY = "MUSIC_MATCH_API_KEY";


    public static boolean isNetworkAvailable(Context context,Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)activity.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
