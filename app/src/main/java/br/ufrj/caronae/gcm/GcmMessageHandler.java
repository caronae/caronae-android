package br.ufrj.caronae.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.asyncs.CheckSubGcmTopic;
import br.ufrj.caronae.R;
import br.ufrj.caronae.asyncs.UnsubGcmTopic;
import br.ufrj.caronae.models.ActiveRideId;
import br.ufrj.caronae.models.ChatMessageReceived;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String msgType = data.getString("msgType");
        String senderName = data.getString("senderName");

        Log.i("onMessageReceived", message);

        boolean notify = true;

        if (msgType != null && msgType.equals("chat")) {
            String rideId = data.getString("rideId");
            String senderId = data.getString("senderId");
            String time = data.getString("time");
            ChatMessageReceived cmr = new ChatMessageReceived(senderName, senderId, message, rideId, time);
            cmr.save();

            //noinspection ConstantConditions
            if (senderId.equals(App.getUser().getDbId()+"")) {
                notify = false;
            } else {
                App.getBus().post(cmr);
            }
        }

        if (msgType != null && msgType.equals("cancelled")) {
            String rideId = data.getString("rideId");
            new UnsubGcmTopic(getApplicationContext(), rideId).execute();
        }

        if (msgType != null && msgType.equals("accepted")) {
            String rideId = data.getString("rideId");
            new CheckSubGcmTopic().execute(rideId);
        }

        if (notify && SharedPref.getNotifPref().equals("true"))
            createNotification(msgType, senderName, message);
    }

    // Creates notification based on title and body received
    private void createNotification(String msgType, String senderName, String message) {
        String title;
        if (msgType.equals("chat")) {
            title = "Nova mensagem";
            message = senderName + ": " + message;
        } else {
            title = "Aviso de carona";
        }

        Context context = App.inst();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setSound(alarmSound)
                .setContentText(message);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}
