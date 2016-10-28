package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class RideRequestReceived extends SugarRecord {

    private int dbId;

    public RideRequestReceived() {
    }

    public RideRequestReceived(int dbId) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
