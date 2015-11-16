package br.ufrj.caronae;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;
import java.util.List;

import br.ufrj.caronae.models.ActiveRideId;

public class CheckAndSubscribeToTopic extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... arg0) {
        String rideId = arg0[0];
        List<ActiveRideId> activeRideId = ActiveRideId.find(ActiveRideId.class, "ride_id = ?", rideId);
        if (activeRideId == null || activeRideId.isEmpty()) {
            try {
                Log.i("CheckAndSubscribeToTopi", "i'll subscribe to ride " + rideId);
                GcmPubSub.getInstance(App.inst()).subscribe(App.getUserGcmToken(), "/topics/" + rideId, null);
                new ActiveRideId(rideId).save();
                Log.i("CheckAndSubscribeToTopi", "subscribed to ride " + rideId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("CheckAndSubscribeToTopi", "ALREADY subscribed to ride " + rideId);
        }

        return null;
    }
}
