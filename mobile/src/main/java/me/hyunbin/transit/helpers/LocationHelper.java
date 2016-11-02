package me.hyunbin.transit.helpers;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * A helper class to easily find the location of the user.
 */

public class LocationHelper implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

  public interface Listener {
    void onLocationChanged(Location location);
  }

  private static final String TAG = LocationHelper.class.getSimpleName();

  private static final int UPDATE_INTERVAL = 90000;
  private static final int MIN_UPDATE_INTERVAL = 30000;

  private Activity mActivity;
  private GoogleApiClient mGoogleApiClient;

  private Listener mListener;
  private PermissionsHelper mPermissionsHelper;

  public LocationHelper(Activity activity) {
    mActivity = activity;
    mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

    mPermissionsHelper = new PermissionsHelper(mActivity);
  }

  public void connect() {
    mGoogleApiClient.connect();
  }

  public void disconnect() {
    mGoogleApiClient.disconnect();
  }

  public void setListener(Listener listener) {
    mListener = listener;
  }

  @Override
  public void onConnected(Bundle bundle) {
    Log.d(TAG, "Connected to Google Play Services");

    if (!mPermissionsHelper.checkForLocationPermission(true)) {
      return;
    }

    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (mListener != null) {
      mListener.onLocationChanged(location);
    }

    LocationRequest locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setInterval(UPDATE_INTERVAL)
        .setFastestInterval(MIN_UPDATE_INTERVAL);
    LocationServices.FusedLocationApi.requestLocationUpdates(
        mGoogleApiClient,
        locationRequest,
        this);
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.d(TAG, "Connection suspended to Google Play Services");
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d(TAG, "Connection failed to Google Play Services");
  }

  @Override
  public void onLocationChanged(Location location) {
    if (mListener != null) {
      mListener.onLocationChanged(location);
    }
  }
}
