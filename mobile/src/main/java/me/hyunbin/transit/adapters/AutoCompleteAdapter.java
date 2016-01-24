package me.hyunbin.transit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.hyunbin.transit.AutoCompleteClient;
import me.hyunbin.transit.R;
import me.hyunbin.transit.models.AutoCompleteItem;

/**
 * Created by Hyunbin on 1/23/16.
 */
public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<AutoCompleteItem> resultList = new ArrayList<>();
    private AutoCompleteClient mAutoCompleteClient;

    public AutoCompleteAdapter(Context context) {
        mContext = context;
        mAutoCompleteClient = new AutoCompleteClient();
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public AutoCompleteItem getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item, parent, false);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position).getN());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    //List<AutoCompleteItem> items = mAutoCompleteClient.getSuggestions(constraint.toString());

                    // Assign the data to the FilterResults
                    //filterResults.values = books;
                    //filterResults.count = books.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    //resultList = (List<Books>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }


}
