package br.ufrj.caronae.asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.models.ActiveRideId;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRequestReceived;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.TokenForJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogOut extends AsyncTask<Void, Void, Void> {

    Context context;

    public LogOut(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        //Unsubscribe from lists
        FirebaseMessaging.getInstance().unsubscribeFromTopic(SharedPref.TOPIC_GERAL);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(App.getUser().getDbId() + "");

        App.getNetworkService(context).saveGcmToken(new TokenForJson(""))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            Log.i("saveGcmToken", "gcm token cleared");
                        } else {
                            Log.e("saveGcmToken", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("saveGcmToken", t.getMessage());
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
        ActiveRide.deleteAll(ActiveRide.class);
        RideRequestReceived.deleteAll(RideRequestReceived.class);
        ActiveRideId.deleteAll(ActiveRideId.class);
        ChatAssets.deleteAll(ChatAssets.class);

        return null;
    }
}