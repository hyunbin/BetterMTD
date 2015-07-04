package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trip {

    @SerializedName("trip_id")
    @Expose
    private String tripId;
    @SerializedName("trip_headsign")
    @Expose
    private String tripHeadsign;
    @SerializedName("route_id")
    @Expose
    private String routeId;
    @SerializedName("block_id")
    @Expose
    private String blockId;
    @Expose
    private String direction;
    @SerializedName("service_id")
    @Expose
    private String serviceId;
    @SerializedName("shape_id")
    @Expose
    private String shapeId;

    /**
     *
     * @return
     * The tripId
     */
    public String getTripId() {
        return tripId;
    }

    /**
     *
     * @param tripId
     * The trip_id
     */
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    /**
     *
     * @return
     * The tripHeadsign
     */
    public String getTripHeadsign() {
        return tripHeadsign;
    }

    /**
     *
     * @param tripHeadsign
     * The trip_headsign
     */
    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
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
     * The blockId
     */
    public String getBlockId() {
        return blockId;
    }

    /**
     *
     * @param blockId
     * The block_id
     */
    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    /**
     *
     * @return
     * The direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     *
     * @param direction
     * The direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     *
     * @return
     * The serviceId
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     *
     * @param serviceId
     * The service_id
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     *
     * @return
     * The shapeId
     */
    public String getShapeId() {
        return shapeId;
    }

    /**
     *
     * @param shapeId
     * The shape_id
     */
    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

}