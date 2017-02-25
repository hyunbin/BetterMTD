package me.hyunbin.transit.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.hyunbin.transit.activities.MainActivity;
import me.hyunbin.transit.R;
import me.hyunbin.transit.activities.DeparturesActivity;

public class FavoritesAdapter extends RecyclerView.Adapter
    <FavoritesAdapter.ListItemViewHolder> {

  private List<HashMap<String, String>> mData;
  private int mLayoutResource;

  public FavoritesAdapter(List<HashMap<String, String>> modelData, int layoutResource) {
    if (modelData == null) {
      throw new IllegalArgumentException("modelData must not be null");
    }
    this.mData = modelData;
    mLayoutResource = layoutResource;
    setHasStableIds(true);
  }

  public void swapData(List<HashMap<String, String>> data) {
    mData = data;
  }

  @Override
  public long getItemId(int position) {
    long id = mData.get(position).get("stop_id").hashCode();
    return id;
  }

  @Override
  public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View itemView = LayoutInflater.from(viewGroup.getContext())
        .inflate(mLayoutResource, viewGroup, false);
    return new ListItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(
      ListItemViewHolder viewHolder, int position) {
    final HashMap<String, String> model = mData.get(position);
    viewHolder.mStopName.setText(model.get("stop_name"));
    viewHolder.mRoot.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), DeparturesActivity.class);
        intent.putExtra(MainActivity.ARG_STOPID, model.get("stop_id"));
        intent.putExtra(MainActivity.ARG_STOPNAME, model.get("stop_name"));
        v.getContext().startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public void removeAllItems() {
    final int size = mData.size();
    for (int i = size - 1; i >= 0; i--) {
      mData.remove(i);
      notifyItemRemoved(i);
    }
  }

  public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
    private TextView mStopName;
    private View mRoot;

    public ListItemViewHolder(View itemView) {
      super(itemView);
      mStopName = (TextView) itemView.findViewById(R.id.stop_name);
      mRoot = itemView.findViewById(R.id.list_item);
    }
  }
}
