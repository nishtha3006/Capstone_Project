package com.example.nishtha.capstone;

import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.nishtha.capstone.Adapters.GridAdapter;
import com.example.nishtha.capstone.Data.SongContract;
import com.example.nishtha.capstone.Query.FetchTopTracks;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopSongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    GridAdapter madapter;
    String country;
    final int SONG_LOADER =0;
    Context mcontext = null;
    boolean fav;
    String[] projection_movie=new String[]{
            SongContract.Song.TABLE_NAME+"."+ SongContract.Song._ID,
            SongContract.Song.COLUMN_TITLE,
            SongContract.Song.COLUMN_ARTIST,
            SongContract.Song.COLUMN_IMAGE_URL
    };
    String[] projection_fav=new String[]{
            SongContract.Favourite.TABLE_NAME+"."+ SongContract.Favourite._ID,
            SongContract.Favourite.COLUMN_TITLE,
            SongContract.Favourite.COLUMN_ARTIST,
            SongContract.Favourite.COLUMN_IMAGE_URL
    };

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    public static final int COL_TITLE = 1;
    public static final int COL_ARTIST = 2;
    public static final int COL_IMAGE_URL = 3;

    int no_fav_song =0;

    public TopSongsFragment(){

        mcontext=getContext();
        country = "india";
        fav=false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mcontext = getContext();
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
         void onItemSelected(Song song);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            Log.d("hello","getting connected");
            mGoogleApiClient.connect();
        }else{
            updateSongList(country);
        }

    }

    private void updateSongList(String country){
        fav=false;
        if(Utility.isNetworkAvailable(getContext(),getActivity())) {
            FetchTopTracks fetchTopTracks = new FetchTopTracks(getContext());
            fetchTopTracks.execute(country);
            getLoaderManager().restartLoader(SONG_LOADER, null, this);
        }else {
            Toast.makeText(getContext(),mcontext.getString(R.string.connection_fail),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.song_type,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.sort_by_pop){
            updateSongList(country);
            return  true;
        }
        if(item.getItemId()==R.id.fav){
            fav=true;
            loadFavouriteSong();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavouriteSong(){
        no_fav_song =getContext().getContentResolver()
                .query(SongContract.Favourite.CONTENT_URI,null,null,null,null).getCount();
        if(no_fav_song ==0){
            mcontext = getContext();
            Toast.makeText(getContext(),mcontext.getString(R.string.fav_list_empty),Toast.LENGTH_LONG).show();
            fav = false;
            getLoaderManager().restartLoader(SONG_LOADER, null, this);
        }else
            getLoaderManager().restartLoader(SONG_LOADER, null, this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        madapter=new GridAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grids);
        gridView.setAdapter(madapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor=(Cursor)parent.getItemAtPosition(position);
                Song song =new Song(cursor);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(song);
                }
            }
        });
        return  rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SONG_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        Uri content_uri;
        if(fav){
            loader = new CursorLoader(getActivity(),
                    SongContract.Favourite.CONTENT_URI,
                    projection_fav,
                    null,
                    null,
                    null);
        }else{
            content_uri= SongContract.Song.CONTENT_URI;
            loader=new CursorLoader(getActivity(),content_uri,projection_movie,null,null,null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        madapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        madapter.swapCursor(null);
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {
        Log.d("hello","in display location");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = null;
            Log.d("hello","mlastlocation is not null");
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    country = addresses.get(0).getCountryName();
                }
                no_fav_song =getContext().getContentResolver()
                        .query(SongContract.Favourite.CONTENT_URI,null,null,null,null).getCount();
                if((fav && no_fav_song ==0)){
                    Toast.makeText(getContext(),mcontext.getString(R.string.fav_list_empty),Toast.LENGTH_LONG).show();
                    updateSongList(country);
                }else if(!fav){
                    Log.d("hello","in top songs");
                    updateSongList(country);
                }
            } catch (IOException ignored) {
                //do something
            }
        } else {
            Log.d("hello","mlocation is null");
            updateSongList(country);
        }
    }

    /**
     * Creating google api client object
     * */
    //Reference : http://stackoverflow.com/questions/29801368/how-to-show-enable-location-dialog-like-google-maps
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        displayLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(), 1000);
                            displayLocation();
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getContext(),
                        mcontext.getString(R.string.device_not_supported), Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("hello", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
//        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
}
