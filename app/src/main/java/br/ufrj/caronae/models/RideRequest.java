package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class RideRequest extends SugarRecord<RideRequest> {

    private int dbId;
    private boolean going;
    private String date;

    public RideRequest(int dbId, boolean going, String date) {
        this.dbId = dbId;
        this.going = going;
        this.date = date;
    }

    public RideRequest() {
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
