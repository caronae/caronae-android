package br.ufrj.caronae.gcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(MyFirebaseInstanceIdService.class.getName(), "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        //        sendRegistrationToServer(refreshedToken);
    }

//    @Override
//    public void onTokenRefresh() {
//        // Fetch updated Instance ID token and notify of changes
        // TODO: Check RegistrationIntentService
//        Intent intent = new Intent(this, RegistrationIntentService.class);
//        startService(intent);
//    }
}
