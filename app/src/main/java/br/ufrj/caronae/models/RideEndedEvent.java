package br.ufrj.caronae.models;

public class RideEndedEvent {
    private String rideId;

    public RideEndedEvent(String rideId) {
        this.rideId = rideId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }
}
