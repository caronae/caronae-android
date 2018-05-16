package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class RideRequestSent extends SugarRecord {

    private int dbId;

    public RideRequestSent(int dbId, boolean going, String date) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }
}
