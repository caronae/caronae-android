package br.ufrj.caronae.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import br.ufrj.caronae.models.ActiveRideId;

public class FirebaseTopicsHandler {

    public static void subscribeFirebaseTopic(String rideId) {

        List<ActiveRideId> activeRideId = ActiveRideId.find(ActiveRideId.class, "ride_id = ?", rideId);
        if (activeRideId == null || activeRideId.isEmpty()) {
            Log.i("CheckSubscribeFBTopic", "I'll subscribe to ride " + rideId);

            FirebaseMessaging.getInstance().subscribeToTopic(rideId);
            new ActiveRideId(rideId).save();

            Log.i("CheckSubscribeFBTopic", "subscribed to ride " + rideId);
        } else {
            Log.i("CheckSubscribeFBTopic", "ALREADY subscribed to ride " + rideId);
        }
    }

    public static void unsubscribeFirebaseTopic(String dbId) {

        FirebaseMessaging.getInstance().unsubscribeFromTopic(dbId);

        ActiveRideId.deleteAll(ActiveRideId.class, "ride_id = ?", dbId);
    }

    public static void CheckSubFirebaseTopic(String dbId) {

            List<ActiveRideId> activeRideId = ActiveRideId.find(ActiveRideId.class, "ride_id = ?", dbId);
            if (activeRideId == null || activeRideId.isEmpty()) {
                    Log.i("CheckSubGcmTopic", "i'll subscribe to ride " + dbId);

                    subscribeFirebaseTopic(dbId);
                    new ActiveRideId(dbId).save();

                    Log.i("CheckSubGcmTopic", "subscribed to ride " + dbId);
            } else {
                Log.i("CheckSubGcmTopic", "ALREADY subscribed to ride " + dbId);
            }
        }
}
