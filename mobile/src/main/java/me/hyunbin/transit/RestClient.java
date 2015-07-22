package me.hyunbin.transit;

import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import me.hyunbin.transit.models.DeparturesByStopResponse;
import me.hyunbin.transit.models.Stop;
import me.hyunbin.transit.models.StopTimesByTripResponse;
import me.hyunbin.transit.models.StopsByLatLonResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by Hyunbin on 7/1/15.
 */
public class RestClient {

    private static final String TAG = RestClient.class.getSimpleName();
    private static final String BASE_URL = "https://developer.cumtd.com/api/v2.2/json";
    private ApiService mApiService;

    public RestClient() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        mApiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService(){
        return mApiService;
    }

    public void getStopsByLatLon(double lat, double lon, Callback<StopsByLatLonResponse> callback){
        mApiService.getStopsByLatLon(lat, lon, 20, callback);
    }

    public void getDeparturesByStop(String stopId, Callback<DeparturesByStopResponse> callback){
        mApiService.getDeparturesByStop(stopId, callback);
    }

    public void getStopTimesByTrip(String tripId, Callback<StopTimesByTripResponse> callback){
        mApiService.getStopTimesByTrip(tripId, callback);
    }
}
