package br.ufrj.caronae.models.modelsforjson;

import java.util.List;

import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;

public class UserWithRidesForJson {
    private User user;
    private List<Ride> rides;

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
