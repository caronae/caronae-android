package br.ufrj.caronae.models;

import java.util.List;

public class RideWithUsers {
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
