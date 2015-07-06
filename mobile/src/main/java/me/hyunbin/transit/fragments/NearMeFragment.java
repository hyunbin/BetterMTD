package me.hyunbin.transit.fragments;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import me.hyunbin.transit.R;
import me.hyunbin.transit.RestClient;
import me.hyunbin.transit.adapters.NearMeAdapter;
import me.hyunbin.transit.models.Stop;
import me.hyunbin.transit.models.StopsByLatLonResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hyunbin on 3/9/15.
 */

public class NearMeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = NearMeFragment.class.getSimpleName();

    private static final int REQUEST_RESOLVE_ERROR = 9000;
    private boolean mResolvingError = false;
    private static int NO_ERROR = 0;
    private static int ERROR_NETWORK = 1;
    private static int ERROR_LOCATION = 2;

    private RecyclerView mRecyclerView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private RestClient mRestClient;
    private Callback<StopsByLatLonResponse> mCallback;
    private Context mContext;
    private Location mLastLocation;
    private TextView mTextView;
    private NearMeAdapter mAdapter;

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
        View v =inflater.inflate(R.layout.fragment_nearme,container,false);
        mContext = getActivity();

        // Initialize Retrofit client and callback response
        mRestClient = new RestClient();
        mCallback = new Callback<StopsByLatLonResponse>(){
            @Override
            public void success(StopsByLatLonResponse responseObject, Response response) {
                Log.d(TAG, "Retrofit success!");
                List<Stop> stopList = responseObject.getStops();
                refreshAdapter(stopList);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Retrofit Error: " + error.toString());
                onErrorStatusChanged(ERROR_NETWORK);
            }
        };

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

    private void startParsing(Location location) {
        if (location != null) {
            onErrorStatusChanged(NO_ERROR);
            // Populates parameters with lat/lon information
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            sendDataRequest(lat, lon);
        }
        else{
            onErrorStatusChanged(ERROR_LOCATION);
        }
    }

    private void sendDataRequest(double lat, double lon){
        mRestClient.getStopsByLatLon(lat, lon, mCallback);
    }

    private void refreshAdapter(List<Stop> data){
        // Either sets an adapter if none has been initialized, or swaps existing adapter.
        if(mAdapter == null) {
            mAdapter = new NearMeAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter = new NearMeAdapter(data);
            mRecyclerView.swapAdapter(mAdapter, false);
        }
    }

}