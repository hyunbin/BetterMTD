package me.hyunbin.transit.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import me.hyunbin.transit.DetailItemDecoration;
import me.hyunbin.transit.R;
import me.hyunbin.transit.RestClient;
import me.hyunbin.transit.adapters.RouteAdapter;
import me.hyunbin.transit.models.StopTime;
import me.hyunbin.transit.models.StopTimesByTripResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hyunbin on 4/19/15.
 */
public class RouteActivity extends AppCompatActivity {

    private static final String TAG = RouteActivity.class.getSimpleName();

    private String mTripIdString;
    private String mHeadSignString;
    private String mCurrentStopIdString;
    private String mRouteColorString;
    private String mRouteTextColorString;

    private RecyclerView mRecyclerView;
    private RouteAdapter mAdapter;

    private RestClient mRestClient;
    private Callback<StopTimesByTripResponse> mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_route);

        mTripIdString = intent.getStringExtra("trip_id");
        mHeadSignString = intent.getStringExtra("headsign");
        mCurrentStopIdString = intent.getStringExtra("current_stop");
        mRouteColorString = intent.getStringExtra("route_color");
        mRouteTextColorString = intent.getStringExtra("text_color");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView2);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // Sets and styles the toolbar to enable hierarchy button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mHeadSignString);

        try{
            // TODO this causes a NumberFormatException, gather JSON data to find out why it's crashing
            toolbar.setBackgroundColor(Color.parseColor("#" + mRouteColorString) - 0x48000000);
        } catch(NumberFormatException e){
            // Logs exception in Crashlytics (cannot reproduce on @Hyunbin end)
            Crashlytics.setString("trip id", mTripIdString);
            Crashlytics.setString("headsign", mHeadSignString);
            Crashlytics.setString("current stop", mCurrentStopIdString);
            Crashlytics.setString("route color", mRouteColorString);
            Crashlytics.setString("text color", mRouteTextColorString);
            Crashlytics.logException(e);
        }

        toolbar.setTitleTextColor(-1);

        if(android.os.Build.VERSION.SDK_INT >= 21) {
            /* Set tinted status bar color */
            float[] hsv = new float[3];
            int mColor = Color.parseColor("#" + mRouteColorString) - 0x48000000;
            Color.colorToHSV(mColor, hsv);
            hsv[2] = (float) (hsv[2] - 0.1);
            getWindow().setStatusBarColor(Color.HSVToColor(hsv));

            /* TODO Set edge effect*/
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        // Sets item decoration to show the train-like line
        mRecyclerView.addItemDecoration(new DetailItemDecoration(this,
                Color.parseColor("#" + mRouteColorString) - 0x48000000));
    }

    @Override
    public void onStart(){
        super.onStart();

        mRestClient = new RestClient();
        mCallback = new Callback<StopTimesByTripResponse>() {
            @Override
            public void success(StopTimesByTripResponse stopTimesByTripResponse, Response response) {
                Log.d(TAG, "Retrofit success!");
                List<StopTime> stopTimes = stopTimesByTripResponse.getStopTimes();
                refreshAdapter(stopTimes);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Retrofit Error: " + error.toString());
            }
        };
        sendDataRequest();
    }

    // Helper function to call Retrofit.
    private void sendDataRequest(){
        mRestClient.getStopTimesByTrip(mTripIdString, mCallback);
    }

    private void refreshAdapter(List<StopTime> data) {
        // Either sets an adapter if none has been initialized, or swaps existing adapter.
        if(mAdapter == null) {
            setViewTreeObserver(data); // Gets ready to scroll down to current bus stop when loaded
            mAdapter = new RouteAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyItemRangeInserted(0, data.size() - 1);
        }
        else if(mAdapter != null) {
            mAdapter = new RouteAdapter(data);
            mRecyclerView.swapAdapter(mAdapter, false);
        }
    }

    // Sets a view observer to listen when RecyclerView finishes drawing
    private void setViewTreeObserver(final List<StopTime> data){
        final ViewTreeObserver vto = mRecyclerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                findAndScrollTo(data);
                if (vto.isAlive()) {
                    // Unregister the listener to only call scrollToPosition once
                    vto.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    // Called by the ViewTreeObserver to find item and scroll by approporiate amount
    private void findAndScrollTo(List<StopTime> data){
        String name;
        for(int i = 0; i < data.size(); i++) {
            name = data.get(i).getStopPoint().getStopId();
            if (name.split(":", 2)[0].equals(mCurrentStopIdString)){
                final int itemHeight = mRecyclerView.getChildAt(0).getHeight();
                mRecyclerView.smoothScrollBy(0, itemHeight * i);
                break;
            }
        }
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

}
