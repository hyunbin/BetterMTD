package me.hyunbin.transit.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationHelper
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final int UPDATE_INTERVAL = 90000;
  private static final int MIN_UPDATE_INTERVAL = 30000;

  private static final int REQUEST_RESOLVE_ERROR = 9000;
  private static final int PERMISSIONS_REQUEST_LOC = 128;

  private boolean mResolvingError = false;

  private Context mContext;

  private GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;
  private Location mLastLocation;
  private Location mPrevLocation;

  public LocationHelper(Context context) {
    mContext = context;
    setupLocationService();
  }

  public void connect() {
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {

  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  private void setupLocationService() {
    mGoogleApiClient = new GoogleApiClient.Builder(mContext)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();

    // Create the LocationRequest object
    mLocationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setInterval(UPDATE_INTERVAL)
        .setFastestInterval(MIN_UPDATE_INTERVAL);
  }


}
