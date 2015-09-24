package br.ufrj.caronae.models;

public class RideOffer {
    private String driverName;
    private String time;
    private String course;
    private String place;
    private String way;
    private String slots;
    private String description;
    private String neighborhood;
    private String rideId;
    private String driverId;
    private boolean go;

    public RideOffer(Ride lastRide, User user) {
        driverName = user.getName();
        time = lastRide.getTime();
        course = user.getCourse();
        place = lastRide.getPlace();
        way = lastRide.getWay();
        slots = lastRide.getSlots();
        description = lastRide.getDescription();
        neighborhood = lastRide.getNeighborhood();
        go = lastRide.isGo();
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public boolean isGo() {
        return go;
    }

    public void setGo(boolean go) {
        this.go = go;
    }
}
