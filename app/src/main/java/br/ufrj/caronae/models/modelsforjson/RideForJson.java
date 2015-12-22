package br.ufrj.caronae.models.modelsforjson;

import java.util.List;

import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;

public class RideForJson extends Ride {
    private User driver;
    private List<User> riders;

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public List<User> getRiders() {
        return riders;
    }

    public void setRiders(List<User> riders) {
        this.riders = riders;
    }
}
