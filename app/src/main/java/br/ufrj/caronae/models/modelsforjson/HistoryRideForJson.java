package br.ufrj.caronae.models.modelsforjson;

import br.ufrj.caronae.models.Ride;

public class HistoryRideForJson {
    private Ride ride;
    private String driverPic;
    private int ridersCount;

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public String getDriverPic() {
        return driverPic;
    }

    public void setDriverPic(String driverPic) {
        this.driverPic = driverPic;
    }

    public int getRidersCount() {
        return ridersCount;
    }

    public void setRidersCount(int ridersCount) {
        this.ridersCount = ridersCount;
    }
}
