package me.hyunbin.transit;

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

import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import me.hyunbin.transit.adapters.StopsAdapter;
import me.hyunbin.transit.models.Departure;
import me.hyunbin.transit.models.DeparturesByStopResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class StopActivity extends AppCompatActivity {

    private static final String TAG = StopActivity.class.getSimpleName();
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
    private StopsAdapter mAdapter;
    private TextView mEmptyTextView;

    private Handler handler;
    private int mUpdateInterval;
    private long mLastRefreshTime;

    private String mStopString;
    private String mStopNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

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
    }

    @Override
    public void onStart(){
        super.onStart();
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
                // Relieves animation
                onItemsLoadComplete();

                // Resets the refresh time once new data is populated
                mLastRefreshTime = System.currentTimeMillis();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Retrofit Error: " + error.toString());
                onErrorStatusChanged(ERROR_NETWORK);
            }
        };
        mLastRefreshTime = System.currentTimeMillis();
        sendDataRequest();

        // Sets a handler to refresh the RecyclerView periodically
        mUpdateInterval = 80000;
        handler = new Handler();
        handler.postDelayed(updateTask, mUpdateInterval);
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
            mAdapter = new StopsAdapter(data, mStopString);
            mRecyclerView.setAdapter(mAdapter);
        }
        else if(mAdapter != null) {
            mAdapter = new StopsAdapter(data, mStopString);
            mRecyclerView.swapAdapter(mAdapter, false);
        }
    }

    final Runnable updateTask=new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh mData at a predetermined interval
            onDataUpdateRequested();
            handler.postDelayed(updateTask, mUpdateInterval);
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
    public void onRestart(){
        // Starts refreshing automatically again when activity is resumed
        super.onRestart();
        handler = new Handler();
        handler.postDelayed(updateTask, 1000);
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
