package br.ufrj.caronae.acts;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.ufrj.caronae.App;
import br.ufrj.caronae.asyncs.FetchMyOfferedRidesService;
import br.ufrj.caronae.models.RideRequestReceived;

public class StartAct extends AppCompatActivity {

    private static final String RIDE_ID_BUNDLE_KEY = "rideId";

    private static final String MSG_TYPE_BUNDLE_KEY = "msgType";

    public static final String MSG_TYPE_ALERT = "alert";

    public static final String ALERT_KEY = "message";

    public static final String MSG_TYPE_ALERT_HEADER = "alertHeader";

    public static final String ALERT_HEADER_KEY = "messageHeader";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Key Set received from Notification:
        google.sent_time
        rideId
        from
        google.message_id
        message
        senderId
        msgType
        collapse_key
        */

        if (App.isUserLoggedIn()) {
            Intent fetchOfferedRides = new Intent(getApplicationContext(), FetchMyOfferedRidesService.class);
            startService(fetchOfferedRides);
        }

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.e("DATA_SENT", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        if (App.isUserLoggedIn()) {
            if (bundle != null
                    && bundle.get(MSG_TYPE_BUNDLE_KEY) != null
                    && bundle.get(MSG_TYPE_BUNDLE_KEY).equals("chat")) {

                Intent intent = new Intent(this, ChatAct.class);
                intent.putExtra(RIDE_ID_BUNDLE_KEY, (String) bundle.get(RIDE_ID_BUNDLE_KEY));
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (bundle != null
                    && bundle.get(MSG_TYPE_BUNDLE_KEY) != null
                    && bundle.get(MSG_TYPE_BUNDLE_KEY).equals("alert")) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(MSG_TYPE_ALERT, (String) bundle.get(ALERT_KEY)).commit();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(MSG_TYPE_ALERT_HEADER, (String) bundle.get(ALERT_HEADER_KEY)).commit();
            } else if (bundle != null
                    && bundle.get(MSG_TYPE_BUNDLE_KEY) != null
                    && (bundle.get(MSG_TYPE_BUNDLE_KEY).equals("joinRequest")
                    || (bundle.get(MSG_TYPE_BUNDLE_KEY).equals("accepted")))) {
                if (bundle.get(MSG_TYPE_BUNDLE_KEY).equals("joinRequest")) {
                    new RideRequestReceived(Integer.valueOf((String) bundle.get(RIDE_ID_BUNDLE_KEY))).save();
                }
                Intent intent = new Intent(this, MainAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else{
                startActivity(new Intent(this, OpeningAct.class));
            }
        }
        else {
            startActivity(new Intent(this, OpeningAct.class));
        }
        finish();
    }
}
