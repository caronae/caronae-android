package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import br.ufrj.caronae.Util;

public class RideHistory extends SugarRecord{

    @SerializedName("neighborhood")
    private String neighborhood;
    @SerializedName("driver")
    private User driver;
    @SerializedName("going")
    private boolean going;
    @SerializedName("done")
    private boolean done;
    @SerializedName("mydate")
    private String date;
    @SerializedName("hub")
    private String hub;
    @SerializedName("myzone")
    private String zone;
    @SerializedName("mytime")
    private String time;

    public String getNeighborhood() {
        return neighborhood;
    }

    public User getDriver() {
        return driver;
    }

    public boolean isGoing() {
        return going;
    }

    public boolean isDone() {
        return done;
    }

    public String getDate() {
        return date;
    }

    public String getHub() {
        return hub;
    }

    public String getZone() {
        return zone;
    }

    public String getTime() {
        return time;
    }
}
