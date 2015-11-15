package br.ufrj.caronae;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;

public class UnsubGcmTopic extends AsyncTask<Void, Void, Void> {
    private final String dbId;
    private final GcmPubSub gcmPubSub;

    public UnsubGcmTopic(Context activity, String dbId) {
        this.dbId = dbId;
        gcmPubSub = GcmPubSub.getInstance(activity);
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            gcmPubSub.unsubscribe(App.getUserGcmToken(), "/topics/" + dbId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
