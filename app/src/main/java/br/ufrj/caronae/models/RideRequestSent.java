package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class RideRequestSent extends SugarRecord<RideRequestSent> {

    private int dbId;
    private boolean going;
    private String date;

    public RideRequestSent(int dbId, boolean going, String date) {
        this.dbId = dbId;
        this.going = going;
        this.date = date;
    }

    public RideRequestSent() {
    }

    public int getDbId() {
        return dbId;
    }

    public boolean isGoing() {
        return going;
    }

    public String getDate() {
        return date;
    }
}
