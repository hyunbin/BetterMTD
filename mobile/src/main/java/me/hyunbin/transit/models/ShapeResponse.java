package me.hyunbin.transit.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShapeResponse {

    @Expose
    private String time;
    @SerializedName("changeset_id")
    @Expose
    private String changesetId;
    @SerializedName("new_changeset")
    @Expose
    private Boolean newChangeset;
    @Expose
    private List<Shape> shapes = new ArrayList<Shape>();

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
     * The shapes
     */
    public List<Shape> getShapes() {
        return shapes;
    }

    /**
     *
     * @param shapes
     * The shapes
     */
    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
    }

}