package me.hyunbin.transit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.hyunbin.transit.models.DeparturesByStopResponse;
import me.hyunbin.transit.models.ShapeResponse;
import me.hyunbin.transit.models.StopTimesByTripResponse;
import me.hyunbin.transit.models.StopsByLatLonResponse;
import me.hyunbin.transit.models.VehiclesByRouteResponse;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Hyunbin on 7/1/15.
 */
public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();
    private static final String BASE_URL = "https://developer.cumtd.com/api/v2.2/json/";
    private ApiInterface mApiInterface;

    public ApiClient() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mApiInterface = restAdapter.create(ApiInterface.class);
    }

    public ApiInterface getApiInterface(){
        return mApiInterface;
    }

    public Call<StopsByLatLonResponse> getStopsByLatLon(double lat, double lon){
        return mApiInterface.getStopsByLatLon(lat, lon, 20);
    }

    public Call<DeparturesByStopResponse> getDeparturesByStop(String stopId){
        return mApiInterface.getDeparturesByStop(stopId);
    }

    public Call<StopTimesByTripResponse> getStopTimesByTrip(String tripId){
        return mApiInterface.getStopTimesByTrip(tripId);
    }

    public Call<ShapeResponse> getShape(String shapeId){
        return mApiInterface.getShape(shapeId);
    }

    public Call<VehiclesByRouteResponse> getVehiclesByRoute(String routeId){
        return mApiInterface.getVehiclesByRoute(routeId);
    }
}
