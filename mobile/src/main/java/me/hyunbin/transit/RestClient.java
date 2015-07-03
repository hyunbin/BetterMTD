package me.hyunbin.transit;

import android.util.Log;

import com.google.android.gms.common.api.Api;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hyunbin on 7/1/15.
 */
public class RestClient {

    private static final String TAG = RestClient.class.getSimpleName();
    private static final String BASE_URL = "https://developer.cumtd.com/api/v2.2/json";
    private ApiService mApiService;

    public RestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL).build();
        mApiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService(){
        return mApiService;
    }

    public void getStopsByLatLon(double lat, double lon, int count){
        Callback<List<ApiService.NearbyStops>> callback = new Callback<List<ApiService.NearbyStops>>(){
            @Override
            public void success(List<ApiService.NearbyStops> stopList, Response response) {
                for(ApiService.NearbyStops stop : stopList){
                    Log.e(TAG, stop.stopName);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Retrofit Error: " + error.toString());
            }
        };
        mApiService.getStopsByLatLon(lat, lon, count, callback);
    }

}
