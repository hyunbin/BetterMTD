package me.hyunbin.transit.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.hyunbin.transit.DetailActivity;
import me.hyunbin.transit.R;
import me.hyunbin.transit.models.Departure;

/**
 * Created by Hyunbin on 7/3/2015.
 */
public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.ListItemViewHolder>{

    private static final String ARG_TRIPID = "trip_id";
    private static final String ARG_HEADSIGN = "headsign";

    private List<Departure> mData;
    private String mCurrentStopName;
    private RecyclerView mParentRecyclerView;

    public StopsAdapter(List<Departure> data, String currentStopName){
        if(data == null){
            throw new IllegalArgumentException("Adapter data must not be null");
        }
        this.mData = data;
        this.mCurrentStopName = currentStopName;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position){
        long id = mData.get(position).getHeadsign().hashCode();
        String subId = mData.get(position).getVehicleId();
        if(subId != "null")
            id = id*10000 + Long.parseLong(subId);
        else
            id = id*10000;
        return id;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView view){
        mParentRecyclerView = view;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        final Departure departure = mData.get(position);

        holder.mHeadSignTextView.setText(departure.getHeadsign());
        holder.mExpectedMinsTextView.setText(departure.getExpectedMins() + "");

        String headSignFrag = "";
        if(departure.getTrip() != null)
            headSignFrag = "To " + departure.getTrip().getTripHeadsign();
        holder.mSubTextView.setText(headSignFrag);

        final String routeColor = departure.getRoute().getRouteColor();
        String routeTextColor = departure.getRoute().getRouteTextColor();
        // Special adjustment for ugly red
        if(routeColor.equals("ff0000") || routeColor.equals("ed1c24")){
            holder.mListItem.setBackgroundColor(Color.parseColor("#" + routeColor) - 0xD2000000);
            routeTextColor = "ffffff";
        }
        final String sRouteTextColor = routeTextColor;

        holder.mListItem.setBackgroundColor(Color.parseColor("#" + routeColor) - 0x48000000);
        holder.mHeadSignTextView.setTextColor(Color.parseColor("#" + routeTextColor));
        holder.mExpectedMinsTextView.setTextColor(Color.parseColor("#" + routeTextColor));
        holder.mSubTextView.setTextColor(Color.parseColor("#" + routeTextColor) - 0x5F000000);
        holder.mMinsTextView.setTextColor(Color.parseColor("#" + routeTextColor) - 0x5F000000);

        if(departure.isIsIstop() == true){
            if(routeTextColor.equals("ffffff"))
                holder.mIStopImageView.setImageResource(R.drawable.ic_istop_light);
            else
                holder.mIStopImageView.setImageResource(R.drawable.ic_istop_dark);
            holder.mIStopImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else {
            holder.mIStopImageView.setVisibility(View.GONE);
        }

        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(departure.getTrip() != null){
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra(ARG_TRIPID, departure.getTrip().getTripId());
                    intent.putExtra(ARG_HEADSIGN, departure.getHeadsign());
                    intent.putExtra("current_stop", mCurrentStopName);
                    intent.putExtra("route_color", routeColor);
                    intent.putExtra("text_color", sRouteTextColor);
                    v.getContext().startActivity(intent);
                }
                else{
                    // Dismisses the Snackbar being shown, if any, and displays the new one
                    Snackbar.make(mParentRecyclerView, "This bus has no scheduled information",
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mHeadSignTextView;
        private TextView mExpectedMinsTextView;
        private TextView mSubTextView;
        private TextView mMinsTextView;
        private RelativeLayout mListItem;
        private ImageView mIStopImageView;
        private View mRootView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            mListItem = (RelativeLayout) itemView.findViewById(R.id.listitem);
            mHeadSignTextView = (TextView) itemView.findViewById(R.id.headsign);
            mExpectedMinsTextView = (TextView) itemView.findViewById(R.id.expectedmins);
            mSubTextView = (TextView) itemView.findViewById(R.id.subtext);
            mMinsTextView = (TextView) itemView.findViewById(R.id.minslabel);
            mRootView = itemView.findViewById(R.id.ripple);
            mIStopImageView = (ImageView) itemView.findViewById(R.id.iStopView);
        }
    }
}
