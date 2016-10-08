
package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Service {

    @SerializedName("begin")
    @Expose
    private Begin begin;
    @SerializedName("end")
    @Expose
    private End end;
    @SerializedName("route")
    @Expose
    private Route route;
    @SerializedName("trip")
    @Expose
    private Trip trip;

    /**
     * 
     * @return
     *     The begin
     */
    public Begin getBegin() {
        return begin;
    }

    /**
     * 
     * @param begin
     *     The begin
     */
    public void setBegin(Begin begin) {
        this.begin = begin;
    }

    /**
     * 
     * @return
     *     The end
     */
    public End getEnd() {
        return end;
    }

    /**
     * 
     * @param end
     *     The end
     */
    public void setEnd(End end) {
        this.end = end;
    }

    /**
     * 
     * @return
     *     The route
     */
    public Route getRoute() {
        return route;
    }

    /**
     * 
     * @param route
     *     The route
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * 
     * @return
     *     The trip
     */
    public Trip getTrip() {
        return trip;
    }

    /**
     * 
     * @param trip
     *     The trip
     */
    public void setTrip(Trip trip) {
        this.trip = trip;
    }

}
