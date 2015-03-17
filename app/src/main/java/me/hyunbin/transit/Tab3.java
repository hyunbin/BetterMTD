package me.hyunbin.transit;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import me.hyunbin.transit.R;

/**
 * Created by Hyunbin on 3/9/15.
 */

public class Tab3 extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG_STOPS = "stops";
    private static final String TAG_STOPID = "stop_id";
    private static final String TAG_STOPNAME = "stop_name";
    private static final String TAG_DISTANCE = "distance";

    RecyclerView nearmeView;
    GoogleApiClient mGoogleApiClient;
    Context context;
    Location mLastLocation;
    TextView textView;
    NearMeAdapter adapter;

    ArrayList<HashMap<String, String>> stopsList;

    private static String baseURL = "https://developer.cumtd.com/api/v2.2/json/GetStopsByLatLon";
    public List<NameValuePair> params;
    JSONArray stops = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_3,container,false);

        context = getActivity().getApplicationContext();
        nearmeView = (RecyclerView) v.findViewById(R.id.nearmeView);
        nearmeView.setItemAnimator(new FadeInAnimator());
        nearmeView.getItemAnimator().setAddDuration(200);
        nearmeView.getItemAnimator().setRemoveDuration(100);

        textView = (TextView) v.findViewById(R.id.textView);
        textView.setVisibility(View.GONE);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        nearmeView.setLayoutManager(layoutManager);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        // Populates parameters with key information
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key","***REMOVED***"));

        return v;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            // Populates parameters with lat/lon information
            params.add(new BasicNameValuePair("lat", String.valueOf(mLastLocation.getLatitude())));
            params.add(new BasicNameValuePair("lon", String.valueOf(mLastLocation.getLongitude())));
            new ParseLocationRequest().execute();
        }
        else {
            textView.setVisibility(View.VISIBLE);
            textView.setText("Failed to get location :c");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        textView.setVisibility(View.VISIBLE);
        textView.setText("Failed to get location :c");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //probably orientation change
            stopsList = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("list");
        } else {
            if (stopsList != null) {
                //returning from backstack, data is fine, do nothing
            } else {
                //newly created, compute data

            }
        }
    }

    public void refreshAdapter(){
        // Either sets an adapter if none has been initialized, or makes appropriate calls to
        // enable animations in the RecyclerView.

        if(adapter==null)
        {
            adapter = new NearMeAdapter(context, stopsList);
            nearmeView.setAdapter(adapter);
            adapter.notifyItemRangeInserted(0,adapter.getItemCount()-1);
        }
        else if(adapter!=null) {
            adapter.addAllItems(stopsList);
        }
    }

    private class ParseLocationRequest extends AsyncTask<Void, Void, Void>
    {
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Re-initializes the arraylist to clear any previously stored information
            stopsList = new ArrayList<HashMap<String, String>>();
        }

        protected Void doInBackground(Void ... arg0) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(baseURL, params);

            //Log.d("Response: ", "> " + jsonStr);

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
            refreshAdapter();
            if(stops == null) {
                textView.setText("Network error :c");
                textView.setVisibility(View.VISIBLE);
            }
        }
    }
}