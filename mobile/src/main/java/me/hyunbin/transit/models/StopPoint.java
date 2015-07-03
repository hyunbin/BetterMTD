package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopPoint {

    @Expose
    private String code;
    @SerializedName("stop_id")
    @Expose
    private String stopId;
    @SerializedName("stop_lat")
    @Expose
    private float stopLat;
    @SerializedName("stop_lon")
    @Expose
    private float stopLon;
    @SerializedName("stop_name")
    @Expose
    private String stopName;

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
     * The stopLat
     */
    public float getStopLat() {
        return stopLat;
    }

    /**
     *
     * @param stopLat
     * The stop_lat
     */
    public void setStopLat(float stopLat) {
        this.stopLat = stopLat;
    }

    /**
     *
     * @return
     * The stopLon
     */
    public float getStopLon() {
        return stopLon;
    }

    /**
     *
     * @param stopLon
     * The stop_lon
     */
    public void setStopLon(float stopLon) {
        this.stopLon = stopLon;
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

}