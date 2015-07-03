package me.hyunbin.transit;

import java.util.List;

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

    String token = "***REMOVED***";

    class NearbyStops{
        public final String stopId;
        public final String stopName;
        public final String distance;

        public NearbyStops(String stopId, String stopName, String distance){
            this.stopId = stopId;
            this.stopName = stopName;
            this.distance = distance;
        }
    }

    @GET("/GetStopsByLatLon?key=" + token)
    void getStopsByLatLon(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("count") int count,
            Callback<List<NearbyStops>> callback
    );
}
