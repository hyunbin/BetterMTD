package me.hyunbin.transit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import me.hyunbin.transit.R;

/**
 * Created by Hyunbin on 3/9/15.
 */

public class Tab2 extends Fragment {

    Context context;
    Map recentsData;
    Stack recentsList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_2,container,false);
        context = getActivity().getApplicationContext();
        //new ParseFavoritesRequest().execute();
        return v;
    }

    private static Comparator<HashMap<String, String>> NUMERICAL_ORDER = new Comparator<HashMap<String, String>>() {
        // Sorts HashMaps based on comparing their stop_name strings
        public int compare(HashMap<String, String> obj1, HashMap<String, String> obj2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(obj2.get("order"), obj1.get("order"));
            if (res == 0) {
                res = obj2.get("order").compareTo(obj1.get("order"));
            }
            return res;
        }
    };

    private class ParseFavoritesRequest extends AsyncTask<Void, Void, Void>
    {
        SharedPreferences recents;
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Grab shared preferences and data
            recents = context.getSharedPreferences("recents",0);
            recentsData = recents.getAll();
            recentsList = new Stack<HashMap<String, String>>();
        }

        protected Void doInBackground(Void ... arg0) {
            Iterator entries = recentsData.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();

                String key = (String) entry.getKey();
                String[] value = (String[]) ((HashSet) entry.getValue()).toArray();
                String stopID = value[0];
                String stopName = value[1];

                HashMap<String, String> stopInfo = new HashMap<String, String>();
                stopInfo.put("stop_name", stopName);
                stopInfo.put("stop_id", stopID);
                stopInfo.put("order", key);

                recentsList.push(stopInfo);
            }
            Collections.sort(recentsList, NUMERICAL_ORDER);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(recentsList.size() != 0){
                //refreshAdapter();
            }
        }
    }
}
