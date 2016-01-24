package me.hyunbin.transit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import me.hyunbin.transit.models.AutoCompleteItem;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Hyunbin on 1/23/16.
 */
public class AutoCompleteClient {

    private static final String TAG = ApiClient.class.getSimpleName();
    private static final String BASE_URL = "http://www.cumtd.com/autocomplete/stops/v1.0/json/";
    private final AutoCompleteInterface mApiService;

    public AutoCompleteClient() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mApiService = restAdapter.create(AutoCompleteInterface.class);
    }

    public Call<List<AutoCompleteItem>> getSuggestions(String query){
        return mApiService.getSuggestions(query);
    }
}
