package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class Ride extends SugarRecord<Ride> {
    private String neighborhood;
    private String place;
    private String way;
    private String date;
    private String time;
    private String slots;
    private String hub;
    private String description;
    private boolean go, routine;
    private boolean[] routineDays;

    public Ride() {
    }

    public Ride(String neighborhood, String place, String way, String date, String time, String slots, String hub, String description, boolean go, boolean routine, boolean[] routineDays) {
        this.neighborhood = neighborhood;
        this.place = place;
        this.way = way;
        this.date = date;
        this.time = time;
        this.slots = slots;
        this.hub = hub;
        this.description = description;
        this.go = go;
        this.routine = routine;
        this.routineDays = routineDays;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
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

    public boolean isGo() {
        return go;
    }

    public void setGo(boolean go) {
        this.go = go;
    }

    public boolean isRoutine() {
        return routine;
    }

    public void setRoutine(boolean routine) {
        this.routine = routine;
    }

    public boolean[] getRoutineDays() {
        return routineDays;
    }

    public void setRoutineDays(boolean[] routineDays) {
        this.routineDays = routineDays;
    }
}
