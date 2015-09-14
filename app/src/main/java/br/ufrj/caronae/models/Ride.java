package br.ufrj.caronae.models;

public class Ride {
    private String from;
    private String to;
    private String date;
    private String time;
    private String slots;
    private String hub;
    private String description;

    public Ride(String from, String to, String date, String time, String slots, String hub, String description) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.slots = slots;
        this.hub = hub;
        this.description = description;
    }
}
