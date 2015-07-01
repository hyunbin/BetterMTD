package me.hyunbin.transit;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by Hyunbin on 3/9/15.
 */

public class Tab3 extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = Tab3.class.getSimpleName();

    private static final int REQUEST_RESOLVE_ERROR = 9000;
    private boolean mResolvingError = false;
    private static int NO_ERROR = 0;
    private static int ERROR_NETWORK = 1;
    private static int ERROR_LOCATION = 2;

    private static final String TAG_STOPS = "stops";
    private static final String TAG_STOPID = "stop_id";
    private static final String TAG_STOPNAME = "stop_name";
    private static final String TAG_DISTANCE = "distance";

    private RecyclerView mRecyclerView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private Location mLastLocation;
    private TextView mTextView;
    private NearMeAdapter mAdapter;

    ArrayList<HashMap<String, String>> mStopsList;

    private String mBaseUrl = "https://developer.cumtd.com/api/v2.2/json/GetStopsByLatLon";
    public List<NameValuePair> mParams;
    JSONArray mStopsArray = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets refresh parameter for location requests
        int updateInterval = 90000;
        int minUpdateInterval = 30000;

        // Begin Google Play Services location service
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(updateInterval)
                .setFastestInterval(minUpdateInterval);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_3,container,false);

        mContext = getActivity().getApplicationContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.near_me_view);
        mRecyclerView.setHasFixedSize(true);

        // Sets animator to RecyclerView
        mRecyclerView.setItemAnimator(new FadeInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(200);
        mRecyclerView.getItemAnimator().setRemoveDuration(100);

        mTextView = (TextView) v.findViewById(R.id.text_view);
        mTextView.setVisibility(View.GONE);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // Populates parameters with key information
        mParams = new ArrayList<NameValuePair>();
        mParams.add(new BasicNameValuePair("key", "***REMOVED***"));

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart called");
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    private void startParsing(Location location) {
        // Re-initialize parameters
        mParams = new ArrayList<NameValuePair>();
        mParams.add(new BasicNameValuePair("key", "***REMOVED***"));

        if (location != null) {
            onErrorStatusChanged(NO_ERROR);
            // Populates parameters with lat/lon information
            mParams.add(new BasicNameValuePair("lat", String.valueOf(location.getLatitude())));
            mParams.add(new BasicNameValuePair("lon", String.valueOf(location.getLongitude())));
            new ParseLocationRequest().execute();
        }
        else{
            onErrorStatusChanged(ERROR_LOCATION);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Connected successfully to Google Play Services");
        mResolvingError = false;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation == null) {
            Log.d(TAG, "Last Location reported as null");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            onErrorStatusChanged(ERROR_LOCATION);
        }
        else{
            Log.d(TAG, "Last Location found");
            onErrorStatusChanged(NO_ERROR);
            startParsing(mLastLocation);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Connection failed to Google Play Services");
        onErrorStatusChanged(ERROR_LOCATION);
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Log.e(TAG, "Connection to Google API client has failed");
            mResolvingError = false;
        }
    }

    private void onErrorStatusChanged(int mode){
        if(mode == ERROR_NETWORK){
            mTextView.setText("Network error\nCheck your internet connection");
            mTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        if(mode == ERROR_LOCATION){
            mTextView.setText("Location not found\nTry enabling Wifi, GPS, or Location");
            mTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        else{
            mTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended to Google Play Services");
    }

    public void refreshAdapter(){
        // Either sets an adapter if none has been initialized, or makes appropriate calls to
        // enable animations in the RecyclerView.
        if(mAdapter == null) {
            mAdapter = new NearMeAdapter(mContext, mStopsList);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount() - 1);
        }
        else if(mAdapter != null) {
            mAdapter.addAllItems(mStopsList);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        Log.d(TAG, "onPause called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if(mGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            startParsing(mLastLocation);
        } else
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (!mResolvingError) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        startParsing(location);
    }

    private class ParseLocationRequest extends AsyncTask<Void, Void, Void>
    {
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Re-initializes the arraylist to clear any previously stored information
            mStopsList = new ArrayList<HashMap<String, String>>();
            if(mAdapter != null){
                mAdapter.removeAllItems();
            }
        }

        protected Void doInBackground(Void ... arg0) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(mBaseUrl, mParams);

            try {
                if (jsonStr != null) {
                    // Get JSON Object from string in ServiceHandler response
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    mStopsArray = jsonObj.getJSONArray(TAG_STOPS);

                    // Parses through JSON array to populate HashMap / ArrayLists
                    for (int i = 0; i < mStopsArray.length(); i++) {
                        JSONObject c = mStopsArray.getJSONObject(i);

                        String stopID = c.getString(TAG_STOPID);
                        String stopName = c.getString(TAG_STOPNAME);
                        String distance = c.getString(TAG_DISTANCE);

                        HashMap<String, String> stop = new HashMap<String, String>();

                        stop.put(TAG_STOPID, stopID);
                        stop.put(TAG_STOPNAME, stopName);
                        stop.put(TAG_DISTANCE, distance);

                        mStopsList.add(stop);
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            refreshAdapter();
            if(mStopsArray == null) {
                onErrorStatusChanged(ERROR_NETWORK);
            }
            else{
                onErrorStatusChanged(NO_ERROR);
            }
        }
    }
}