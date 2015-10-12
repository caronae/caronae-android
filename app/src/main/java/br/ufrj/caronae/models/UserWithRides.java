package br.ufrj.caronae.models;

import java.util.List;

public class UserWithRides {
    User user;
    List<Ride> rides;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }
}
