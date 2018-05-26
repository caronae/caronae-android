package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class RideRequestReceived extends SugarRecord {

    private int dbId;

    public RideRequestReceived(int dbId) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }
}
