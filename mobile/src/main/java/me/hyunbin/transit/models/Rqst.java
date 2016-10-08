
package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rqst {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("params")
    @Expose
    private GetPlannedTripsByLatLonParams params;

    /**
     * 
     * @return
     *     The method
     */
    public String getMethod() {
        return method;
    }

    /**
     * 
     * @param method
     *     The method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 
     * @return
     *     The params
     */
    public GetPlannedTripsByLatLonParams getParams() {
        return params;
    }

    /**
     * 
     * @param params
     *     The params
     */
    public void setParams(GetPlannedTripsByLatLonParams params) {
        this.params = params;
    }

}
