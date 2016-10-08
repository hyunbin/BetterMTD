package me.hyunbin.transit.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * A helper class to deal with the new Android permissions system in version 6.0 and higher.
 */

public class PermissionsHelper {

  public interface Listener {
    void onShowRationale();
  }

  private static final String TAG = PermissionsHelper.class.getSimpleName();
  private static final int PERMISSIONS_REQUEST_LOC = 128;

  private Activity mActivity;
  private Listener mListener;

  public PermissionsHelper(Activity activity) {
    mActivity = activity;
  }

  public void setListener(Listener listener) {
    mListener = listener;
  }

  public void checkForLocationPermission() {
    if (ActivityCompat.checkSelfPermission(
        mActivity,
        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

      if (ActivityCompat.shouldShowRequestPermissionRationale(
          mActivity,
          Manifest.permission.ACCESS_FINE_LOCATION)) {
        // The user has previously denied a permissions request.
        if (mListener != null) {
          mListener.onShowRationale();
        }
      } else {
        // This is the first time the user has seen the location dialogue.
        ActivityCompat.requestPermissions(
            mActivity,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            PERMISSIONS_REQUEST_LOC);
      }
    }
  }
}
