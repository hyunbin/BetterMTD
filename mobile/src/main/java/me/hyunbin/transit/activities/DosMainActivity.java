package me.hyunbin.transit.activities;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import me.hyunbin.transit.R;
import me.hyunbin.transit.helpers.LocationHelper;
import me.hyunbin.transit.helpers.PermissionsHelper;

/**
 * The new main activity screen consolidates map, search bar, and favorites screens into one.
 */

public class DosMainActivity extends AppCompatActivity implements OnMapReadyCallback {

  private static final String TAG = DosMainActivity.class.getSimpleName();

  private static final double DEFAULT_LATITUDE = 40.1020;
  private static final double DEFAULT_LONGITUDE = -88.2272;
  private static final float DEFAULT_ZOOM = 16.5f;

  private View mFrame;
  private GoogleMap mMap;

  private LocationHelper mLocationHelper;
  private PermissionsHelper mPermissionsHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dos_main);

    mFrame = findViewById(R.id.frame_layout);

    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
    mapFragment.getMapAsync(this);

    LocationHelper.Listener locationListener = new LocationHelper.Listener() {
      @Override
      public void onLocationChanged(Location location) {
        if (mMap != null) {
          mMap.moveCamera(CameraUpdateFactory.newLatLng(
              new LatLng(
                  location.getLatitude(),
                  location.getLongitude())));
        }
      }
    };

    mLocationHelper = new LocationHelper(this);
    mLocationHelper.setListener(locationListener);

    PermissionsHelper.Listener permissionsListener = new PermissionsHelper.Listener() {
      @Override
      public void onShowRationale() {
        String message = "The location permission is disabled.";
        Snackbar.make(mFrame, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("Enable", new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                ActivityCompat.requestPermissions(
                    DosMainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PermissionsHelper.PERMISSIONS_REQUEST_LOC);
              }
            })
            .show();
      }
    };

    mPermissionsHelper = new PermissionsHelper(this);
    mPermissionsHelper.setListener(permissionsListener);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

    if (!mPermissionsHelper.checkForLocationPermission()) {
      // If the location permission is disabled, the map is set to the heart of campus.
      mMap.moveCamera(CameraUpdateFactory.newLatLng(
          new LatLng(
              DEFAULT_LATITUDE,
              DEFAULT_LONGITUDE)));
      return;
    }

    mMap.setMyLocationEnabled(true);

    // TODO: We should set padding here if map is obstructed by other views.
    // mMap.setPadding();
  }

  @Override
  public void onStart() {
    super.onStart();
    mLocationHelper.connect();
  }

  @Override
  public void onStop() {
    super.onStop();
    mLocationHelper.disconnect();
  }
}
