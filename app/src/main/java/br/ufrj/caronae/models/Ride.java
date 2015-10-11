package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class Ride extends SugarRecord<Ride> {
    @SerializedName("myzone")
    private String zone;
    private String neighborhood;
    private String reference;
    private String route;
    @SerializedName("mydate")
    private String date;
    @SerializedName("mytime")
    private String time;
    private String slots;
    private String hub;
    private String description;
    private boolean going, routine, monday, tuesday, wednesday, thursday, friday, saturday;

    public Ride() {
    }

    public Ride(String neighborhood, String reference, String route, String date, String time, String slots, String hub, String description, boolean going, boolean routine, boolean[] routineDays) {
        this.neighborhood = neighborhood;
        this.reference = reference;
        this.route = route;
        this.date = date;
        this.time = time;
        this.slots = slots;
        this.hub = hub;
        this.description = description;
        this.going = going;
        this.routine = routine;
        monday = routineDays[0];
        tuesday = routineDays[1];
        wednesday = routineDays[2];
        thursday = routineDays[3];
        friday = routineDays[4];
        saturday = routineDays[5];
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

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public String getHub() {
        return hub;
    }

    public void setHub(String hub) {
        this.hub = hub;
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

    public boolean isRoutine() {
        return routine;
    }

    public void setRoutine(boolean routine) {
        this.routine = routine;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }
}
