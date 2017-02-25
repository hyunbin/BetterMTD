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
import me.hyunbin.transit.fragments.DosFavoritesFragment;
import me.hyunbin.transit.fragments.DosNearMeFragment;
import me.hyunbin.transit.fragments.DosSearchFragment;
import me.hyunbin.transit.helpers.LayoutUtil;
import me.hyunbin.transit.helpers.LocationHelper;
import me.hyunbin.transit.helpers.MapHelper;
import me.hyunbin.transit.helpers.StopsByLatLonHelper;
import me.hyunbin.transit.helpers.PermissionsHelper;
import me.hyunbin.transit.models.StopsByLatLonResponse;

/**
 * The new main activity screen consolidates map, search bar, and favorites screens into one.
 */

public class DosMainActivity extends AppCompatActivity implements OnMapReadyCallback {
  private static final String TAG = DosMainActivity.class.getSimpleName();

  private static final double DEFAULT_LATITUDE = 40.1020;
  private static final double DEFAULT_LONGITUDE = -88.2272;
  private static final float DEFAULT_ZOOM = 16.5f;
  private static final float MAX_ZOOM = 18.0f;
  private static final float MIN_ZOOM = 16.0f;

  private DosFavoritesFragment mFavoritesFragment;
  private DosNearMeFragment mNearMeFragment;
  private DosSearchFragment mSearchFragment;
  private GoogleMap mMap;
  private View mFrame;

  private StopsByLatLonHelper mStopsByLatLonHelper;
  private LocationHelper mLocationHelper;
  private PermissionsHelper mPermissionsHelper;

  private boolean mCameraMoving = false;
  private int mBottomHeightPx = 0;
  private int mTopHeightPx = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dos_main);

    mFavoritesFragment = (DosFavoritesFragment) getFragmentManager()
        .findFragmentById(R.id.favorites_fragment);
    mNearMeFragment = (DosNearMeFragment) getFragmentManager()
        .findFragmentById(R.id.near_me_fragment);
    mSearchFragment = (DosSearchFragment) getFragmentManager()
        .findFragmentById(R.id.search_fragment);
    mFrame = findViewById(R.id.frame_layout);

    MapFragment mapFragment = (MapFragment) getFragmentManager()
        .findFragmentById(R.id.map_fragment);
    mapFragment.getMapAsync(this);

    mStopsByLatLonHelper = new StopsByLatLonHelper();
    mStopsByLatLonHelper.addListener(new StopsByLatLonHelper.Listener() {
      @Override
      public void onResponse(StopsByLatLonResponse response) {
        mNearMeFragment.onStopsByLatLonResponse(response);
        MapHelper.populateMapWithStopMarkers(mMap, response.getStops());
      }

      @Override
      public void onFailure(String errorMsg) {
        mNearMeFragment.onStopsByLatLonResponseFailure(errorMsg);
      }
    });

    LocationHelper.Listener locationListener = new LocationHelper.Listener() {
      @Override
      public void onLocationChanged(Location location) {
        if (mMap != null) {
          if (!mCameraMoving) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(
                    location.getLatitude(),
                    location.getLongitude())));
            mStopsByLatLonHelper.execute(location);
          }
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
  public void onBackPressed() {
    if (!mSearchFragment.onBackPressed()) {
      super.onBackPressed();
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    Log.e(TAG, "Map is ready!");

    mMap = googleMap;
    mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
    mMap.setMaxZoomPreference(MAX_ZOOM);
    mMap.setMinZoomPreference(MIN_ZOOM);

    mTopHeightPx = LayoutUtil.dpToPx(64);
    mBottomHeightPx = LayoutUtil.dpToPx(240);
    if (mNearMeFragment.getIsCollapsed()) {
      mBottomHeightPx -= LayoutUtil.dpToPx(80) + 2;
    }
    mMap.setPadding(0, mTopHeightPx, 0, mBottomHeightPx);

    if (!mPermissionsHelper.checkForLocationPermission(false)) {
      // If the location permission is disabled, the map is set to the heart of campus.
      mMap.moveCamera(CameraUpdateFactory.newLatLng(
          new LatLng(
              DEFAULT_LATITUDE,
              DEFAULT_LONGITUDE)));
    } else {
      mMap.setMyLocationEnabled(true);
    }

    mMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
      @Override
      public void onCameraMoveCanceled() {
        mCameraMoving = false;
      }
    });
    mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
      @Override
      public void onCameraMoveStarted(int i) {
        mCameraMoving = true;
      }
    });
    mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
      @Override
      public void onCameraMove() {
        LatLng location = mMap.getCameraPosition().target;
        mStopsByLatLonHelper.execute(location);
      }
    });
  }

  @Override
  public void onStart() {
    super.onStart();

    mNearMeFragment.setListener(new DosNearMeFragment.Listener() {
      @Override
      public void onViewCollapsed(boolean isCollapsed) {
        if (isCollapsed) {
          mBottomHeightPx -= LayoutUtil.dpToPx(80) + 2;
        } else {
          mBottomHeightPx += LayoutUtil.dpToPx(80) + 2;
        }
        Log.e(TAG, "Listener: Bottom height = " + mBottomHeightPx);
        mMap.setPadding(0, mTopHeightPx, 0, mBottomHeightPx);
      }
    });

    mLocationHelper.connect();
  }

  @Override
  public void onStop() {
    super.onStop();
    mLocationHelper.disconnect();
  }
}
