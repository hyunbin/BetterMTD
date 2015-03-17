package me.hyunbin.transit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import me.hyunbin.transit.R;

/**
 * Created by Hyunbin on 3/3/15.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter
        <RecyclerViewAdapter.ListItemViewHolder> {

    ArrayList<HashMap<String, String>> items;
    private static Context sContext;
    Handler handler;
    int updateInterval;
    int counter = 0;

    RecyclerViewAdapter(Context context, ArrayList<HashMap<String, String>> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException(
                    "modelData must not be null");
        }
        this.sContext = context;
        this.items = modelData;
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
        viewHolder.subText.setText(headSignFrag);
        viewHolder.listItem.setBackgroundColor(Color.parseColor("#" + model.get("route_color"))- 0x48000000);
        viewHolder.headSign.setTextColor(Color.parseColor("#" + model.get("route_text_color")));
        viewHolder.expectedMins.setTextColor(Color.parseColor("#" + model.get("route_text_color")));
        viewHolder.subText.setTextColor(Color.parseColor("#" + model.get("route_text_color")) - 0x5F000000);
        viewHolder.minsLabel.setTextColor(Color.parseColor("#" + model.get("route_text_color")) - 0x5F000000);
        viewHolder.mRootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence text = "Added stop to notifications";
                Toast toast = Toast.makeText(sContext, text, Toast.LENGTH_SHORT);
                toast.show();

                setNotification();
                // Sets a handler to refresh the notification periodically
                updateInterval = 5000;
                handler = new Handler();
                //handler.postDelayed(updateTask,updateInterval);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeAllItems() {
        final int size = items.size();
        for(int i = size-1; i >= 0 ; i--) {
            items.remove(i);
            notifyItemRemoved(i);
        }
    }

    public void addAllItems(ArrayList<HashMap<String, String>> newItems){
        for(int n = 0 ; n < newItems.size() ; n++) {
            items.add(newItems.get(n));
            notifyItemInserted(items.size() - 1);
        }
    }

    public void addOneItem(HashMap<String, String> newItem){
        items.add(newItem);
        notifyItemInserted(items.size() - 1);
    }

    public void setNotification(){

        int mNotificationId = 001;

        /*
        //Create an Intent for the BroadcastReceiver
        Intent buttonIntent = new Intent(sContext, ButtonReceiver.class);
        buttonIntent.putExtra("notificationId",mNotificationId);

        //Create the PendingIntent
        PendingIntent dismissIntent = PendingIntent.getBroadcast(sContext.getApplicationContext(), 0, buttonIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        */

        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(mNotificationId, sContext);

        //Create the notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(sContext)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("My notification")
                        .setContentText("Hello world!" + counter)
                        .setOngoing(true)
                        .addAction(R.drawable.ic_close, "Dismiss now", dismissIntent)
                        .setPriority(2);

        counter++;

        NotificationManager mNotifyMgr =
                (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    final Runnable updateTask=new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh items at a predetermined interval
            setNotification();
            handler.postDelayed(updateTask, updateInterval);
        }
    };

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView headSign;
        TextView expectedMins;
        TextView subText;
        TextView minsLabel;
        RelativeLayout listItem;
        View mRootView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            listItem = (RelativeLayout) itemView.findViewById(R.id.listitem);
            headSign = (TextView) itemView.findViewById(R.id.headsign);
            expectedMins = (TextView) itemView.findViewById(R.id.expectedmins);
            subText = (TextView) itemView.findViewById(R.id.subtext);
            minsLabel = (TextView) itemView.findViewById(R.id.minslabel);
            mRootView = itemView.findViewById(R.id.ripple);
        }
    }
}

