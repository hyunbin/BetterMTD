package me.hyunbin.transit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import me.hyunbin.transit.R;
import me.hyunbin.transit.RestClient;
import me.hyunbin.transit.adapters.DeparturesAdapter;
import me.hyunbin.transit.models.Departure;
import me.hyunbin.transit.models.DeparturesByStopResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DeparturesActivity extends AppCompatActivity {

    private static final String TAG = DeparturesActivity.class.getSimpleName();
    private static int NO_ERROR = 0;
    private static int ERROR_NETWORK = 1;
    private static int ERROR_EMPTY_RESPONSE = 2;

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mSharedPrefsEditor;
    private RestClient mRestClient;
    private Callback<DeparturesByStopResponse> mCallback;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout mEmptySwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private DeparturesAdapter mAdapter;
    private TextView mEmptyTextView;

    private Handler handler;
    private int mUpdateInterval;
    private long mLastRefreshTime;

    private String mStopString;
    private String mStopNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departures);

        Intent intent = getIntent();
        mStopString = intent.getStringExtra(MainActivity.ARG_STOPID);
        mStopNameString = intent.getStringExtra(MainActivity.ARG_STOPNAME);

        // Grabs preferences for favorite stops and recents, adds this stop to recents
        mSharedPrefs = this.getSharedPreferences("favorites", 0);

        // Sets and styles the toolbar to enable hierarchy button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mStopNameString);
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        // Sets animator to RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new FadeInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(400);
        mRecyclerView.getItemAnimator().setRemoveDuration(400);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // Sets SwipeRefreshLayout to enable the swipe-to-refresh gesture
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onDataUpdateRequested();
            }
        });

        // The following code looks ugly and unnecessary, but this is to circumvent a
        // SwipeRefreshLayout bug that doesn't show refresh when refresh is called.
        final boolean refreshing = true;
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(refreshing);
            }
        });

        mEmptySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);
        mEmptySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onDataUpdateRequested();
            }
        });
        mEmptySwipeRefreshLayout.setVisibility(View.GONE);

        mEmptyTextView = (TextView) findViewById(R.id.text_view);

        mRestClient = new RestClient();
        mCallback = new Callback<DeparturesByStopResponse>() {
            @Override
            public void success(DeparturesByStopResponse departuresByStopResponse, Response response) {
                Log.d(TAG, "Retrofit success!");
                List<Departure> departures = departuresByStopResponse.getDepartures();
                if(departures.size() == 0){
                    onErrorStatusChanged(ERROR_EMPTY_RESPONSE);
                }
                else{
                    onErrorStatusChanged(NO_ERROR);
                    refreshAdapter(departures);
                }
                // Resets the refresh time once new data is populated
                mLastRefreshTime = System.currentTimeMillis();

                // Relieves animation
                onItemsLoadComplete();

                // Set Crashlytics key to a certain string to see raw JSON at time of crash
                Crashlytics.setString("departure json", response.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Retrofit Error: " + error.toString());
                onErrorStatusChanged(ERROR_NETWORK);
                // Relieves animation
                onItemsLoadComplete();
            }
        };
        mLastRefreshTime = System.currentTimeMillis();
        mUpdateInterval = 70000;

        sendDataRequest();
        // Sets a handler to refresh the RecyclerView periodically
        handler = new Handler();
        handler.postDelayed(updateTask, mUpdateInterval);
    }

    @Override
    public void onRestart(){
        // Starts refreshing automatically again when activity is resumed
        super.onRestart();
        Log.e(TAG, "onRestart invoked");
        onDataUpdateRequested();
    }

    // Helper function to facilitate show/hide of error messages and data view.
    private void onErrorStatusChanged(int mode){
        if(mode == ERROR_NETWORK){
            // Disables recyclerView and shows error text
            mEmptyTextView.setText("Network error :c\nData provided by CUMTD");
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mEmptySwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
        else if(mode == ERROR_EMPTY_RESPONSE){
            // Disables recyclerView and shows error text
            mEmptyTextView.setText("There are no buses scheduled :c\nData provided by CUMTD");
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mEmptySwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
        else{
            // Disables nothing here text and shows recyclerView
            mEmptySwipeRefreshLayout.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void refreshAdapter(List<Departure> data) {
        // Either sets an adapter if none has been initialized, or swaps existing adapter.
        if(mAdapter == null)
        {
            mAdapter = new DeparturesAdapter(data, mStopString, mStopNameString);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyItemRangeInserted(0, data.size() - 1);
        }
        else if(mAdapter != null) {
            mAdapter = new DeparturesAdapter(data, mStopString, mStopNameString);
            mRecyclerView.swapAdapter(mAdapter, false);
        }
    }

    private final Runnable updateTask=new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh mData at a predetermined interval
            onDataUpdateRequested();
        }
    };

    private void onDataUpdateRequested() {
        if(System.currentTimeMillis() - mLastRefreshTime < 20000){
            showSnack("Your schedule is up-to-date");
            onItemsLoadComplete();
        }
        else {
            // The following code looks ugly and unnecessary, but this is to circumvent a
            // SwipeRefreshLayout bug that doesn't show refresh when refresh is called.
            final boolean refreshing = true;
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(refreshing);
                }
            });
            sendDataRequest();
            // Reset all active callbacks
            handler.removeCallbacks(updateTask);
            handler.postDelayed(updateTask, mUpdateInterval);
        }
    }

    // Helper function to call Retrofit.
    private void sendDataRequest(){
        mRestClient.getDeparturesByStop(mStopString, mCallback);
    }

    private void onItemsLoadComplete() {
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
        mEmptySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStop(){
        // Stops refreshing automatically again when activity is stopped
        super.onStop();
        handler.removeCallbacks(updateTask);
    }

    @Override
    public void onDestroy() {
        // Stops refreshing automatically again when activity is destroyed
        super.onDestroy();
        handler.removeCallbacks(updateTask);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds mData to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stop, menu);
        // Updates mSharedPrefs icon based on state (either filled or outline)
        if(mSharedPrefs.getString(mStopString, "nope") == "nope"){
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

            // Updates mSharedPrefs based on whether the stop is already stored in mSharedPrefs
            mSharedPrefsEditor = mSharedPrefs.edit();
            if(mSharedPrefs.getString(mStopString, "nope") == "nope"){
                mSharedPrefsEditor.putString(mStopString, mStopNameString);
                mSharedPrefsEditor.commit();
                showSnack("Stop added to favorites");
            }
            else{
                mSharedPrefsEditor.remove(mStopString);
                mSharedPrefsEditor.commit();
                showSnack("Stop removed from favorites");
            }
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showSnack(String message){
        // Dismisses the Snackbar being shown, if any, and displays the new one
        if(mSwipeRefreshLayout.getVisibility() == View.VISIBLE){
            Snackbar.make(mSwipeRefreshLayout, message, Snackbar.LENGTH_SHORT).show();
        }
        else{
            Snackbar.make(mEmptySwipeRefreshLayout, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
