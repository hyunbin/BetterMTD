package me.hyunbin.transit;

import me.hyunbin.transit.models.DeparturesByStopResponse;
import me.hyunbin.transit.models.ShapeResponse;
import me.hyunbin.transit.models.StopTimesByTripResponse;
import me.hyunbin.transit.models.StopsByLatLonResponse;
import me.hyunbin.transit.models.VehiclesByRouteResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Hyunbin on 7/1/15.
 */
public interface ApiInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    String token = MTD.apiKey;

    @GET("GetStopsByLatLon?key=" + token)
    Call<StopsByLatLonResponse>  getStopsByLatLon(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("count") int count
    );

    @GET("GetDeparturesByStop?key=" + token)
    Call<DeparturesByStopResponse> getDeparturesByStop(
            @Query("stop_id") String stopId
    );

    @GET("GetStopTimesByTrip?key=" + token)
    Call<StopTimesByTripResponse> getStopTimesByTrip(
            @Query("trip_id") String tripId
    );

    @GET("GetShape?key=" + token)
    Call<ShapeResponse> getShape(
            @Query("shape_id") String shapeId
    );

    @GET("GetVehiclesByRoute?key=" + token)
    Call<VehiclesByRouteResponse> getVehiclesByRoute(
            @Query("route_id") String routeId
    );
}