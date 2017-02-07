package br.ufrj.caronae.firebase;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.models.modelsforjson.TokenForJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationIntentService extends IntentService {
    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + token);

            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            //sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private void sendRegistrationToServer(final String token) {
        // send network request
        App.getNetworkService(getApplicationContext()).saveGcmToken(new TokenForJson(token))
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, Response<Response> response) {
                        if (response.isSuccessful()) {
                            Log.i("saveGcmToken", "gcm token sent to server");

                            // save token
                            SharedPref.saveUserGcmToken(token);
                        } else {
                            Log.e("saveGcmToken", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Log.e("saveGcmToken", t.getMessage());
                    }
                });
        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
        //sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
    }
}
