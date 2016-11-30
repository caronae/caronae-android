package br.ufrj.caronae.gcm;

import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.acts.ActiveRideAct;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.ActiveRideId;

/**
 * Created by Luis-DELL on 10/28/2016.
 */
public class FirebaseTopicsHandler {

    public static void subscribeToTopic(String rideId) {

        List<ActiveRideId> activeRideId = ActiveRideId.find(ActiveRideId.class, "ride_id = ?", rideId);
        if (activeRideId == null || activeRideId.isEmpty()) {
            Log.i("CheckSubscribeFBTopic", "I'll subscribe to ride " + rideId);

            FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + rideId);
            new ActiveRideId(rideId).save();

            Log.i("CheckSubscribeFBTopic", "subscribed to ride " + rideId);
        } else {
            Log.i("CheckSubscribeFBTopic", "ALREADY subscribed to ride " + rideId);
        }
    }

    public static void unsubscribeToTopic(String dbId) {

        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/" + dbId);

        ActiveRideId.deleteAll(ActiveRideId.class, "ride_id = ?", dbId);
    }
}
