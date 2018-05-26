package br.ufrj.caronae.models;

import com.orm.SugarRecord;

public class NewChatMsgIndicator extends SugarRecord {

    private int dbId;

    public NewChatMsgIndicator() {
        // Required empty public constructor
    }

    public NewChatMsgIndicator(int dbId) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
