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

/**
 * Created by Hyunbin on 3/9/15.
 */

public class Tab1 extends Fragment {

    private static final String TAG = Tab1.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private Context mContext;
    private TextView mTextView;

    private FavoritesAdapter mAdapter;
    private Map mFavoritesData;
    private ArrayList<HashMap<String, String>> mFavoritesList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1,container,false);
        mContext = getActivity().getApplicationContext();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.favorites_view);
        mRecyclerView.setHasFixedSize(true);

        // Sets animator to RecyclerView
        mRecyclerView.setItemAnimator(new FadeInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(200);
        mRecyclerView.getItemAnimator().setRemoveDuration(100);

        // Hides no favorites view by default
        mTextView = (TextView) v.findViewById(R.id.text_view);
        mTextView.setVisibility(View.GONE);

        // Uses linear layout manager for simplicity
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        return v;
    }

    public void refreshAdapter(){
        /* Either sets an adapter if none has been initialized, or makes appropriate calls to
        enable animations in the RecyclerView. */
        if(mAdapter ==null)
        {
            mAdapter = new FavoritesAdapter(mContext, mFavoritesList);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount()-1);
        }
        else if(mAdapter !=null) {
            mAdapter.addAllItems(mFavoritesList);
        }
    }

    private Comparator<HashMap<String, String>> ALPHABETICAL_ORDER = new Comparator<HashMap<String, String>>() {
    // Sorts HashMaps based on comparing their stop_name strings
        public int compare(HashMap<String, String> obj1, HashMap<String, String> obj2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(obj1.get("stop_name"), obj2.get("stop_name"));
            if (res == 0) {
                res = obj1.get("stop_name").compareTo(obj2.get("stop_name"));
            }
            return res;
        }
    };

    @Override
    public void onStart(){
        super.onStart();
        new ParseFavoritesRequest().execute();
    }

    private class ParseFavoritesRequest extends AsyncTask<Void, Void, Void>
    {
        SharedPreferences favorites;
        protected void onPreExecute()
        {
            super.onPreExecute();
            if(mAdapter !=null){
                mAdapter.removeAllItems();
            }
            // Grab shared preferences and data
            favorites = mContext.getSharedPreferences("favorites",0);
            mFavoritesData = favorites.getAll();
            mFavoritesList = new ArrayList<HashMap<String, String>>();
        }

        protected Void doInBackground(Void ... arg0) {
            Iterator entries = mFavoritesData.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                HashMap<String, String> stopInfo = new HashMap<String, String>();
                stopInfo.put("stop_name", value);
                stopInfo.put("stop_id", key);
                mFavoritesList.add(stopInfo);
            }
            Collections.sort(mFavoritesList, ALPHABETICAL_ORDER);
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(mFavoritesList.size() != 0){
                refreshAdapter();
            }
            else{
                mTextView.setText("Network error :c");
                mTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}