package me.hyunbin.transit.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import me.hyunbin.transit.R;
import me.hyunbin.transit.SpacesItemDecoration;
import me.hyunbin.transit.adapters.FavoritesAdapter;
import me.hyunbin.transit.helpers.FavoritesHelper;

/**
 * This fragment contains both the favorites and near me stops listed out as chips, placed at the
 * bottom of DosMainActivity.
 */

public class DosFavoritesFragment extends Fragment {

  private static final String TAG = DosFavoritesFragment.class.getSimpleName();

  private static final int ANIMATION_DURATION_MS = 400;
  private static final int FAVORITES_SPAN_COUNT = 2;

  private FavoritesAdapter mFavoritesAdapter;
  private FavoritesHelper mFavoritesHelper;
  private RecyclerView mFavoritesList;

  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_dos_favorites, container, false);

    mFavoritesList = (RecyclerView) v.findViewById(R.id.favorites_list);
    setupRecyclerView(mFavoritesList);

    mFavoritesHelper = new FavoritesHelper(getActivity());
    mFavoritesHelper.setListener(new FavoritesHelper.Listener() {
      @Override
      public void onFavoritesParsed(List<HashMap<String, String>> favoritesList) {
        updateFavoritesData(favoritesList);
      }
    });

    return v;
  }

  @Override
  public void onStart() {
    super.onStart();
    mFavoritesHelper.parseFavorites();
  }

  private void setupRecyclerView(RecyclerView view) {
    view.getItemAnimator().setAddDuration(ANIMATION_DURATION_MS);
    view.getItemAnimator().setRemoveDuration(ANIMATION_DURATION_MS);

    StaggeredGridLayoutManager layoutManager;
    view.addItemDecoration(new SpacesItemDecoration(2, FAVORITES_SPAN_COUNT));
    layoutManager = new StaggeredGridLayoutManager(
        FAVORITES_SPAN_COUNT,
        StaggeredGridLayoutManager.HORIZONTAL);

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
}
