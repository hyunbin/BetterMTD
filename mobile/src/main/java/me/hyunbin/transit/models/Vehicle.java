package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vehicle {

    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @Expose
    private Object trip;
    @Expose
    private Location location;
    @SerializedName("previous_stop_id")
    @Expose
    private String previousStopId;
    @SerializedName("next_stop_id")
    @Expose
    private String nextStopId;
    @SerializedName("origin_stop_id")
    @Expose
    private String originStopId;
    @SerializedName("destination_stop_id")
    @Expose
    private String destinationStopId;
    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;

    /**
     *
     * @return
     * The vehicleId
     */
    public String getVehicleId() {
        return vehicleId;
    }

    /**
     *
     * @param vehicleId
     * The vehicle_id
     */
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     *
     * @return
     * The trip
     */
    public Object getTrip() {
        return trip;
    }

    /**
     *
     * @param trip
     * The trip
     */
    public void setTrip(Object trip) {
        this.trip = trip;
    }

    /**
     *
     * @return
     * The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     *
     * @return
     * The previousStopId
     */
    public String getPreviousStopId() {
        return previousStopId;
    }

    /**
     *
     * @param previousStopId
     * The previous_stop_id
     */
    public void setPreviousStopId(String previousStopId) {
        this.previousStopId = previousStopId;
    }

    /**
     *
     * @return
     * The nextStopId
     */
    public String getNextStopId() {
        return nextStopId;
    }

    /**
     *
     * @param nextStopId
     * The next_stop_id
     */
    public void setNextStopId(String nextStopId) {
        this.nextStopId = nextStopId;
    }

    /**
     *
     * @return
     * The originStopId
     */
    public String getOriginStopId() {
        return originStopId;
    }

    /**
     *
     * @param originStopId
     * The origin_stop_id
     */
    public void setOriginStopId(String originStopId) {
        this.originStopId = originStopId;
    }

    /**
     *
     * @return
     * The destinationStopId
     */
    public String getDestinationStopId() {
        return destinationStopId;
    }

    /**
     *
     * @param destinationStopId
     * The destination_stop_id
     */
    public void setDestinationStopId(String destinationStopId) {
        this.destinationStopId = destinationStopId;
    }

    /**
     *
     * @return
     * The lastUpdated
     */
    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     *
     * @param lastUpdated
     * The last_updated
     */
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}