package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;

public class Location {

    @Expose
    private float lat;
    @Expose
    private float lon;

    /**
     *
     * @return
     * The lat
     */
    public float getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(float lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The lon
     */
    public float getLon() {
        return lon;
    }

    /**
     *
     * @param lon
     * The lon
     */
    public void setLon(float lon) {
        this.lon = lon;
    }

}