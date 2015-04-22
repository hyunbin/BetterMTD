package me.hyunbin.transit;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hyunbin on 4/19/15.
 */
public class DetailActivity extends ActionBarActivity {

    private String baseURL = "https://developer.cumtd.com/api/v2.2/json/GetStopTimesByTrip";
    public List<NameValuePair> params;

    private final String TAG_STOPTIMES = "stop_times";
    private final String TAG_POINT = "stop_point";
    private final String TAG_STOPID = "stop_id";
    private final String TAG_STOPNAME = "stop_name";
    private final String TAG_ARRIVALTIME = "arrival_time";

    JSONArray departures = null;

    private Context context;
    private String tripId;
    private String headSign;

    private ArrayList stopsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Intent intent = getIntent();
        setContentView(R.layout.activity_detail);

        tripId = intent.getStringExtra("trip_id");
        headSign = intent.getStringExtra("headsign");

        // Sets and styles the toolbar to enable hierarchy button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(headSign);
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        // Sets up base URL and populates parameters to load
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key","***REMOVED***"));
        params.add(new BasicNameValuePair("trip_id", tripId));

        new HTTPStopRequest().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HTTPStopRequest extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
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
                    departures = jsonObj.getJSONArray(TAG_STOPTIMES);

                    // Parses through JSON array to populate HashMap / ArrayLists
                    for (int i = 0; i < departures.length(); i++) {
                        JSONObject c = departures.getJSONObject(i);
                        JSONObject t = c.getJSONObject(TAG_POINT);
                        String stopId = t.getString(TAG_STOPID);
                        String stopName = t.getString(TAG_STOPNAME);
                        String arrivalTime = t.getString(TAG_ARRIVALTIME);

                        HashMap<String, String> departure = new HashMap<String, String>();
                        departure.put(TAG_STOPID, stopId);
                        departure.put(TAG_STOPNAME, stopName);
                        departure.put(TAG_ARRIVALTIME, arrivalTime);
                        stopsList.add(departure);
                    }
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
