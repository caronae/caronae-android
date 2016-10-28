package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class ChatAssets extends SugarRecord{
    private String rideId;
    private String location;
    private int color;
    private int bgres;
    private String date;
    private String time;

    public ChatAssets() {
    }

    public ChatAssets(String rideId, String location, int color, int bgres, String date, String time) {
        this.rideId = rideId;
        this.location = location;
        this.color = color;
        this.bgres = bgres;
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

    public int getBgRes() {
        return bgres;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
