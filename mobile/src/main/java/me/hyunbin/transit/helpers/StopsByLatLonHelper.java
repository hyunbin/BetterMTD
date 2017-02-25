package me.hyunbin.transit.helpers;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import me.hyunbin.transit.ApiClient;
import me.hyunbin.transit.models.StopsByLatLonResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hyunbin on 2/24/17.
 */

public class StopsByLatLonHelper {
  public interface Listener {
    void onResponse(StopsByLatLonResponse response);
    void onFailure(String errorMsg);
  }

  private static final String TAG = StopsByLatLonHelper.class.getSimpleName();

  private ApiClient mApiClient;
  private Callback<StopsByLatLonResponse> mNearMeStopsResponse;
  private List<Listener> mListeners;

  public StopsByLatLonHelper() {
    mApiClient = new ApiClient();
    mNearMeStopsResponse = new Callback<StopsByLatLonResponse>() {
      @Override
      public void onResponse(Response<StopsByLatLonResponse> response) {
        if (response.isSuccess()) {
          for (Listener listener : mListeners) {
            listener.onResponse(response.body());
          }
        } else {
          for (Listener listener : mListeners) {
            listener.onFailure(response.errorBody().toString());
          }
        }
      }

      @Override
      public void onFailure(Throwable t) {
        for (Listener listener : mListeners) {
          listener.onFailure(t.toString());
        }
      }
    };
    mListeners = new ArrayList<>();
  }

  public void addListener(Listener listener) {
    mListeners.add(listener);
  }

  public void removeListener(Listener listener) {
    mListeners.remove(listener);
  }

  public void execute(LatLng location) {
    Call<StopsByLatLonResponse> call = mApiClient.getStopsByLatLon(
        location.latitude,
        location.longitude);
    call.enqueue(mNearMeStopsResponse);
  }

  public void execute(Location location) {
    Call<StopsByLatLonResponse> call = mApiClient.getStopsByLatLon(
        location.getLatitude(),
        location.getLongitude());
    call.enqueue(mNearMeStopsResponse);
  }
}
