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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import me.hyunbin.transit.ApiClient;
import me.hyunbin.transit.R;
import me.hyunbin.transit.models.GetPlannedTripsByLatLonParams;
import me.hyunbin.transit.models.GetPlannedTripsByLatLonResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This fragment contains trip-planning features.
 */

public class NavigationFragment extends Fragment {

  private final static String TAG = NavigationFragment.class.getSimpleName();
  private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

  private ApiClient mApiClient;
  private Callback<GetPlannedTripsByLatLonResponse> mCallback;
  private GetPlannedTripsByLatLonParams mParams;

  private TextView mStartLocation;
  private TextView mEndLocation;
  private TextView mCallbackView;

  private LatLngBounds mServiceRegion;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mApiClient = new ApiClient();

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

    mCallback = new Callback<GetPlannedTripsByLatLonResponse>() {
      @Override
      public void onResponse(Response<GetPlannedTripsByLatLonResponse> response) {
        // TODO: do something pretty with the response here
        Log.e(TAG, response.body().toString());
      }

      @Override
      public void onFailure(Throwable t) {
        Log.e(TAG, "Error getting response for GetPlannedTripsByLatLon", t);
      }
    };
    mParams = new GetPlannedTripsByLatLonParams();

    // TODO: get rid of me, I am only for debugging into the night
    mParams.setTime("12:12");

    return view;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        Place place = PlaceAutocomplete.getPlace(getContext(), data);

        mCallbackView.setText(place.getName());
        mCallbackView.setTextColor(getResources().getColor(android.R.color.primary_text_light));

        if (mCallbackView == mStartLocation) {
          mParams.setOriginLat(place.getLatLng().latitude);
          mParams.setOriginLon(place.getLatLng().longitude);
        } else if (mCallbackView == mEndLocation) {
          mParams.setDestinationLat(place.getLatLng().latitude);
          mParams.setDestinationLon(place.getLatLng().longitude);
        }

        maybeGetPlannedTripsByLatLon();
      } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
        Status status = PlaceAutocomplete.getStatus(getContext(), data);
        Log.e(TAG, status.getStatusMessage());
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Log.d(TAG, "Result cancelled");
      }
    }
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

  private void maybeGetPlannedTripsByLatLon() {
    if (mParams.getOriginLat() != null
        && mParams.getOriginLon() != null
        && mParams.getDestinationLat() != null
        && mParams.getDestinationLon() != null) {
      getPlannedTripsByLatLon(mParams);
    }
  }

  private void getPlannedTripsByLatLon(GetPlannedTripsByLatLonParams params) {
    Call<GetPlannedTripsByLatLonResponse> call = mApiClient.getPlannedTripsByLatLon(params);
    call.enqueue(mCallback);
  }
}
