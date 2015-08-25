package me.hyunbin.transit.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehiclesByRouteResponse {

    @Expose
    private String time;
    @SerializedName("new_changeset")
    @Expose
    private Boolean newChangeset;
    @Expose
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();

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
    public Boolean getNewChangeset() {
        return newChangeset;
    }

    /**
     *
     * @param newChangeset
     * The new_changeset
     */
    public void setNewChangeset(Boolean newChangeset) {
        this.newChangeset = newChangeset;
    }

    /**
     *
     * @return
     * The vehicles
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     *
     * @param vehicles
     * The vehicles
     */
    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

}