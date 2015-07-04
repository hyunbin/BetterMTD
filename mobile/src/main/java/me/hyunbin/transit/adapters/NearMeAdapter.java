package me.hyunbin.transit.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import me.hyunbin.transit.MainActivity;
import me.hyunbin.transit.R;
import me.hyunbin.transit.StopActivity;
import me.hyunbin.transit.models.Stop;

/**
 * Created by Hyunbin on 7/3/2015.
 */

public class NearMeAdapter extends RecyclerView.Adapter
        <NearMeAdapter.ListItemViewHolder>{

    List<Stop> mData;

    public NearMeAdapter(List<Stop> data){
        if(data == null){
            throw new IllegalArgumentException("Adapter data must not be null");
        }
        this.mData = data;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position){
        long id = mData.get(position).getStopId().hashCode();
        return id;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearme_stop, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        final Stop stop = mData.get(position);
        holder.mStopNameTextView.setText(stop.getStopName());

        String distance;
        double test = stop.getDistance() * 0.000189394;
        if(test >= 0.11){
            distance = new DecimalFormat("#0.00").format(test);
            holder.mDistanceTextView.setText(distance + " mi");
        }
        else{
            holder.mDistanceTextView.setText(stop.getDistance() + " ft");
        }
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StopActivity.class);
                intent.putExtra(MainActivity.ARG_STOPID, stop.getStopId());
                intent.putExtra(MainActivity.ARG_STOPNAME, stop.getStopName());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mStopNameTextView;
        private TextView mDistanceTextView;
        private View mRootView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            mStopNameTextView = (TextView) itemView.findViewById(R.id.stopName);
            mDistanceTextView = (TextView) itemView.findViewById(R.id.distanceView);
            mRootView = itemView.findViewById(R.id.ripple);
        }
    }
}
