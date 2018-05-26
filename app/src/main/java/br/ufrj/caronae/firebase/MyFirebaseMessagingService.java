package br.ufrj.caronae.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.acts.ChatAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.RideEndedEvent;
import br.ufrj.caronae.models.RideRequestReceived;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int MESSAGE_NOTIFICATION_ID           = 435345;

    private static final String MSG_TYPE_ALERT                 = "alert";

    public static final String ALERT_KEY                       = "message";

    public static final String MSG_TYPE_ALERT_HEADER           = "alertHeader";

    public static final String ALERT_HEADER_KEY                = "title";


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.i("onMessageReceived", "onMessageReceived");

        if (App.isUserLoggedIn() && remoteMessage.getData() != null) {
            Map data = remoteMessage.getData();
            String msgType = (String) data.get("msgType");
            if (msgType != null && msgType.equals("alert")) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(MSG_TYPE_ALERT, (String) data.get(ALERT_KEY)).apply();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(MSG_TYPE_ALERT_HEADER,(String) data.get(ALERT_HEADER_KEY)).commit();
            } else {
                String message = (String) data.get("message");
                String senderName = (String) data.get("senderName");

                final String rideId = (String) data.get("rideId");

                Log.i("onMessageReceived", message);

                if (msgType != null && msgType.equals("chat")) {
                    String senderId = (String) data.get("senderId");
                    //noinspection ConstantConditions
                    if (senderId.equals(App.getUser().getDbId() + "")) {
                        return;
                    }

                    List<ChatMessageReceived> listOldMessages = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);

                    ChatMessageReceived lastMessage = null;
                    if (listOldMessages.size() != 0) {
                        lastMessage = listOldMessages.get(listOldMessages.size() - 1);
                    }

                    String since;
                    if (lastMessage == null) {
                        since = null;
                    } else {
                        since = lastMessage.getTime();
                    }

                    if (!SharedPref.getChatActIsForeground()) {
                        startService(new Intent(this, FetchReceivedMessagesService.class).putExtra("rideId", rideId).putExtra("since", since));
                    }
                }


                // TODO: Check msgType = melhorar informacoes na notificacao

                if (msgType != null && msgType.equals("joinRequest")) {
                    new RideRequestReceived(Integer.valueOf(rideId)).save();
                }

                if (msgType != null && msgType.equals("finished")) {
                    FirebaseTopicsHandler.unsubscribeFirebaseTopic(rideId);
                    App.getBus().post(new RideEndedEvent(rideId));
                }

                // TODO: Carona cancelada Nao esta rebendo notificacao
                if (msgType != null && msgType.equals("cancelled")) {
                    FirebaseTopicsHandler.unsubscribeFirebaseTopic(rideId);
                    App.getBus().post(new RideEndedEvent(rideId));
                }

                if (msgType != null && msgType.equals("accepted")) {
                    FirebaseTopicsHandler.CheckSubFirebaseTopic(rideId);
                }

                if (SharedPref.getNotifPref().equals("true"))
                    if (msgType != null && msgType.equals("chat")) {
                        if (!SharedPref.getChatActIsForeground()) {
                            createNotification(msgType, message, rideId);
                        } else {
                            App.getBus().post(rideId);
                        }
                    } else
                        createNotification(msgType, message, rideId);

            }
        }
    }

    private void createNotification(String msgType, String message, String rideId) {
        String title;
        Intent resultIntent;
        if (msgType.equals("chat")) {
            title = "Nova mensagem";

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

        Context context = App.getInst();
        Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
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
