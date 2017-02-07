package br.ufrj.caronae.firebase;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import br.ufrj.caronae.R;

public class MyService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public MyService() {
        super("My_Worker_Thread");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Service Started....",Toast.LENGTH_LONG).show();
        Log.v("SIRIVICO", "startComand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("SIRIVICO", "destroy");
        Toast.makeText(this,"Service Stopped....",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Toast.makeText(getApplicationContext(),"Good",Toast.LENGTH_LONG).show();
        Log.v("SIRIVICO", "handleIntent");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Titulo")
                .setAutoCancel(true)
                .setContentText("missage");
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(435345, mBuilder.build());

        synchronized (this){


            int count=0;
            while (count<1) {
                try {
                    wait(3000);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }


    }
}