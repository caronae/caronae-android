package br.ufrj.caronae.models.modelsforjson;

import java.util.List;

import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;

public class RideWithUsersForJson {
    private Ride ride;
    private List<User> users;

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
