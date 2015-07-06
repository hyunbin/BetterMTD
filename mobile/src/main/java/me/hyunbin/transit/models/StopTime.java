package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopTime {

    @SerializedName("arrival_time")
    @Expose
    private String arrivalTime;
    @SerializedName("departure_time")
    @Expose
    private String departureTime;
    @SerializedName("stop_sequence")
    @Expose
    private String stopSequence;
    @SerializedName("stop_point")
    @Expose
    private StopPoint stopPoint;

    /**
     *
     * @return
     * The arrivalTime
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     *
     * @param arrivalTime
     * The arrival_time
     */
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     *
     * @return
     * The departureTime
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     *
     * @param departureTime
     * The departure_time
     */
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    /**
     *
     * @return
     * The stopSequence
     */
    public String getStopSequence() {
        return stopSequence;
    }

    /**
     *
     * @param stopSequence
     * The stop_sequence
     */
    public void setStopSequence(String stopSequence) {
        this.stopSequence = stopSequence;
    }

    /**
     *
     * @return
     * The stopPoint
     */
    public StopPoint getStopPoint() {
        return stopPoint;
    }

    /**
     *
     * @param stopPoint
     * The stop_point
     */
    public void setStopPoint(StopPoint stopPoint) {
        this.stopPoint = stopPoint;
    }

}