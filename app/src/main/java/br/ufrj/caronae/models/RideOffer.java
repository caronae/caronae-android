package br.ufrj.caronae.models;

public class RideOffer {
    private String driverName;
    private String time;
    private String course;
    private String place;
    private String route;
    private String slots;
    private String description;
    private String neighborhood;
    private String hub;
    private int rideId;
    private int driverId;
    private boolean go;

    public RideOffer(Ride ride, User user) {
        driverName = user.getName();
        time = ride.getTime();
        course = user.getCourse();
        place = ride.getPlace();
        route = ride.getRoute();
        slots = ride.getSlots();
        description = ride.getDescription();
        neighborhood = ride.getNeighborhood();
        hub = ride.getHub();
        go = ride.isGoing();
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    public String getHub() {
        return hub;
    }

    public void setHub(String hub) {
        this.hub = hub;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public boolean isGo() {
        return go;
    }

    public void setGo(boolean go) {
        this.go = go;
    }
}
