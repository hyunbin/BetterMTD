
package me.hyunbin.transit.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Itinerary {

    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("travel_time")
    @Expose
    private Integer travelTime;
    @SerializedName("legs")
    @Expose
    private List<Leg> legs = new ArrayList<Leg>();

    /**
     * 
     * @return
     *     The startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * 
     * @param startTime
     *     The start_time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * 
     * @return
     *     The endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * 
     * @param endTime
     *     The end_time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * 
     * @return
     *     The travelTime
     */
    public Integer getTravelTime() {
        return travelTime;
    }

    /**
     * 
     * @param travelTime
     *     The travel_time
     */
    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }

    /**
     * 
     * @return
     *     The legs
     */
    public List<Leg> getLegs() {
        return legs;
    }

    /**
     * 
     * @param legs
     *     The legs
     */
    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

}
