package br.ufrj.caronae.asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.login.LoginManager;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.ActiveRideId;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRequestReceived;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.TokenForJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class LogOut extends AsyncTask<Void, Void, Void> {

    Context context;

    public LogOut(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        App.getNetworkService(context).saveGcmToken(new TokenForJson(""))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
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
            for (ActiveRideId ari : activeRideIds) {
                FirebaseTopicsHandler.unsubscribeFirebaseTopic(ari.getRideId());
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