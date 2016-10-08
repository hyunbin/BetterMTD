
package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetPlannedTripsByLatLonResponse {

    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("new_changeset")
    @Expose
    private Boolean newChangeset;
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("rqst")
    @Expose
    private Rqst rqst;
    @SerializedName("itineraries")
    @Expose
    private List<Itinerary> itineraries = new ArrayList<Itinerary>();

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

    /**
     * 
     * @return
     *     The newChangeset
     */
    public Boolean getNewChangeset() {
        return newChangeset;
    }

    /**
     * 
     * @param newChangeset
     *     The new_changeset
     */
    public void setNewChangeset(Boolean newChangeset) {
        this.newChangeset = newChangeset;
    }

    /**
     * 
     * @return
     *     The status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The rqst
     */
    public Rqst getRqst() {
        return rqst;
    }

    /**
     * 
     * @param rqst
     *     The rqst
     */
    public void setRqst(Rqst rqst) {
        this.rqst = rqst;
    }

    /**
     * 
     * @return
     *     The itineraries
     */
    public List<Itinerary> getItineraries() {
        return itineraries;
    }

    /**
     * 
     * @param itineraries
     *     The itineraries
     */
    public void setItineraries(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }

}
