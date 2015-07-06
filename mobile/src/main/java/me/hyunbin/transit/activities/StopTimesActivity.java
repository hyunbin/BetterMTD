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

import java.util.List;

import me.hyunbin.transit.DetailItemDecoration;
import me.hyunbin.transit.R;
import me.hyunbin.transit.RestClient;
import me.hyunbin.transit.adapters.StopTimesAdapter;
import me.hyunbin.transit.models.StopTime;
import me.hyunbin.transit.models.StopTimesByTripResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hyunbin on 4/19/15.
 */
public class StopTimesActivity extends AppCompatActivity {

    private static final String TAG = StopTimesActivity.class.getSimpleName();

    private String mTripIdString;
    private String mHeadSignString;
    private String mCurrentStopIdString;
    private String mRouteColorString;
    private String mRouteTextColorString;

    private RecyclerView mRecyclerView;
    private StopTimesAdapter mAdapter;

    private RestClient mRestClient;
    private Callback<StopTimesByTripResponse> mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_stop_times);

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
        toolbar.setBackgroundColor(Color.parseColor("#" + mRouteColorString) - 0x48000000);

        if(android.os.Build.VERSION.SDK_INT >= 21) {
            /* Set tinted status bar color */
            float[] hsv = new float[3];
            int mColor = Color.parseColor("#" + mRouteColorString) - 0x48000000;
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
            mAdapter = new StopTimesAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
        }
        else if(mAdapter != null) {
            mAdapter = new StopTimesAdapter(data);
            mRecyclerView.swapAdapter(mAdapter, false);
        }
        findAndScrollTo(data);
    }

    private void findAndScrollTo(List<StopTime> data){
        String name;
        for(int i = 0; i < data.size(); i++) {
            name = data.get(i).getStopPoint().getStopName();
            if (name.split(":", 2)[0].equals(mCurrentStopIdString)){
                mRecyclerView.scrollToPosition(i);
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
