package br.ufrj.caronae.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MyGCMBroadcastReceiver extends WakefulBroadcastReceiver {
    private static String zzaLH = "gcm.googleapis.com/refresh";

    @Override
    public void onReceive(Context context, Intent intent) {

        String var3 = intent.getStringExtra("from"), serviceName;
        if("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction()) || "google.com/iid".equals(var3) || zzaLH.equals(var3)) {
            serviceName = RegistrationIntentService.class.getName();
        } else {
            serviceName = MyGCMIntentService.class.getName();
        }

        ComponentName comp = new ComponentName(context.getPackageName(),  serviceName);

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}