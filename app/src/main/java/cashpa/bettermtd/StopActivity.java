package cashpa.bettermtd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class StopActivity extends ActionBarActivity {

    private static String baseURL = "https://developer.cumtd.com/api/v2.2/json/GetDeparturesByStop";
    public List<NameValuePair> params;

    // JSON Node names
    private static final String TAG_TIME = "time";
    private static final String TAG_DEPARTURES = "departures";
    private static final String TAG_STOPID = "stop_id";
    private static final String TAG_HEADSIGN = "headsign";
    private static final String TAG_ROUTE = "route";
    private static final String TAG_ROUTECOLOR = "route_color";
    private static final String TAG_ROUTEID = "route_id";
    private static final String TAG_ROUTELONGNAME = "route_long_name";
    private static final String TAG_ROUTESHORTNAME = "route_short_name";
    private static final String TAG_ROUTETEXTCOLOR = "route_text_color";
    private static final String TAG_VEHICLEID = "vehicle_id";
    private static final String TAG_ISISTOP = "is_istop";
    private static final String TAG_EXPECTEDMINS = "expected_mins";
    private static final String TAG_TRIP = "trip";
    private static final String TAG_TRIPHEADSIGN = "trip_headsign";
    private static final String TAG_DESTINATION = "destination";

    JSONArray departures = null;

    ArrayList<HashMap<String, String>> departureList;
    public RecyclerView recyclerView;
    public Context context;
    SharedPreferences favorites;
    SharedPreferences recents;
    SharedPreferences.Editor recentsEdit;
    SharedPreferences.Editor edit;

    public SwipeRefreshLayout swipeLayout;
    public SwipeRefreshLayout emptySwipeLayout;
    public RecyclerViewAdapter adapter;
    public TextView nothingHere;

    private Handler handler;
    private int updateInterval;
    long lastRefreshTime;

    String stop;
    String stopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_stop);

        stop = intent.getStringExtra(MainActivity.ARG_STOPID);
        stopName = intent.getStringExtra(MainActivity.ARG_STOPNAME);

        context = getApplicationContext();

        // Grabs preferences for favorite stops and recents, adds this stop to recents
        favorites = context.getSharedPreferences("favorites",0);
        recents = context.getSharedPreferences("recents",0);
        int max = recents.getAll().size();

        // This set is the information pertaining to this stop
        Set recentStop = new LinkedHashSet();
        recentStop.add(stop);
        recentStop.add(stopName);

        recentsEdit = recents.edit();
        recentsEdit.putStringSet(Integer.toString(max), recentStop);
        recentsEdit.commit();

        // Sets and styles the toolbar to enable hierarchy button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(stopName);
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        // Sets a handler to refresh the RecyclerView periodically
        updateInterval = 60000;
        handler = new Handler();
        handler.postDelayed(updateTask,updateInterval);

        // Sets animator to RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new FadeInAnimator());
        recyclerView.getItemAnimator().setAddDuration(200);
        recyclerView.getItemAnimator().setRemoveDuration(100);

        // Sets SwipeRefreshLayout to enable the swipe-to-refresh gesture
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        // The following code looks ugly and unnecessary, but this is to circumvent a
        // SwipeRefreshLayout bug that doesn't show refresh when refresh is called.
        final boolean refreshing = true;
        swipeLayout.post(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(refreshing);
            }
        });

        emptySwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);
        emptySwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        emptySwipeLayout.setVisibility(View.GONE);

        nothingHere = (TextView) findViewById(R.id.textView);
        nothingHere.setText("There are no buses scheduled :c");

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // Sets up base URL and populates parameters to load
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key","***REMOVED***"));
        params.add(new BasicNameValuePair("stop_id", stop));

        lastRefreshTime = System.currentTimeMillis();
        new HTTPStopRequest().execute();
    }

    @Override
    public void onDestroy() {
        // Stops refreshing automatically again when activity is destroyed
        super.onDestroy();
        handler.removeCallbacks(updateTask);
    }

    @Override
    public void onStop(){
        // Stops refreshing automatically again when activity is stopped
        super.onStop();
        handler.removeCallbacks(updateTask);
    }

    @Override
    public void onRestart(){
        // Starts refreshing automatically again when activity is resumed
        super.onRestart();
        handler = new Handler();
        handler.postDelayed(updateTask,1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stop, menu);
        // Updates favorites icon based on state (either filled or outline)
        if(favorites.getString(stop, "nope") == "nope"){
            menu.getItem(0).setIcon(R.drawable.ic_notfavorite);
        }
        else{
            menu.getItem(0).setIcon(R.drawable.ic_favorite);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            // Pops a toast as pacifier, then refreshes.
            int duration = Toast.LENGTH_SHORT;

            // Updates favorites based on whether the stop is already stored in favorites
            edit = favorites.edit();
            if(favorites.getString(stop, "nope") == "nope"){
                edit.putString(stop, stopName);
                edit.commit();
                CharSequence text = "Stop added to favorites";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else{
                edit.remove(stop);
                edit.commit();
                CharSequence text = "Stop removed from favorites";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void refreshAdapter() {
        // Either sets an adapter if none has been initialized, or makes appropriate calls to
        // enable animations in the RecyclerView.

        if(adapter==null)
        {
            adapter = new RecyclerViewAdapter(context, departureList);
            recyclerView.setAdapter(adapter);
            adapter.notifyItemRangeInserted(0,adapter.getItemCount()-1);
            /*
            for(int n = 0 ; n < adapter.getItemCount(); n++){
                adapter.notifyItemInserted(n);
            }
            */
        }
        else if(adapter!=null) {
            adapter.addAllItems(departureList);
            /*
                for(int i = 0 ; i < departureList.size(); i++) {
                    HashMap<String, String> newItem = departureList.get(i);
                    adapter.addOneItem(newItem);
                }
            */
        }
    }

    void refreshItems() {
        if(System.currentTimeMillis() - lastRefreshTime < 20000){
            // Pops a toast as pacifier if cached data will be used
            CharSequence text = "Your schedule is up-to-date";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            onItemsLoadComplete();
        }
        else {
            // The following code looks ugly and unnecessary, but this is to circumvent a
            // SwipeRefreshLayout bug that doesn't show refresh when refresh is called.
            final boolean refreshing = true;
            swipeLayout.post(new Runnable() {
                @Override public void run() {
                    swipeLayout.setRefreshing(refreshing);
                }
            });
            new HTTPStopRequest().execute();
        }

    }

    public void setNothingHere(boolean b){
        if(b==true){
            // Disables recyclerView and shows the nothing here text
            swipeLayout.setVisibility(View.GONE);
            emptySwipeLayout.setVisibility(View.VISIBLE);
        }
        else{
            // Disables nothing here text and shows recyclerView
            emptySwipeLayout.setVisibility(View.GONE);
            swipeLayout.setVisibility(View.VISIBLE);
        }
    }


    void onItemsLoadComplete() {
        // Stop refresh animation
        swipeLayout.setRefreshing(false);
        emptySwipeLayout.setRefreshing(false);
    }

    final Runnable updateTask=new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh items at a predetermined interval
            refreshItems();
            handler.postDelayed(updateTask, updateInterval);
        }
    };

    private class HTTPStopRequest extends AsyncTask<Void, Void, Void>
    {
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Removes all items to display the proper animations
            if(adapter!=null){
                adapter.removeAllItems();
            }
            // Re-initializes the arraylist to clear any previously stored information
            departureList = new ArrayList<HashMap<String, String>>();
        }

        protected Void doInBackground(Void ... arg0) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(baseURL, params);

            Log.d("Response: ", "> " + jsonStr);

            try {
                if (jsonStr != null) {

                    // Get JSON Object from string in ServiceHandler response
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    departures = jsonObj.getJSONArray(TAG_DEPARTURES);

                    // Parses through JSON array to populate HashMap / ArrayLists
                    for (int i = 0; i < departures.length(); i++) {
                        JSONObject c = departures.getJSONObject(i);

                        String tripDest;
                        try {
                            JSONObject t = c.getJSONObject(TAG_TRIP);
                            tripDest = t.getString(TAG_TRIPHEADSIGN);
                        }
                        catch (JSONException e)
                        {
                            tripDest = "";
                        }

                        JSONObject r = c.getJSONObject(TAG_ROUTE);
                        String routeColor = r.getString(TAG_ROUTECOLOR);
                        String routeTextColor = r.getString(TAG_ROUTETEXTCOLOR);
                        String stopID = c.getString(TAG_STOPID);
                        String headSign = c.getString(TAG_HEADSIGN);

                        String vehicleID = c.getString(TAG_VEHICLEID);
                        String expectedMins = c.getString(TAG_EXPECTEDMINS);

                        HashMap<String, String> departure = new HashMap<String, String>();

                        departure.put(TAG_STOPID, stopID);
                        departure.put(TAG_HEADSIGN, headSign);
                        departure.put(TAG_VEHICLEID, vehicleID);
                        departure.put(TAG_EXPECTEDMINS, expectedMins);
                        departure.put(TAG_ROUTECOLOR, routeColor);
                        departure.put(TAG_TRIPHEADSIGN, tripDest);
                        departure.put(TAG_ROUTETEXTCOLOR, routeTextColor);

                        departureList.add(departure);
                        /*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(departures.length() == 0 ){
                                    setNothingHere(true);
                                }
                                else{
                                    setNothingHere(false);
                                }
                            }
                        });
                        */
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(departures == null) {
                nothingHere.setText("Network error. :c");
                setNothingHere(true);
            }
            else if(departures.length() == 0 ){
                setNothingHere(true);
            }
            else{
                setNothingHere(false);
            }

            // Relieves animation
            onItemsLoadComplete();

            // Resets the refresh time once new data is populated
            lastRefreshTime = System.currentTimeMillis();
            refreshAdapter();

        }
    }

}
