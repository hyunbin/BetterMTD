package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

    @SerializedName("route_color")
    @Expose
    private String routeColor;
    @SerializedName("route_id")
    @Expose
    private String routeId;
    @SerializedName("route_long_name")
    @Expose
    private String routeLongName;
    @SerializedName("route_short_name")
    @Expose
    private String routeShortName;
    @SerializedName("route_text_color")
    @Expose
    private String routeTextColor;

    /**
     *
     * @return
     * The routeColor
     */
    public String getRouteColor() {
        return routeColor;
    }

    /**
     *
     * @param routeColor
     * The route_color
     */
    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    /**
     *
     * @return
     * The routeId
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     *
     * @param routeId
     * The route_id
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    /**
     *
     * @return
     * The routeLongName
     */
    public String getRouteLongName() {
        return routeLongName;
    }

    /**
     *
     * @param routeLongName
     * The route_long_name
     */
    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    /**
     *
     * @return
     * The routeShortName
     */
    public String getRouteShortName() {
        return routeShortName;
    }

    /**
     *
     * @param routeShortName
     * The route_short_name
     */
    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    /**
     *
     * @return
     * The routeTextColor
     */
    public String getRouteTextColor() {
        return routeTextColor;
    }

    /**
     *
     * @param routeTextColor
     * The route_text_color
     */
    public void setRouteTextColor(String routeTextColor) {
        this.routeTextColor = routeTextColor;
    }

}