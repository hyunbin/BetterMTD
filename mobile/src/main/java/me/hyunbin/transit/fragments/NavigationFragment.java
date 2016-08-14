package me.hyunbin.transit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import me.hyunbin.transit.R;

/**
 * This fragment contains trip-planning features.
 */

public class NavigationFragment extends Fragment {

  private final static String TAG = NavigationFragment.class.getSimpleName();
  private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

  private TextView mStartLocation;
  private TextView mEndLocation;
  private TextView mCallbackView;

  private LatLngBounds mServiceRegion;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mServiceRegion = new LatLngBounds(
        new LatLng(40.012331, -88.357561),
        new LatLng(40.190802, -88.093802));
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_navigation, container, false);

    mStartLocation = (TextView) view.findViewById(R.id.start_location);
    mEndLocation = (TextView) view.findViewById(R.id.end_location);

    mStartLocation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showPlaceAutoComplete(mStartLocation);
      }
    });
    mEndLocation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showPlaceAutoComplete(mEndLocation);
      }
    });
    return view;
  }

  private void showPlaceAutoComplete(TextView view) {
    try {
      Intent intent =
          new PlaceAutocomplete
              .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
              .setBoundsBias(mServiceRegion)
              .build(getActivity());
      startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
      e.printStackTrace();
    }

    mCallbackView = view;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        Place place = PlaceAutocomplete.getPlace(getContext(), data);
        Log.i(TAG, "Place: " + place.getName() + ", LatLon: " + place.getLatLng());
        mCallbackView.setText(place.getName());
        mCallbackView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
      } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
        Status status = PlaceAutocomplete.getStatus(getContext(), data);
        // TODO: Handle the error.
        Log.i(TAG, status.getStatusMessage());
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Log.e(TAG, "Result cancelled");
        // The user canceled the operation.
      }
    }
  }

}
