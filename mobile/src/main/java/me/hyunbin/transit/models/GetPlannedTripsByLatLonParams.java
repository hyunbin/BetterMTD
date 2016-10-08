
package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPlannedTripsByLatLonParams {

    @SerializedName("destination_lat")
    @Expose
    private Double destinationLat;
    @SerializedName("destination_lon")
    @Expose
    private Double destinationLon;
    @SerializedName("origin_lat")
    @Expose
    private Double originLat;
    @SerializedName("origin_lon")
    @Expose
    private Double originLon;
    @SerializedName("time")
    @Expose
    private String time;

    /**
     * 
     * @return
     *     The destinationLat
     */
    public Double getDestinationLat() {
        return destinationLat;
    }

    /**
     * 
     * @param destinationLat
     *     The destination_lat
     */
    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    /**
     * 
     * @return
     *     The destinationLon
     */
    public Double getDestinationLon() {
        return destinationLon;
    }

    /**
     * 
     * @param destinationLon
     *     The destination_lon
     */
    public void setDestinationLon(Double destinationLon) {
        this.destinationLon = destinationLon;
    }

    /**
     * 
     * @return
     *     The originLat
     */
    public Double getOriginLat() {
        return originLat;
    }

    /**
     * 
     * @param originLat
     *     The origin_lat
     */
    public void setOriginLat(Double originLat) {
        this.originLat = originLat;
    }

    /**
     * 
     * @return
     *     The originLon
     */
    public Double getOriginLon() {
        return originLon;
    }

    /**
     * 
     * @param originLon
     *     The origin_lon
     */
    public void setOriginLon(Double originLon) {
        this.originLon = originLon;
    }

    /**
     * 
     * @return
     *     The time
     */
    public String getTime() {
        return time;
    }

    /**
     * 
     * @param time
     *     The time
     */
    public void setTime(String time) {
        this.time = time;
    }

}
