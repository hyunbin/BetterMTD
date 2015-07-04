
package me.hyunbin.transit.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeparturesByStopResponse {

    @Expose
    private String time;
    @SerializedName("new_changeset")
    @Expose
    private boolean newChangeset;
    @Expose
    private List<Departure> departures = new ArrayList<Departure>();

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
     * The departures
     */
    public List<Departure> getDepartures() {
        return departures;
    }

    /**
     *
     * @param departures
     * The departures
     */
    public void setDepartures(List<Departure> departures) {
        this.departures = departures;
    }

}