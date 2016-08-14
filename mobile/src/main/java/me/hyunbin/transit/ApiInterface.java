package me.hyunbin.transit;

import me.hyunbin.transit.models.DeparturesByStopResponse;
import me.hyunbin.transit.models.GetPlannedTripsByLatLonResponse;
import me.hyunbin.transit.models.ShapeResponse;
import me.hyunbin.transit.models.StopTimesByTripResponse;
import me.hyunbin.transit.models.StopsByLatLonResponse;
import me.hyunbin.transit.models.VehiclesByRouteResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

  String token = MTD.apiKey;

  @GET("GetStopsByLatLon?key=" + token)
  Call<StopsByLatLonResponse> getStopsByLatLon(
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

  @GET("GetPlannedTripsByLatLon?key=" + token)
  Call<GetPlannedTripsByLatLonResponse> getPlannedTripsByLatLon(
      @Query("origin_lat") Double origin_lat,
      @Query("origin_lon") Double origin_lon,
      @Query("destination_lat") Double destination_lat,
      @Query("destination_lon") Double destination_lon
  );

  @GET("GetPlannedTripsByLatLon?key=" + token)
  Call<GetPlannedTripsByLatLonResponse> getPlannedTripsByLatLon(
      @Query("origin_lat") Double origin_lat,
      @Query("origin_lon") Double origin_lon,
      @Query("destination_lat") Double destination_lat,
      @Query("destination_lon") Double destination_lon,
      @Query("time") String time
  );
}
