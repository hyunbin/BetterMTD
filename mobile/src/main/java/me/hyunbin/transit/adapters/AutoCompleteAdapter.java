package me.hyunbin.transit.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import me.hyunbin.transit.activities.MainActivity;
import me.hyunbin.transit.R;
import me.hyunbin.transit.activities.DeparturesActivity;
import me.hyunbin.transit.models.AutoCompleteItem;
import me.hyunbin.transit.models.Stop;

public class AutoCompleteAdapter extends RecyclerView.Adapter
    <AutoCompleteAdapter.ListItemViewHolder> {

  List<AutoCompleteItem> mData;

  public AutoCompleteAdapter(List<AutoCompleteItem> data) {
    if (data == null) {
      throw new IllegalArgumentException("Adapter data must not be null");
    }
    this.mData = data;
    setHasStableIds(true);
  }

  public void swapData(List<AutoCompleteItem> data) {
    mData = data;
  }

  @Override
  public long getItemId(int position) {
    long id = mData.get(position).getI().hashCode();
    return id;
  }

  @Override
  public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_favorite, parent, false);
    return new ListItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(ListItemViewHolder holder, int position) {
    final AutoCompleteItem stop = mData.get(position);
    holder.mStopName.setText(stop.getN());

    holder.mRoot.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), DeparturesActivity.class);
        intent.putExtra(MainActivity.ARG_STOPID, stop.getI());
        intent.putExtra(MainActivity.ARG_STOPNAME, stop.getN());
        v.getContext().startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return mData.size();
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
