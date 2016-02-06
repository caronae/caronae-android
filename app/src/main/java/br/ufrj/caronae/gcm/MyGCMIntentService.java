package br.ufrj.caronae.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.acts.ChatAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.asyncs.CheckSubGcmTopic;
import br.ufrj.caronae.asyncs.UnsubGcmTopic;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.NewChatMsgIndicator;
import br.ufrj.caronae.models.RideEndedEvent;
import br.ufrj.caronae.models.RideRequestReceived;

public class MyGCMIntentService extends IntentService {

    private static final int MESSAGE_NOTIFICATION_ID = 435345;

    public MyGCMIntentService() {
        super("MyGCMIntentService");
    }

    // Handle the intent that is broadcast
    @Override
    protected void onHandleIntent(Intent intent) {
        // Get the bundle attached to the intent.
        Bundle extras = intent.getExtras();

        // Get an instance of the GCM
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // Get the message type
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            // You can check the message type to be sure it's not an error
            //noinspection deprecation
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                onMessageReceived(intent.getExtras());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        MyGCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void onMessageReceived(Bundle data) {
        String message = data.getString("message");
        String msgType = data.getString("msgType");
        String senderName = data.getString("senderName");
        String rideId = data.getString("rideId");

        Log.i("onMessageReceived", message);

        boolean notify = true;

        if (msgType != null && msgType.equals("chat")) {
            String senderId = data.getString("senderId");
            String time = data.getString("time");
            ChatMessageReceived cmr = new ChatMessageReceived(senderName, senderId, message, rideId, time);
            cmr.save();

            new NewChatMsgIndicator(Integer.valueOf(rideId)).save();

            //noinspection ConstantConditions
            if (senderId.equals(App.getUser().getDbId() + "")) {
                notify = false;
            } else {
                App.getBus().post(cmr);
            }
        }

        if (msgType != null && msgType.equals("joinRequest")) {
            new RideRequestReceived(Integer.valueOf(rideId)).save();
        }

        if (msgType != null && msgType.equals("finished")) {
            new UnsubGcmTopic(getApplicationContext(), rideId).execute();
            App.getBus().post(new RideEndedEvent(rideId));
        }

        if (msgType != null && msgType.equals("cancelled")) {
            new UnsubGcmTopic(getApplicationContext(), rideId).execute();
            App.getBus().post(new RideEndedEvent(rideId));
        }

        if (msgType != null && msgType.equals("accepted")) {
            new CheckSubGcmTopic().execute(rideId);
            //new DeleteConflictingRequests().execute(rideId);
        }

        if (notify && SharedPref.getNotifPref().equals("true"))
            createNotification(msgType, senderName, message, rideId);
    }

    private void createNotification(String msgType, String senderName, String message, String rideId) {
        String title;
        Intent resultIntent;
        if (msgType.equals("chat")) {
            title = "Nova mensagem";
            message = senderName + ": " + message;

            List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideId);
            if (l != null && !l.isEmpty()) {
                resultIntent = new Intent(this, ChatAct.class);
                resultIntent.putExtra("rideId", rideId);
            } else {
                resultIntent = new Intent(this, MainAct.class);
            }
        } else {
            title = "Aviso de carona";
            resultIntent = new Intent(this, MainAct.class);
        }

        resultIntent.putExtra("msgType", msgType);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Context context = App.inst();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentText(message);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}