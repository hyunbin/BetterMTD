package me.hyunbin.transit.fragments;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import me.hyunbin.transit.ApiClient;
import me.hyunbin.transit.R;
import me.hyunbin.transit.SpacesItemDecoration;
import me.hyunbin.transit.adapters.FavoritesAdapter;
import me.hyunbin.transit.adapters.NearMeAdapter;
import me.hyunbin.transit.helpers.FavoritesHelper;
import me.hyunbin.transit.models.Stop;
import me.hyunbin.transit.models.StopsByLatLonResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This fragment contains both the favorites and near me stops listed out as chips, placed at the
 * bottom of DosMainActivity.
 */

public class DosStopsFragment extends Fragment {

  private static final String TAG = DosStopsFragment.class.getSimpleName();

  private static final int ANIMATION_DURATION_MS = 400;
  private static final int FAVORITES_SPAN_COUNT = 2;
  private static final int NEAR_ME_SPAN_COUNT = 1;

  private ApiClient mApiClient;
  private Callback<StopsByLatLonResponse> mNearMeStopsResponse;
  private FavoritesHelper mFavoritesHelper;

  private FavoritesAdapter mFavoritesAdapter;
  private NearMeAdapter mNearMeAdapter;
  private RecyclerView mFavoritesList;
  private RecyclerView mNearMeList;

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_dos_stops, container, false);

    mFavoritesList = (RecyclerView) v.findViewById(R.id.favorites_list);
    setupRecyclerView(mFavoritesList);

    mNearMeList = (RecyclerView) v.findViewById(R.id.near_me_list);
    setupRecyclerView(mNearMeList);

    mApiClient = new ApiClient();
    mNearMeStopsResponse = new Callback<StopsByLatLonResponse>() {
      @Override
      public void onResponse(Response<StopsByLatLonResponse> response) {
        if (response.isSuccess()) {
          Log.d(TAG, "Retrofit success!");
          List<Stop> stopList = response.body().getStops();
          updateNearMeData(stopList);
        } else {
          Log.d(TAG, "Retrofit Error: " + response.errorBody().toString());
          // TODO: Show network error.
        }
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(TAG, "Retrofit Error: " + t.toString());
        // TODO: Show network error.
      }
    };

    mFavoritesHelper = new FavoritesHelper(getActivity());
    mFavoritesHelper.setListener(new FavoritesHelper.Listener() {
      @Override
      public void onFavoritesParsed(List<HashMap<String, String>> favoritesList) {
        updateFavoritesData(favoritesList);
      }
    });
    mFavoritesHelper.parseFavorites();

    return v;
  }

  public void setLocation(Location location) {
    Call<StopsByLatLonResponse> call = mApiClient.getStopsByLatLon(
        location.getLatitude(),
        location.getLongitude());
    call.enqueue(mNearMeStopsResponse);
  }

  private void setupRecyclerView(RecyclerView view) {
    view.getItemAnimator().setAddDuration(ANIMATION_DURATION_MS);
    view.getItemAnimator().setRemoveDuration(ANIMATION_DURATION_MS);

    StaggeredGridLayoutManager layoutManager;
    if (view == mNearMeList) {
      view.addItemDecoration(new SpacesItemDecoration(2, NEAR_ME_SPAN_COUNT));
      layoutManager = new StaggeredGridLayoutManager(
          NEAR_ME_SPAN_COUNT,
          StaggeredGridLayoutManager.HORIZONTAL);
    } else {
      view.addItemDecoration(new SpacesItemDecoration(2, FAVORITES_SPAN_COUNT));
      layoutManager = new StaggeredGridLayoutManager(
          FAVORITES_SPAN_COUNT,
          StaggeredGridLayoutManager.HORIZONTAL);
    }

    view.setLayoutManager(layoutManager);
  }

  private void updateFavoritesData(List<HashMap<String, String>> data) {
    if (mFavoritesAdapter == null) {
      mFavoritesAdapter = new FavoritesAdapter(data);
      mFavoritesList.setAdapter(mFavoritesAdapter);
      mFavoritesAdapter.notifyItemRangeInserted(0, data.size() - 1);
    } else {
      mFavoritesAdapter.swapData(data);
      mFavoritesAdapter.notifyDataSetChanged();
    }
  }

  private void updateNearMeData(List<Stop> data) {
    if (mNearMeAdapter == null) {
      mNearMeAdapter = new NearMeAdapter(data);
      mNearMeList.setAdapter(mNearMeAdapter);
      mNearMeAdapter.notifyItemRangeInserted(0, data.size() - 1);
    } else {
      mNearMeAdapter.swapData(data);
      mNearMeAdapter.notifyDataSetChanged();
    }
  }
}
