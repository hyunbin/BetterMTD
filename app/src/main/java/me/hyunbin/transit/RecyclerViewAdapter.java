package me.hyunbin.transit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hyunbin on 3/3/15.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter
        <RecyclerViewAdapter.ListItemViewHolder> {

    private final String ARG_TRIPID = "trip_id";
    private final String ARG_HEADSIGN = "headsign";
    private final String TAG_VEHICLEID = "vehicle_id";

    ArrayList<HashMap<String, String>> items;
    private Context context;
    String currentStopName;
    RecyclerView parentView;

    RecyclerViewAdapter(Context context, ArrayList<HashMap<String, String>> modelData, String currentStopName) {
        if (modelData == null) {
            throw new IllegalArgumentException(
                    "modelData must not be null");
        }
        this.context = context;
        this.items = modelData;
        this.currentStopName = currentStopName;
        setHasStableIds(true);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView view){
        parentView = view;
    }

    @Override
    public long getItemId(int position){
        long id = items.get(position).get(ARG_HEADSIGN).hashCode();
        String subId = items.get(position).get(TAG_VEHICLEID);
        if(subId != "null"){
            id = id*10000 + Long.parseLong(subId);
        }
        else{
            id = id*10000;
        }
        return id;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.list_item,
                        viewGroup,
                        false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(
            ListItemViewHolder viewHolder, int position) {
        HashMap<String, String> model = items.get(position);
        viewHolder.headSign.setText(model.get("headsign"));
        viewHolder.expectedMins.setText(model.get("expected_mins"));
        String headSignFrag = "";
        if(model.get("trip_headsign")=="") {
            headSignFrag = "";
        }
        else{
            headSignFrag = "To " + model.get("trip_headsign");
        }

        final String mRouteColor = model.get("route_color");
        String mRouteTextColor = model.get("route_text_color");

        // Special adjustment for ugly red
        if(mRouteColor.equals("ff0000") || mRouteColor.equals("ed1c24")){
            viewHolder.listItem.setBackgroundColor(Color.parseColor("#" + mRouteColor) - 0xD2000000);
            mRouteTextColor = "ffffff";
        }

        viewHolder.listItem.setBackgroundColor(Color.parseColor("#" + mRouteColor) - 0x48000000);
        viewHolder.subText.setText(headSignFrag);
        viewHolder.headSign.setTextColor(Color.parseColor("#" + mRouteTextColor));
        viewHolder.expectedMins.setTextColor(Color.parseColor("#" + mRouteTextColor));
        viewHolder.subText.setTextColor(Color.parseColor("#" + mRouteTextColor) - 0x5F000000);
        viewHolder.minsLabel.setTextColor(Color.parseColor("#" + mRouteTextColor) - 0x5F000000);

        if(model.get("is_istop") == "true"){
            if(model.get("route_text_color").equals("ffffff")){
                viewHolder.iStopView.setImageResource(R.drawable.ic_istop_light);
            }
            else {
                viewHolder.iStopView.setImageResource(R.drawable.ic_istop_dark);
            }
            viewHolder.iStopView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else{
            viewHolder.iStopView.setVisibility(View.GONE);
        }

        // Sets an onClickListener to open up a new activity with details
        final String trip = model.get(ARG_TRIPID);
        final String title = model.get(ARG_HEADSIGN);
        final String routeTextColor = mRouteTextColor;
        final String sHeadSignFrag = headSignFrag;

        viewHolder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sHeadSignFrag != ""){
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra(ARG_TRIPID, trip);
                    intent.putExtra(ARG_HEADSIGN, title);
                    intent.putExtra("current_stop", currentStopName);
                    intent.putExtra("route_color", mRouteColor);
                    intent.putExtra("text_color", routeTextColor);
                    v.getContext().startActivity(intent);
                }
                else{
                    // Dismisses the Snackbar being shown, if any, and displays the new one
                    Snackbar.make(parentView, "This bus has no scheduled information",
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setNotification(String route, String time){
        int mNotificationId = 001;
        // Specify the action to perform when dismiss button is clicked
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(mNotificationId, context);

        // Create the notification and populate its parameters
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(route)
                        .setContentText(time + " min remaining")
                        .setOngoing(true)
                        .addAction(R.drawable.ic_close, "Dismiss now", dismissIntent)
                        .setPriority(2);

        // Push the notification to the user
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView headSign;
        TextView expectedMins;
        TextView subText;
        TextView minsLabel;
        RelativeLayout listItem;
        View mRootView;
        ImageView iStopView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            listItem = (RelativeLayout) itemView.findViewById(R.id.listitem);
            headSign = (TextView) itemView.findViewById(R.id.headsign);
            expectedMins = (TextView) itemView.findViewById(R.id.expectedmins);
            subText = (TextView) itemView.findViewById(R.id.subtext);
            minsLabel = (TextView) itemView.findViewById(R.id.minslabel);
            mRootView = itemView.findViewById(R.id.ripple);
            iStopView = (ImageView) itemView.findViewById(R.id.iStopView);
        }
    }
}