package me.hyunbin.transit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EdgeEffect;

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
public class DetailActivity extends AppCompatActivity {

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
    private String currentStopName;
    private String mRouteColor;
    private String mRouteTextColor;
    private ArrayList stopsList;

    public RecyclerView recyclerView;
    public DetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Intent intent = getIntent();
        setContentView(R.layout.activity_detail);

        tripId = intent.getStringExtra("trip_id");
        headSign = intent.getStringExtra("headsign");
        currentStopName = intent.getStringExtra("current_stop");
        mRouteColor = intent.getStringExtra("route_color");
        mRouteTextColor = intent.getStringExtra("text_color");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // Sets and styles the toolbar to enable hierarchy button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(headSign);
        toolbar.setBackgroundColor(Color.parseColor("#" + mRouteColor) - 0x48000000);

        if(android.os.Build.VERSION.SDK_INT >= 21) {
            /* Set tinted status bar color */
            float[] hsv = new float[3];
            int mColor = Color.parseColor("#" + mRouteColor) - 0x48000000;
            Color.colorToHSV(mColor, hsv);
            hsv[2] = (float) (hsv[2] - 0.1);
            getWindow().setStatusBarColor(Color.HSVToColor(hsv));

            /* TODO Set edge effect*/
        }
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        // Sets item decoration to show the train-like line
        recyclerView.addItemDecoration(new DetailItemDecoration(context,
                Color.parseColor("#" + mRouteColor) - 0x48000000));

        // Sets up base URL and populates parameters to load
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key","107516afa39d442fb728498a32e43e35"));
        params.add(new BasicNameValuePair("trip_id", tripId));

        stopsList = new ArrayList<HashMap<String, String>>();
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

    void findAndScrollTo(){
        String name;
        for(int i = 0; i < stopsList.size(); i++) {
            name = ((HashMap<String, String>) stopsList.get(i)).get(TAG_STOPID);
            if (name.equals(currentStopName)){
                recyclerView.scrollToPosition(i);
                break;
            }
        }
    }

    void refreshAdapter() {
        // Either sets an adapter if none has been initialized, or makes appropriate calls to
        // enable animations in the RecyclerView.

        if(adapter==null)
        {
            adapter = new DetailAdapter(context, stopsList);
            recyclerView.setAdapter(adapter);
            adapter.notifyItemRangeInserted(0,adapter.getItemCount()-1);
            findAndScrollTo();
        }
        else if(adapter!=null) {
            adapter = new DetailAdapter(context, stopsList);
            recyclerView.swapAdapter(adapter, false);
            findAndScrollTo();
        }
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

            try {
                if (jsonStr != null) {
                    // Get JSON Object from string in ServiceHandler response
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    departures = jsonObj.getJSONArray(TAG_STOPTIMES);

                    // Parses through JSON array to populate HashMap / ArrayLists
                    for (int i = 0; i < departures.length(); i++) {
                        JSONObject c = departures.getJSONObject(i);
                        JSONObject t = c.getJSONObject(TAG_POINT);
                        String stopId = t.getString(TAG_STOPID).split(":", 2)[0];;
                        String stopName = t.getString(TAG_STOPNAME).split("\\(", 2)[0];
                        String arrivalTime = c.getString(TAG_ARRIVALTIME);

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
            refreshAdapter();
        }
    }
}
