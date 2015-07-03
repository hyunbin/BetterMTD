
package me.hyunbin.transit.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stop {

    @SerializedName("stop_id")
    @Expose
    private String stopId;
    @SerializedName("stop_name")
    @Expose
    private String stopName;
    @Expose
    private String code;
    @Expose
    private float distance;
    @SerializedName("stop_points")
    @Expose
    private List<StopPoint> stopPoints = new ArrayList<StopPoint>();

    /**
     *
     * @return
     * The stopId
     */
    public String getStopId() {
        return stopId;
    }

    /**
     *
     * @param stopId
     * The stop_id
     */
    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    /**
     *
     * @return
     * The stopName
     */
    public String getStopName() {
        return stopName;
    }

    /**
     *
     * @param stopName
     * The stop_name
     */
    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    /**
     *
     * @return
     * The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The distance
     */
    public float getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The stopPoints
     */
    public List<StopPoint> getStopPoints() {
        return stopPoints;
    }

    /**
     *
     * @param stopPoints
     * The stop_points
     */
    public void setStopPoints(List<StopPoint> stopPoints) {
        this.stopPoints = stopPoints;
    }

}