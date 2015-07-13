package me.hyunbin.transit.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.hyunbin.transit.activities.MainActivity;
import me.hyunbin.transit.R;
import me.hyunbin.transit.activities.DeparturesActivity;
import me.hyunbin.transit.models.StopTime;

/**
 * Created by Hyunbin on 7/6/2015.
 */
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ListItemViewHolder> {

    private List<StopTime> mData;
    private DateFormat mInDateFormat;
    private DateFormat mOutDateFormat;

    public RouteAdapter(List<StopTime> data){
        if(data == null){
            throw new IllegalArgumentException("Adapter data must not be null");
        }
        mData = data;
        setHasStableIds(true);
        mInDateFormat = new SimpleDateFormat("HH:mm:ss");
        mOutDateFormat = new SimpleDateFormat("hh:mm a");
    }

    @Override
    public long getItemId(int position){
        long id = mData.get(position).getStopPoint().getCode().hashCode();
        return id;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stop_time, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        final StopTime stopTime = mData.get(position);
        try {
            Date time = mInDateFormat.parse(stopTime.getArrivalTime());
            holder.mTimeTextView.setText(mOutDateFormat.format(time));
        }
        catch(Exception e){
            e.printStackTrace();
            holder.mTimeTextView.setText(stopTime.getArrivalTime());
        }
        holder.mStopNameTextView.setText(stopTime.getStopPoint().getStopName().split("\\(", 2)[0]);
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DeparturesActivity.class);
                intent.putExtra(MainActivity.ARG_STOPID, stopTime.getStopPoint().getStopId().split(":", 2)[0]);
                intent.putExtra(MainActivity.ARG_STOPNAME, stopTime.getStopPoint().getStopName().split("\\(", 2)[0]);
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
        private TextView mTimeTextView;
        private LinearLayout mLinearLayout;
        private View mRootView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView.findViewById(R.id.ripple);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.listitem);
            mTimeTextView = (TextView) itemView.findViewById(R.id.timeView);
            mStopNameTextView = (TextView) itemView.findViewById(R.id.stopName);
        }
    }
}
