package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shape {

    @SerializedName("shape_dist_traveled")
    @Expose
    private Double shapeDistTraveled;
    @SerializedName("shape_pt_lat")
    @Expose
    private Double shapePtLat;
    @SerializedName("shape_pt_lon")
    @Expose
    private Double shapePtLon;
    @SerializedName("shape_pt_sequence")
    @Expose
    private Integer shapePtSequence;
    @SerializedName("stop_id")
    @Expose
    private String stopId;

    /**
     *
     * @return
     * The shapeDistTraveled
     */
    public Double getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    /**
     *
     * @param shapeDistTraveled
     * The shape_dist_traveled
     */
    public void setShapeDistTraveled(Double shapeDistTraveled) {
        this.shapeDistTraveled = shapeDistTraveled;
    }

    /**
     *
     * @return
     * The shapePtLat
     */
    public Double getShapePtLat() {
        return shapePtLat;
    }

    /**
     *
     * @param shapePtLat
     * The shape_pt_lat
     */
    public void setShapePtLat(Double shapePtLat) {
        this.shapePtLat = shapePtLat;
    }

    /**
     *
     * @return
     * The shapePtLon
     */
    public Double getShapePtLon() {
        return shapePtLon;
    }

    /**
     *
     * @param shapePtLon
     * The shape_pt_lon
     */
    public void setShapePtLon(Double shapePtLon) {
        this.shapePtLon = shapePtLon;
    }

    /**
     *
     * @return
     * The shapePtSequence
     */
    public Integer getShapePtSequence() {
        return shapePtSequence;
    }

    /**
     *
     * @param shapePtSequence
     * The shape_pt_sequence
     */
    public void setShapePtSequence(Integer shapePtSequence) {
        this.shapePtSequence = shapePtSequence;
    }

    /**
     *
     * @return
     * The stopId
     */
    public String getStopId() {
        return stopId;
    }

    /**
     *
     * @param stopId
     * The stop_id
     */
    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

}