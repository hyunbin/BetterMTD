package me.hyunbin.transit;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
        GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "Tab3";
    private final String TAG_STOPS = "stops";
    private final String TAG_STOPID = "stop_id";
    private final String TAG_STOPNAME = "stop_name";
    private final String TAG_DISTANCE = "distance";

    private RecyclerView nearMeView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context context;
    private Location mLastLocation;
    private TextView textView;
    private NearMeAdapter adapter;

    public SwipeRefreshLayout swipeLayout;
    public SwipeRefreshLayout emptySwipeLayout;
    public TextView nothingHere;

    ArrayList<HashMap<String, String>> stopsList;

    private String baseURL = "https://developer.cumtd.com/api/v2.2/json/GetStopsByLatLon";
    public List<NameValuePair> params;
    JSONArray stops = null;

    private Handler handler;
    private int updateInterval;
    long lastRefreshTime;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_3,container,false);

        context = getActivity().getApplicationContext();
        nearMeView = (RecyclerView) v.findViewById(R.id.nearmeView);
        nearMeView.setItemAnimator(new FadeInAnimator());
        nearMeView.getItemAnimator().setAddDuration(200);
        nearMeView.getItemAnimator().setRemoveDuration(100);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        nearMeView.setLayoutManager(layoutManager);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        // Populates parameters with key information
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key","107516afa39d442fb728498a32e43e35"));

        // Sets handler refresh parameter to refresh the RecyclerView periodically
        updateInterval = 90000;
        handler = new Handler();

        lastRefreshTime = System.currentTimeMillis() - 60000;
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    private void startParsing(Location location) {
        // Re-initalize parameters
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key", "107516afa39d442fb728498a32e43e35"));

        if(System.currentTimeMillis() - lastRefreshTime > 50000){
            if (location != null) {
                textView.setVisibility(View.GONE);
                // Populates parameters with lat/lon information
                params.add(new BasicNameValuePair("lat", String.valueOf(location.getLatitude())));
                params.add(new BasicNameValuePair("lon", String.valueOf(location.getLongitude())));
                new ParseLocationRequest().execute();
            }
            else{
                textView.setVisibility(View.VISIBLE);
                textView.setText("Failed to get location :c");
            }
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(20000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startParsing(mLastLocation);
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location){
        startParsing(location);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startParsing(mLastLocation);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        swipeLayout.setVisibility(View.GONE);
        emptySwipeLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void refreshAdapter(){
        // Either sets an adapter if none has been initialized, or makes appropriate calls to
        // enable animations in the RecyclerView.
        if(adapter==null)
        {
            adapter = new NearMeAdapter(context, stopsList);
            nearMeView.setAdapter(adapter);
            adapter.notifyItemRangeInserted(0,adapter.getItemCount()-1);
        }
        else if(adapter!=null) {
            //adapter.removeAllItems();
            adapter.addAllItems(stopsList);
        }
        swipeLayout.setRefreshing(false);
        emptySwipeLayout.setRefreshing(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        handler.removeCallbacks(updateTask);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if(mGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            startParsing(mLastLocation);
        }
        handler.postDelayed(updateTask, updateInterval);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        mGoogleApiClient.disconnect();
        handler.removeCallbacks(updateTask);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Log.d(TAG, "is reported as visible");
            handler.removeCallbacks(updateTask);
            handler.postDelayed(updateTask, updateInterval);
        }
        else{
            Log.d(TAG, "is reported as NOT visible");
            if(updateTask != null && handler != null){
                handler.removeCallbacks(updateTask);
            }
        }
    }

    final Runnable updateTask=new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh items at a predetermined interval
            Log.d(TAG, "Runnable is running");
            if(mGoogleApiClient.isConnected()) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                startParsing(mLastLocation);
                handler.postDelayed(updateTask, updateInterval);
            }
        }
    };

    private class ParseLocationRequest extends AsyncTask<Void, Void, Void>
    {
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Re-initializes the arraylist to clear any previously stored information
            if(adapter!=null) {
                adapter.removeAllItems();
            }
            stopsList = new ArrayList<HashMap<String, String>>();
            if(adapter != null){
                adapter.removeAllItems();
            }
        }

        protected Void doInBackground(Void ... arg0) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(baseURL, params);

            try {
                if (jsonStr != null) {
                    // Get JSON Object from string in ServiceHandler response
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    stops = jsonObj.getJSONArray(TAG_STOPS);

                    // Parses through JSON array to populate HashMap / ArrayLists
                    for (int i = 0; i < stops.length(); i++) {
                        JSONObject c = stops.getJSONObject(i);

                        String stopID = c.getString(TAG_STOPID);
                        String stopName = c.getString(TAG_STOPNAME);
                        String distance = c.getString(TAG_DISTANCE);

                        HashMap<String, String> stop = new HashMap<String, String>();

                        stop.put(TAG_STOPID, stopID);
                        stop.put(TAG_STOPNAME, stopName);
                        stop.put(TAG_DISTANCE, distance);

                        stopsList.add(stop);
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
            lastRefreshTime = System.currentTimeMillis();
            refreshAdapter();

            if(stops == null) {
                swipeLayout.setVisibility(View.GONE);
                emptySwipeLayout.setVisibility(View.VISIBLE);
                nothingHere.setText("Network error :c");
            }
            swipeLayout.setEnabled(false);
            emptySwipeLayout.setEnabled(false);
        }
    }
}