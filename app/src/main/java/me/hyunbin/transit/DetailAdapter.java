package me.hyunbin.transit;

import android.content.Context;
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
 * Created by Hyunbin on 4/21/15.
 */
public class DetailAdapter extends RecyclerView.Adapter
<DetailAdapter.ListItemViewHolder>  {

    private final String TAG_STOPNAME = "stop_name";
    private final String TAG_ARRIVALTIME = "arrival_time";

    ArrayList<HashMap<String, String>> items;
    private Context context;


    DetailAdapter(Context context, ArrayList<HashMap<String, String>> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException(
                    "modelData must not be null");
        }
        this.context = context;
        this.items = modelData;
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
        HashMap<String, String> model = items.get(position);
        viewHolder.stopName.setText(model.get(TAG_STOPNAME));
        viewHolder.timeView.setText(model.get(TAG_ARRIVALTIME));
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

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView timeView;
        TextView stopName;
        RelativeLayout listItem;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            listItem = (RelativeLayout) itemView.findViewById(R.id.listitem);
            timeView = (TextView) itemView.findViewById(R.id.timeView);
            stopName = (TextView) itemView.findViewById(R.id.stopName);
        }
    }
}
