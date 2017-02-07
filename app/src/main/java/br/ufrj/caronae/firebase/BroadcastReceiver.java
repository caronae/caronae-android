package br.ufrj.caronae.firebase;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Created by Luis on 2/6/2017.
 */

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean conected = !intent.getBooleanExtra(connectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(conected) {
            context.startService(new Intent(context, MyService.class));
        }
    }
}
