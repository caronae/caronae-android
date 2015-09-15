package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class Ride extends SugarRecord<Ride> {
    private String origin;
    private String destination;
    private String date;
    private String time;
    private String slots;
    private String hub;
    private String description;
    private boolean go;

    public Ride() {
    }

    public Ride(String origin, String destination, String date, String time, String slots, String hub, String description, boolean go) {
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.slots = slots;
        this.hub = hub;
        this.description = description;
        this.go = go;
    }
}
