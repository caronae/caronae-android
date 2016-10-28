package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class ActiveRide extends SugarRecord {

    private int dbId;
    private boolean going;
    private String date;

    public ActiveRide(int dbId, boolean going, String date) {
        this.dbId = dbId;
        this.going = going;
        this.date = date;
    }

    public ActiveRide() {
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
