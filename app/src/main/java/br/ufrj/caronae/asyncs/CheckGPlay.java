package br.ufrj.caronae.asyncs;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.gcm.RegistrationIntentService;

public class CheckGPlay extends AsyncTask<Void, Void, Void> {
    private final Activity context;

    public CheckGPlay(Activity context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int resultGplay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if (resultGplay != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(resultGplay, context, MainAct.GPLAY_UNAVAILABLE);
        } else {
            if (SharedPref.getUserGcmToken().equals(SharedPref.MISSING_PREF)) {
                Intent intent = new Intent(context, RegistrationIntentService.class);
                context.startService(intent);
            }
        }
        return null;
    }
}
