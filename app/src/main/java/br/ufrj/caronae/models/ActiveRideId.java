package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class ActiveRideId extends SugarRecord {
    private String rideId;

    public ActiveRideId() {
        // Required empty public constructor
    }

    public ActiveRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }
}
