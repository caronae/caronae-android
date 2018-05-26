package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class RideOffer extends SugarRecord{
    @SerializedName("mytime")
    protected String time;
    @SerializedName("neighborhood")
    protected String neighborhood;
    @SerializedName("repeats_until")
    protected String repeatsUntil;
    @SerializedName("description")
    protected String description;
    @SerializedName("place")
    protected String place;
    @SerializedName("going")
    protected boolean going;
    @SerializedName("mydate")
    protected String date;
    @SerializedName("dbId")
    protected int dbId;
    @SerializedName("slots")
    protected int slots;
    @SerializedName("myzone")
    protected String zone;
    @SerializedName("week_days")
    protected String weekDays;
    @SerializedName("hub")
    protected String hub;
    @SerializedName("route")
    protected String route;

    public RideOffer(String time, String neighborhood, String repeatsUntil, String description, String place, boolean going,
                String date, int dbId, int slots, String zone, String weekDays, String hub, String route) {
        this.zone = zone;
        this.neighborhood = neighborhood;
        this.place = place;
        this.route = route;
        this.date = date;
        this.time = time;
        this.hub = hub;
        this.dbId = dbId;
        this.description = description;
        this.going = going;
        this.weekDays = weekDays;
        this.repeatsUntil = repeatsUntil;
        this.slots = slots;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getPlace() {
        return place;
    }

    public String getRoute() {
        return route;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getHub() {
        return hub;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGoing() {
        return going;
    }

    public void setGoing(boolean going) {
        this.going = going;
    }

    public int getDbId() {
        return dbId;
    }
}
