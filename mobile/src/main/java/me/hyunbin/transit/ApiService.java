package me.hyunbin.transit;

import java.util.List;

import me.hyunbin.transit.models.DeparturesByStopResponse;
import me.hyunbin.transit.models.ShapeResponse;
import me.hyunbin.transit.models.StopTimesByTripResponse;
import me.hyunbin.transit.models.StopsByLatLonResponse;
import me.hyunbin.transit.models.VehiclesByRouteResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

/**
 * Created by Hyunbin on 7/1/15.
 */
public interface ApiService {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    String token = MTD.apiKey;

    @GET("/GetStopsByLatLon?key=" + token)
    void getStopsByLatLon(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("count") int count,
            Callback<StopsByLatLonResponse> callback
    );

    @GET("/GetDeparturesByStop?key=" + token)
    void getDeparturesByStop(
            @Query("stop_id") String stopId,
            Callback<DeparturesByStopResponse> callback
    );

    @GET("/GetStopTimesByTrip?key=" + token)
    void getStopTimesByTrip(
            @Query("trip_id") String tripId,
            Callback<StopTimesByTripResponse> callback
    );

    @GET("/GetShape?key=" + token)
    void getShape(
            @Query("shape_id") String shapeId,
            Callback<ShapeResponse> callback
    );

    @GET("/GetVehiclesByRoute?key=" + token)
    void getVehiclesByRoute(
            @Query("route_id") String routeId,
            Callback<VehiclesByRouteResponse> callback
    );
}