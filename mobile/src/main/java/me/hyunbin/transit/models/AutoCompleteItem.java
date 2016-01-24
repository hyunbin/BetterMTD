package me.hyunbin.transit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AutoCompleteItem {

    @SerializedName("c")
    @Expose
    private String c;
    @SerializedName("i")
    @Expose
    private String i;
    @SerializedName("n")
    @Expose
    private String n;

    /**
     *
     * @return
     * The c
     */
    public String getC() {
        return c;
    }

    /**
     *
     * @param c
     * The c
     */
    public void setC(String c) {
        this.c = c;
    }

    /**
     *
     * @return
     * The i
     */
    public String getI() {
        return i;
    }

    /**
     *
     * @param i
     * The i
     */
    public void setI(String i) {
        this.i = i;
    }

    /**
     *
     * @return
     * The n
     */
    public String getN() {
        return n;
    }

    /**
     *
     * @param n
     * The n
     */
    public void setN(String n) {
        this.n = n;
    }

}