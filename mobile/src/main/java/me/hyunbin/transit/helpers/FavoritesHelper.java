package me.hyunbin.transit.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class to fetch favorites from SharedPreferences and sort them alphabetically.
 *
 * TODO: The implementation in this class and FavoritesAdapter is pretty terrible.
 */
public class FavoritesHelper {
  public interface Listener {
    void onFavoritesParsed(List<HashMap<String, String>> favoritesList);
  }

  private ArrayList<HashMap<String, String>> mFavoritesList;
  private Context mContext;
  private Listener mListener;
  private Map mFavoritesData;

  public FavoritesHelper(Context context) {
    mContext = context;
  }

  public void parseFavorites() {
    new ParseFavoritesRequest().execute();
  }

  public void setListener(Listener listener) {
    mListener = listener;
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

  private class ParseFavoritesRequest extends AsyncTask<Void, Void, Void> {
    SharedPreferences favorites;

    protected void onPreExecute() {
      super.onPreExecute();
      // Grab shared preferences and data
      favorites = mContext.getSharedPreferences("favorites", 0);
      mFavoritesData = favorites.getAll();
      mFavoritesList = new ArrayList<>();
    }

    protected Void doInBackground(Void... arg0) {
      for (Object o : mFavoritesData.entrySet()) {
        Map.Entry entry = (Map.Entry) o;
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
      if (mListener != null) {
        mListener.onFavoritesParsed(mFavoritesList);
      }
    }
  }
}
