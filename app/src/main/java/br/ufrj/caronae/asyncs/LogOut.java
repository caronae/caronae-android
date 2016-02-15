package br.ufrj.caronae.asyncs;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.models.ActiveRideId;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRequestReceived;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.TokenForJson;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LogOut extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
        App.getNetworkService().saveGcmToken(new TokenForJson(""), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i("saveGcmToken", "gcm token cleared");
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    Log.e("saveGcmToken", error.getMessage());
                } catch (Exception e) {//sometimes RetrofitError is null
                    Log.e("saveGcmToken", e.getMessage());
                }
            }
        });
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        LoginManager.getInstance().logOut();

        SharedPref.removeAllPrefButGcm();
        App.clearUserVar();

        List<ActiveRideId> activeRideIds = ActiveRideId.listAll(ActiveRideId.class);
        if (!activeRideIds.isEmpty()) {
            GcmPubSub pubSub = GcmPubSub.getInstance(App.inst());
            for (ActiveRideId ari : activeRideIds) {
                try {
                    pubSub.unsubscribe(SharedPref.getUserGcmToken(), "/topics/" + ari.getRideId());

                    Log.i("logOut", "unsubscribed from ride " + ari.getRideId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Ride.deleteAll(Ride.class);
        RideRequestSent.deleteAll(RideRequestSent.class);
        RideRequestReceived.deleteAll(RideRequestReceived.class);
        ActiveRideId.deleteAll(ActiveRideId.class);
        //ChatMessageReceived.deleteAll(ChatMessageReceived.class);
        ChatAssets.deleteAll(ChatAssets.class);

        return null;
    }
}