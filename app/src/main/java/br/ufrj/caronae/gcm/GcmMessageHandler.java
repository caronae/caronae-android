package br.ufrj.caronae.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.UnsubGcmTopic;
import br.ufrj.caronae.models.ChatMessageReceived;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String msgType = data.getString("msgType");

        Log.i("onMessageReceived", message);

        if (msgType != null && msgType.equals("chat")) {
            String sender = data.getString("senderName");
            new ChatMessageReceived(sender, message).save();
        }

        if (msgType != null && msgType.equals("cancelled")) {
            String rideId = data.getString("rideId");
            new UnsubGcmTopic(getApplicationContext(), rideId).execute();
        }

        if (msgType != null && msgType.equals("accepted")) {
            String rideId = data.getString("rideId");
            App.subscribeToTopicIfNeeded(rideId);
        }

        if (App.getPref(App.NOTIFICATIONS_ON_PREF_KEY).equals("true"))
            createNotification(message);
    }

    // Creates notification based on title and body received
    private void createNotification(String message) {
        Context context = App.inst();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Aviso de carona")
                .setSound(alarmSound)
                .setContentText(message);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}
