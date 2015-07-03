package me.hyunbin.transit.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopsByLatLonResponse {

    @Expose
    private String time;
    @SerializedName("changeset_id")
    @Expose
    private String changesetId;
    @SerializedName("new_changeset")
    @Expose
    private boolean newChangeset;
    @Expose
    private List<Stop> stops = new ArrayList<Stop>();

    /**
     *
     * @return
     * The time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     *
     * @return
     * The changesetId
     */
    public String getChangesetId() {
        return changesetId;
    }

    /**
     *
     * @param changesetId
     * The changeset_id
     */
    public void setChangesetId(String changesetId) {
        this.changesetId = changesetId;
    }

    /**
     *
     * @return
     * The newChangeset
     */
    public boolean isNewChangeset() {
        return newChangeset;
    }

    /**
     *
     * @param newChangeset
     * The new_changeset
     */
    public void setNewChangeset(boolean newChangeset) {
        this.newChangeset = newChangeset;
    }

    /**
     *
     * @return
     * The stops
     */
    public List<Stop> getStops() {
        return stops;
    }

    /**
     *
     * @param stops
     * The stops
     */
    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

}