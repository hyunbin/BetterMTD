package me.hyunbin.transit.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import me.hyunbin.transit.MainActivity;
import me.hyunbin.transit.R;
import me.hyunbin.transit.StopActivity;


/**
 * Created by Hyunbin on 3/10/15.
 */
public class FavoritesAdapter extends RecyclerView.Adapter
        <FavoritesAdapter.ListItemViewHolder> {

    ArrayList<HashMap<String, String>> mData;

    public FavoritesAdapter(ArrayList<HashMap<String, String>> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        this.mData = modelData;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position){
        long id = mData.get(position).get("stop_id").hashCode();
        return id;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favorites_stop, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(
            ListItemViewHolder viewHolder, int position) {
        final HashMap<String, String> model = mData.get(position);
        viewHolder.mStopNameTextView.setText(model.get("stop_name"));
        viewHolder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StopActivity.class);
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
        for(int i = size-1; i >= 0 ; i--) {
            mData.remove(i);
            notifyItemRemoved(i);
        }
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView mStopNameTextView;
        View mRootView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            mStopNameTextView = (TextView) itemView.findViewById(R.id.stopName);
            mRootView = itemView.findViewById(R.id.ripple);
        }
    }

}
