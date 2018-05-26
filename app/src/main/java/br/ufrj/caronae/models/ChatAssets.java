package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class ChatAssets extends SugarRecord{
    private String rideId;
    private String location;
    private int color;
    private String date;
    private String time;

    public ChatAssets(String rideId, String location, int color, String date, String time) {
        this.rideId = rideId;
        this.location = location;
        this.color = color;
        this.date = date;
        this.time = time;
    }

    public String getRideId() {
        return rideId;
    }

    public String getLocation() {
        return location;
    }

    public int getColor() {
        return color;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
