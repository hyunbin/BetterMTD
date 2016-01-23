package me.hyunbin.transit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

    private static int PERMISSIONS_REQUEST_LOC = 128;
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
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout mEmptySwipeRefreshLayout;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
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
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_nearme, container, false);
        mContext = getActivity();

        // Initialize Retrofit client and callback response
        mRestClient = new RestClient();
        mCallback = new Callback<StopsByLatLonResponse>() {
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

        // Sets animator to RecyclerView
        mRecyclerView.setItemAnimator(new FadeInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(400);
        mRecyclerView.getItemAnimator().setRemoveDuration(400);

        mTextView = (TextView) v.findViewById(R.id.text_view);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // Sets SwipeRefreshLayout to enable the swipe-to-refresh gesture
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLocationData();
            }
        });

        mEmptySwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout_emptyView);
        mEmptySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLocationData();
            }
        });
        mEmptySwipeRefreshLayout.setVisibility(View.GONE);

        mCoordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordLayout);
        return v;
    }

    @Override
    public void onStart() {
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
        refreshLocationData();
    }

    private void refreshLocationData(){
        // Before we make location requests, check for permission
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // This is where we should show further rationale, because the user
                // has previous denied a permission request.
                onErrorStatusChanged(ERROR_LOCATION);
                Log.e(TAG, "Showing re-enable permission prompt");
                String message = "Location permission is disabled.";
                Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Enable", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOC);
                            }
                        })
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOC);
            }
        } else{
            mEmptySwipeRefreshLayout.setEnabled(true);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation == null) {
                Log.d(TAG, "Last location reported as null. Requesting location updates.");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                onErrorStatusChanged(ERROR_LOCATION);
            } else {
                Log.d(TAG, "Last location found. Now parsing.");
                onErrorStatusChanged(NO_ERROR);
                startParsing(mLastLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        for (int i = 0; i < permissions.length; i++) {
            if(permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                // Permission was granted!
                Log.e(TAG, "Location permission reported as GRANTED");
                if (!mResolvingError) {
                    mGoogleApiClient.connect();
                }
            } else{
                // Permission denied, alert user
                Log.e(TAG, "Location permission reported as DENIED");
                refreshLocationData();
            }
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
            mEmptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        }
        if(mode == ERROR_LOCATION){
            mTextView.setText("Location not found\nTry enabling Wifi, GPS, or Location");
            mEmptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        }
        else{
            mEmptySwipeRefreshLayout.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
        mEmptySwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setRefreshing(false);
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
            mAdapter.notifyItemRangeInserted(0, data.size() - 1);
        }
        else {
            mAdapter.swapData(data);
            mAdapter.notifyItemRangeChanged(0, data.size() - 1);
        }
    }
}