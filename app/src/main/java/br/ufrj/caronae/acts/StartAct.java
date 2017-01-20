package br.ufrj.caronae.acts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Set;

import br.ufrj.caronae.App;

public class StartAct extends AppCompatActivity {

    private final String RIDE_ID_BUNDLE_KEY                   = "rideId";

    private final String MSG_TYPE_BUNDLE_KEY                  = "msgType";

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

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("DATA_SENT", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        if (App.isUserLoggedIn())
            if(bundle != null && bundle.get(MSG_TYPE_BUNDLE_KEY).equals("chat")){
                Intent intent = new Intent(this, ChatAct.class);
                intent.putExtra(RIDE_ID_BUNDLE_KEY, (String) bundle.get(RIDE_ID_BUNDLE_KEY));
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, MainAct.class));
            }
        else
            startActivity(new Intent(this, OpeningAct.class));

        finish();
    }
}
