package me.hyunbin.transit.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import me.hyunbin.transit.R;

/**
 * Created by Hyunbin on 6/8/15.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = MapActivity.class.getSimpleName();
    private MapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Finds and sets map fragment
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setupAndStyleToolbar();
    }

    private void setupAndStyleToolbar(){
        Intent intent = getIntent();

        // Sets and styles the toolbar to enable hierarchy button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra("headsign"));
        toolbar.setBackgroundColor(Color.parseColor("#" + intent.getStringExtra("route_color")) - 0x48000000);
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        if(android.os.Build.VERSION.SDK_INT >= 21) {
            /* Set tinted status bar color */
            float[] hsv = new float[3];
            int mColor = Color.parseColor("#" + intent.getStringExtra("route_color")) - 0x48000000;
            Color.colorToHSV(mColor, hsv);
            hsv[2] = (float) (hsv[2] - 0.1);
            getWindow().setStatusBarColor(Color.HSVToColor(hsv));
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }

}