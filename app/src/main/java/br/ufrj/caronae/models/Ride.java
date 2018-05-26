package br.ufrj.caronae.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import br.ufrj.caronae.Util;

public class Ride extends SugarRecord{
    @SerializedName("myzone")
    protected String zone;
    protected String neighborhood;
    protected String place;
    protected String route;
    @SerializedName("mydate")
    protected String date;
    protected String slots;
    @SerializedName("mytime")
    protected String time;
    protected String hub;
    protected String description;
    @SerializedName("week_days")
    protected String weekDays;
    @SerializedName("repeats_until")
    protected String repeatsUntil;
    protected boolean going, routine;
    @SerializedName("dbId")
    protected int dbId;
    @SerializedName("routine_id")
    protected String routineId;
    protected String campus;

    public Ride() {
        // Required empty public constructor
    }

    public Ride(String zone, String neighborhood, String place, String route, String date, String time, String slots, String hub, String campus, String description, boolean going, boolean routine, String weekDays, String repeatsUntil) {
        this.zone = zone;
        this.neighborhood = neighborhood;
        this.place = place;
        this.route = route;
        this.date = date;
        this.time = time;
        this.slots = slots;
        this.hub = hub;
        this.description = description;
        this.going = going;
        this.routine = routine;
        this.weekDays = weekDays;
        this.repeatsUntil = repeatsUntil;
        this.campus = campus;
    }

    public Ride(Ride ride) {
        zone = ride.getZone();
        neighborhood = ride.getNeighborhood();
        place = ride.getPlace();
        route = ride.getRoute();
        date = Util.formatBadDateWithYear(ride.getDate());
        time = ride.getTime();
        slots = ride.getSlots();
        hub = ride.getHub();
        description = ride.getDescription();
        going = ride.isGoing();
        weekDays = ride.getWeekDays();
        routine = weekDays != null && !weekDays.isEmpty();
        repeatsUntil = ride.getRepeatsUntil();
        dbId = ride.getId().intValue();
        routineId = ride.getRoutineId();
        campus = ride.campus;
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

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
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

    public String getWeekDays() {
        return weekDays;
    }

    public String getRepeatsUntil() {
        return repeatsUntil;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getRoutineId() {
        return routineId;
    }

}
