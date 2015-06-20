package me.hyunbin.transit;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

    private static final String TAG = Tab3.class.getSimpleName();

    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private boolean mResolvingError = false;

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

    private Handler mHandler;
    private int mUpdateInterval;
    private long mLastRefreshTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Begin Google Play Services location service
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_3,container,false);

        mContext = getActivity().getApplicationContext();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.near_me_view);
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

        // Sets handler refresh parameter to refresh the RecyclerView periodically
        mUpdateInterval = 90000;
        mHandler = new Handler();

        mLastRefreshTime = System.currentTimeMillis() - 60000;
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    private void startParsing(Location location) {
        // Re-initalize parameters
        mParams = new ArrayList<NameValuePair>();
        mParams.add(new BasicNameValuePair("key", "***REMOVED***"));

        if(System.currentTimeMillis() - mLastRefreshTime > 50000){
            if (location != null) {
                mTextView.setVisibility(View.GONE);
                // Populates parameters with lat/lon information
                mParams.add(new BasicNameValuePair("lat", String.valueOf(location.getLatitude())));
                mParams.add(new BasicNameValuePair("lon", String.valueOf(location.getLongitude())));
                new ParseLocationRequest().execute();
            }
            else{
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("Failed to get location :c");
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startParsing(mLastLocation);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText("Failed to get location :c");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void refreshAdapter(){
        // Either sets an adapter if none has been initialized, or makes appropriate calls to
        // enable animations in the RecyclerView.
        if(mAdapter ==null)
        {
            mAdapter = new NearMeAdapter(mContext, mStopsList);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount()-1);
        }
        else if(mAdapter !=null) {
            mAdapter.addAllItems(mStopsList);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        mHandler.removeCallbacks(updateTask);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if(mGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            startParsing(mLastLocation);
        }
        mHandler.postDelayed(updateTask, mUpdateInterval);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        mGoogleApiClient.disconnect();
        mHandler.removeCallbacks(updateTask);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Log.d(TAG, "is reported as visible");
            mHandler.removeCallbacks(updateTask);
            mHandler.postDelayed(updateTask, mUpdateInterval);
        }
        else{
            Log.d(TAG, "is reported as NOT visible");
            if(updateTask != null && mHandler != null){
                mHandler.removeCallbacks(updateTask);
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
                mHandler.postDelayed(updateTask, mUpdateInterval);
            }
        }
    };

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
            mLastRefreshTime = System.currentTimeMillis();
            refreshAdapter();
            if(mStopsArray == null) {
                mTextView.setText("Network error :c");
                mTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}