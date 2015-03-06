package cashpa.bettermtd;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hyunbin on 3/3/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter
        <RecyclerViewAdapter.ListItemViewHolder> {

    ArrayList<HashMap<String, String>> items;
    private static Context sContext;

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

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView headSign;
        TextView expectedMins;
        TextView subText;
        TextView minsLabel;
        RelativeLayout listItem;
        public ListItemViewHolder(View itemView) {
            super(itemView);
            // TODO populate ViewHolder with more items
            listItem = (RelativeLayout) itemView.findViewById(R.id.listitem);
            headSign = (TextView) itemView.findViewById(R.id.headsign);
            expectedMins = (TextView) itemView.findViewById(R.id.expectedmins);
            subText = (TextView) itemView.findViewById(R.id.subtext);
            minsLabel = (TextView) itemView.findViewById(R.id.minslabel);
        }
    }
}

