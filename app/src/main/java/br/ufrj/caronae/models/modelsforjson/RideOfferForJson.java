package br.ufrj.caronae.models.modelsforjson;

import com.google.gson.annotations.SerializedName;

import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;

public class RideOfferForJson {
    private String driverName;
    @SerializedName("mytime")
    private String time;
    @SerializedName("mydate")
    private String date;
    private String course;
    private String place;
    private String route;
    private String slots;
    private String description;
    private String neighborhood;
    @SerializedName("myzone")
    private String zone;
    private String hub;
    private String profilePicUrl;
    private int rideId;
    private int driverId;
    private boolean going;

    public RideOfferForJson(Ride ride, User user) {
        driverName = user.getName();
        time = ride.getTime();
        date = ride.getDate();
        course = user.getCourse();
        place = ride.getPlace();
        route = ride.getRoute();
        slots = ride.getSlots();
        description = ride.getDescription();
        neighborhood = ride.getNeighborhood();
        zone = ride.getZone();
        hub = ride.getHub();
        going = ride.isGoing();
        profilePicUrl = user.getProfilePicUrl();
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
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

    public boolean isGoing() {
        return going;
    }

    public void setGoing(boolean going) {
        this.going = going;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
