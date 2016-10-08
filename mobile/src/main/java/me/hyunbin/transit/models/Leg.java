
package me.hyunbin.transit.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Leg {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("walk")
    @Expose
    private Walk walk;
    @SerializedName("services")
    @Expose
    private List<Service> services = new ArrayList<Service>();

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The walk
     */
    public Walk getWalk() {
        return walk;
    }

    /**
     * 
     * @param walk
     *     The walk
     */
    public void setWalk(Walk walk) {
        this.walk = walk;
    }

    /**
     * 
     * @return
     *     The services
     */
    public List<Service> getServices() {
        return services;
    }

    /**
     * 
     * @param services
     *     The services
     */
    public void setServices(List<Service> services) {
        this.services = services;
    }

}
