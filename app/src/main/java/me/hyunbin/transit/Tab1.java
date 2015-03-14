package me.hyunbin.transit;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import me.hyunbin.transit.R;

/**
 * Created by Hyunbin on 3/9/15.
 */

public class Tab1 extends Fragment {

    RecyclerView favoritesView;
    Context context;
    Map favoritesData;
    FavoritesAdapter adapter;
    TextView textView;
    ArrayList<HashMap<String, String>> favoritesList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);
        context = getActivity().getApplicationContext();

        // Sets animator to RecyclerView
        favoritesView = (RecyclerView) v.findViewById(R.id.favoritesView);
        favoritesView.setItemAnimator(new FadeInAnimator());
        favoritesView.getItemAnimator().setAddDuration(200);
        favoritesView.getItemAnimator().setRemoveDuration(100);

        // Hides no favorites view by default
        textView = (TextView) v.findViewById(R.id.textView);
        textView.setVisibility(View.GONE);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        favoritesView.setLayoutManager(layoutManager);

        new ParseFavoritesRequest().execute();
        return v;
    }

    public void refreshAdapter(){
        // Either sets an adapter if none has been initialized, or makes appropriate calls to
        // enable animations in the RecyclerView.

        if(adapter==null)
        {
            adapter = new FavoritesAdapter(context, favoritesList);
            favoritesView.setAdapter(adapter);
            adapter.notifyItemRangeInserted(0,adapter.getItemCount()-1);
        }
        else if(adapter!=null) {
            adapter.addAllItems(favoritesList);
        }
    }

    private static Comparator<HashMap<String, String>> ALPHABETICAL_ORDER = new Comparator<HashMap<String, String>>() {
    // Sorts HashMaps based on comparing their stop_name strings
        public int compare(HashMap<String, String> obj1, HashMap<String, String> obj2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(obj1.get("stop_name"), obj2.get("stop_name"));
            if (res == 0) {
                res = obj1.get("stop_name").compareTo(obj2.get("stop_name"));
            }
            return res;
        }
    };

    private class ParseFavoritesRequest extends AsyncTask<Void, Void, Void>
    {
        SharedPreferences favorites;
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Grab shared preferences and data
            favorites = context.getSharedPreferences("favorites",0);
            favoritesData = favorites.getAll();
            favoritesList = new ArrayList<HashMap<String, String>>();
        }

        protected Void doInBackground(Void ... arg0) {
            Iterator entries = favoritesData.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                HashMap<String, String> stopInfo = new HashMap<String, String>();
                stopInfo.put("stop_name", value);
                stopInfo.put("stop_id", key);
                favoritesList.add(stopInfo);
            }
            Collections.sort(favoritesList, ALPHABETICAL_ORDER);
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(favoritesList.size() != 0){
                refreshAdapter();
            }
            else{
                textView.setVisibility(View.VISIBLE);
            }
        }
    }
}
