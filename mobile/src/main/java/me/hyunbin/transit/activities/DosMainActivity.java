package me.hyunbin.transit.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
  private static final float DEFAULT_ZOOM = 16.5f;

  private GoogleMap mMap;

  private LocationHelper mLocationHelper;
  private PermissionsHelper mPermissionsHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dos_main);

    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
    mapFragment.getMapAsync(this);

    LocationHelper.Listener locationListener = new LocationHelper.Listener() {
      @Override
      public void onLocationChanged(Location location) {
        // TODO: do something here with the new location update.
        Log.e(TAG, "New location: " + location.getLatitude() + ", " + location.getLongitude());

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
        // TODO: do something here to show rationale to the user before requesting permission again.
      }
    };

    mPermissionsHelper = new PermissionsHelper(this);
    mPermissionsHelper.setListener(permissionsListener);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mPermissionsHelper.checkForLocationPermission();
    mMap.setMyLocationEnabled(true);
    mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
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
