
package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Walk {

    @SerializedName("begin")
    @Expose
    private Begin begin;
    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("end")
    @Expose
    private End end;

    /**
     * 
     * @return
     *     The begin
     */
    public Begin getBegin() {
        return begin;
    }

    /**
     * 
     * @param begin
     *     The begin
     */
    public void setBegin(Begin begin) {
        this.begin = begin;
    }

    /**
     * 
     * @return
     *     The direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * 
     * @param direction
     *     The direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * 
     * @return
     *     The distance
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * 
     * @param distance
     *     The distance
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    /**
     * 
     * @return
     *     The end
     */
    public End getEnd() {
        return end;
    }

    /**
     * 
     * @param end
     *     The end
     */
    public void setEnd(End end) {
        this.end = end;
    }

}
