package me.hyunbin.transit.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopTimesByTripResponse {

    @Expose
    private String time;
    @SerializedName("changeset_id")
    @Expose
    private String changesetId;
    @SerializedName("new_changeset")
    @Expose
    private boolean newChangeset;
    @SerializedName("stop_times")
    @Expose
    private List<StopTime> stopTimes = new ArrayList<StopTime>();

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
     * The stopTimes
     */
    public List<StopTime> getStopTimes() {
        return stopTimes;
    }

    /**
     *
     * @param stopTimes
     * The stop_times
     */
    public void setStopTimes(List<StopTime> stopTimes) {
        this.stopTimes = stopTimes;
    }

}