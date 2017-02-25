package me.hyunbin.transit.helpers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import me.hyunbin.transit.models.Stop;
import me.hyunbin.transit.models.StopPoint;

/**
 * Created by Hyunbin on 2/24/17.
 */

public class MapHelper {
  public static void populateMapWithStopMarkers(GoogleMap map, List<Stop> stops) {
    map.clear();
    for (Stop stop : stops) {
      LatLng latLng = getAverageLatLngForStopPoints(stop.getStopPoints());
      map.addMarker(new MarkerOptions()
          .position(latLng)
          .title(stop.getStopName()));
    }
  }

  private static LatLng getAverageLatLngForStopPoints(List<StopPoint> stopPoints) {
    float lat = 0.0f;
    float lng = 0.0f;
    for (StopPoint point : stopPoints) {
      lat += point.getStopLat();
      lng += point.getStopLon();
    }
    lat /= stopPoints.size();
    lng /= stopPoints.size();
    return new LatLng(lat, lng);
  }
}
