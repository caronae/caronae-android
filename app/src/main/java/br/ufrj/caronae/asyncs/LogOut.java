package br.ufrj.caronae.asyncs;

import android.content.Context;
import android.os.AsyncTask;

import com.facebook.login.LoginManager;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.ActiveRideId;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRequestSent;

public class LogOut extends AsyncTask<Void, Void, Void> {

    Context context;

    public LogOut(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        LoginManager.getInstance().logOut();

        SharedPref.removeAllPrefButGcm();
        App.clearUserVar();

        List<ActiveRideId> activeRideIds = ActiveRideId.listAll(ActiveRideId.class);
        if (!activeRideIds.isEmpty()) {
            for (ActiveRideId ari : activeRideIds) {
                FirebaseTopicsHandler.unsubscribeFirebaseTopic(ari.getRideId());
            }
        }

        Ride.deleteAll(Ride.class);
        RideRequestSent.deleteAll(RideRequestSent.class);
        ActiveRideId.deleteAll(ActiveRideId.class);
        ChatAssets.deleteAll(ChatAssets.class);

        return null;
    }
}