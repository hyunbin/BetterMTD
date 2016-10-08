package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Departure {

    @SerializedName("stop_id")
    @Expose
    private String stopId;
    @Expose
    private String headsign;
    @Expose
    private Route route;
    @Expose
    private Trip trip;
    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @Expose
    private Origin origin;
    @Expose
    private Destination destination;
    @SerializedName("is_monitored")
    @Expose
    private boolean isMonitored;
    @SerializedName("is_scheduled")
    @Expose
    private boolean isScheduled;
    @SerializedName("is_istop")
    @Expose
    private boolean isIstop;
    @Expose
    private String scheduled;
    @Expose
    private String expected;
    @SerializedName("expected_mins")
    @Expose
    private int expectedMins;
    @Expose
    private Location location;

    public long getUniqueId(){
        long id = getHeadsign().hashCode() * 10000;
        String subId = getVehicleId();
        if(subId != null && !subId.contentEquals("null")) {
            id = id + Long.parseLong(subId);
        }
        return id;
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
     * The headsign
     */
    public String getHeadsign() {
        return headsign;
    }

    /**
     *
     * @param headsign
     * The headsign
     */
    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    /**
     *
     * @return
     * The route
     */
    public Route getRoute() {
        return route;
    }

    /**
     *
     * @param route
     * The route
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     *
     * @return
     * The trip
     */
    public Trip getTrip() {
        return trip;
    }

    /**
     *
     * @param trip
     * The trip
     */
    public void setTrip(Trip trip) {
        this.trip = trip;
    }

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
     * The origin
     */
    public Origin getOrigin() {
        return origin;
    }

    /**
     *
     * @param origin
     * The origin
     */
    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    /**
     *
     * @return
     * The destination
     */
    public Destination getDestination() {
        return destination;
    }

    /**
     *
     * @param destination
     * The destination
     */
    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    /**
     *
     * @return
     * The isMonitored
     */
    public boolean isIsMonitored() {
        return isMonitored;
    }

    /**
     *
     * @param isMonitored
     * The is_monitored
     */
    public void setIsMonitored(boolean isMonitored) {
        this.isMonitored = isMonitored;
    }

    /**
     *
     * @return
     * The isScheduled
     */
    public boolean isIsScheduled() {
        return isScheduled;
    }

    /**
     *
     * @param isScheduled
     * The is_scheduled
     */
    public void setIsScheduled(boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    /**
     *
     * @return
     * The isIstop
     */
    public boolean isIsIstop() {
        return isIstop;
    }

    /**
     *
     * @param isIstop
     * The is_istop
     */
    public void setIsIstop(boolean isIstop) {
        this.isIstop = isIstop;
    }

    /**
     *
     * @return
     * The scheduled
     */
    public String getScheduled() {
        return scheduled;
    }

    /**
     *
     * @param scheduled
     * The scheduled
     */
    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    /**
     *
     * @return
     * The expected
     */
    public String getExpected() {
        return expected;
    }

    /**
     *
     * @param expected
     * The expected
     */
    public void setExpected(String expected) {
        this.expected = expected;
    }

    /**
     *
     * @return
     * The expectedMins
     */
    public int getExpectedMins() {
        return expectedMins;
    }

    /**
     *
     * @param expectedMins
     * The expected_mins
     */
    public void setExpectedMins(int expectedMins) {
        this.expectedMins = expectedMins;
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

}
