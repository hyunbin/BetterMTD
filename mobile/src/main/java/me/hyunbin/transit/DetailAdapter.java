package me.hyunbin.transit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Hyunbin on 4/21/15.
 */
public class DetailAdapter extends RecyclerView.Adapter
<DetailAdapter.ListItemViewHolder>  {

    private final String TAG_STOPID = "stop_id";
    private final String TAG_STOPNAME = "stop_name";
    private final String TAG_ARRIVALTIME = "arrival_time";
    public final static String ARG_STOPID = "cashpa.bettermtd.STOPID";
    public final static String ARG_STOPNAME = "cashpa.bettermtd.STOPNAME";

    ArrayList<HashMap<String, String>> items;
    private Context context;
    DateFormat dateFormat;
    DateFormat outFormat;

    DetailAdapter(Context context, ArrayList<HashMap<String, String>> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException(
                    "modelData must not be null");
        }
        this.context = context;
        this.items = modelData;
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        outFormat = new SimpleDateFormat("hh:mm a");
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.detail_stop, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(
            ListItemViewHolder viewHolder, int position) {
        final HashMap<String, String> model = items.get(position);
        viewHolder.stopName.setText(model.get(TAG_STOPNAME));

        try {
            Date time = (Date) dateFormat.parse(model.get(TAG_ARRIVALTIME));
            viewHolder.timeView.setText(outFormat.format(time));
        }
        catch(Exception e){
            viewHolder.timeView.setText(model.get(TAG_ARRIVALTIME));
        }

        viewHolder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StopActivity.class);
                intent.putExtra(ARG_STOPID, model.get(TAG_STOPID));
                intent.putExtra(ARG_STOPNAME, model.get(TAG_STOPNAME));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addAllItems(ArrayList<HashMap<String, String>> newItems){
        for(int n = 0 ; n < newItems.size() ; n++) {
            items.add(newItems.get(n));
            notifyItemInserted(items.size() - 1);
        }
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView stopName;
        TextView timeView;
        LinearLayout listItem;
        View mRootView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView.findViewById(R.id.ripple);
            listItem = (LinearLayout) itemView.findViewById(R.id.listitem);
            timeView = (TextView) itemView.findViewById(R.id.timeView);
            stopName = (TextView) itemView.findViewById(R.id.stopName);
        }
    }
}
