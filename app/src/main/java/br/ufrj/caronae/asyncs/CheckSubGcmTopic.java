package br.ufrj.caronae.asyncs;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.models.ActiveRideId;

public class CheckSubGcmTopic extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... arg0) {
        String rideId = arg0[0];
        List<ActiveRideId> activeRideId = ActiveRideId.find(ActiveRideId.class, "ride_id = ?", rideId);
        if (activeRideId == null || activeRideId.isEmpty()) {
            try {
                Log.i("CheckSubGcmTopic", "i'll subscribe to ride " + rideId);

                GcmPubSub.getInstance(App.inst()).subscribe(SharedPref.getUserGcmToken(), "/topics/" + rideId, null);
                new ActiveRideId(rideId).save();

                Log.i("CheckSubGcmTopic", "subscribed to ride " + rideId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("CheckSubGcmTopic", "ALREADY subscribed to ride " + rideId);
        }

        return null;
    }
}
