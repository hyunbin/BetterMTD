package me.hyunbin.transit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.hyunbin.transit.R;
import me.hyunbin.transit.models.Itinerary;

/**
 * Created by Hyunbin on 8/15/16.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ListItemViewHolder> {

  private List<Itinerary> mItineraries;

  public NavigationAdapter(List<Itinerary> itineraries) {
    mItineraries = itineraries;
  }

  @Override
  public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View item = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.item_itinerary, parent, false);
    return new ListItemViewHolder(item);
  }

  @Override
  public void onBindViewHolder(ListItemViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return mItineraries.size();
  }

  public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
    // TODO: add necessary elements here

    public ListItemViewHolder(View view) {
      super(view);
      // TODO: find necessary elements here
    }
  }
}
