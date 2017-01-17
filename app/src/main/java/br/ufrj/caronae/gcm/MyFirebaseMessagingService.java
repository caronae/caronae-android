package br.ufrj.caronae.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.ChatAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.ChatMessageReceivedFromJson;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import br.ufrj.caronae.models.NewChatMsgIndicator;
import br.ufrj.caronae.models.RideEndedEvent;
import br.ufrj.caronae.models.RideRequestReceived;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Luis-DELL on 10/28/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int MESSAGE_NOTIFICATION_ID = 435345;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map data = remoteMessage.getData();
        String message = (String)data.get("message");
        String msgType = (String)data.get("msgType");
        String senderName = (String)data.get("senderName");
        String rideId = (String)data.get("rideId");

        Log.i("onMessageReceived", message);

        if (msgType != null && msgType.equals("chat")) {
            String senderId = (String)data.get("senderId");
            //noinspection ConstantConditions
            if (senderId.equals(App.getUser().getDbId() + "")) {
                return;
            }

            List<ChatMessageReceived> listOldMessages = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);

            ChatMessageReceived lastMessage = null;
            if(listOldMessages.size() != 0) {
                lastMessage = listOldMessages.get(listOldMessages.size() - 1);
            }

            String since;
            if (lastMessage == null){
                since = null;
            } else{
                since = lastMessage.getTime();
            }

            startService(new Intent(this, FetchReceivedMessagesService.class).putExtra("ride_id", rideId).putExtra("since", since));

            App.getChatService().requestChatMsgs(rideId, since, new Callback<ModelReceivedFromChat>() {
                    @Override
                    public void success(ModelReceivedFromChat chatMessagesReceived, Response response) {
                        Log.i("GetMessages", "Sulcefully Retrieved Chat Messages");
                        List<ChatMessageReceivedFromJson> listMessages = chatMessagesReceived.getMessages();
                        for (int mensagesNum = 0; mensagesNum < listMessages.size(); mensagesNum++){
                            ChatMessageReceived cmr = new ChatMessageReceived(listMessages.get(mensagesNum).getUser().getName(),
                                    String.valueOf(listMessages.get(mensagesNum).getUser().getId()),
                                    listMessages.get(mensagesNum).getMessage(),
                                    listMessages.get(mensagesNum).getRideId(),
                                    listMessages.get(mensagesNum).getTime());
                            cmr.save();
                            App.getBus().post(cmr);
                        }
                        new NewChatMsgIndicator(Integer.valueOf(listMessages.get(0).getRideId())).save();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Util.toast("Erro ao Recuperar mensagem de chat");
                        try {
                            Log.e("GetMessages", error.getMessage());
                        } catch (Exception e) {
                            Log.e("GetMessages", e.getMessage());
                        }
                    }
                });
        }


        if (msgType != null && msgType.equals("joinRequest")) {
            new RideRequestReceived(Integer.valueOf(rideId)).save();
        }

        if (msgType != null && msgType.equals("finished")) {
            FirebaseTopicsHandler.unsubscribeFirebaseTopic(rideId);
            App.getBus().post(new RideEndedEvent(rideId));
            ActiveRide.deleteAll(ActiveRide.class, "db_id = ?", rideId);
        }

        if (msgType != null && msgType.equals("cancelled")) {
            FirebaseTopicsHandler.unsubscribeFirebaseTopic(rideId);
            App.getBus().post(new RideEndedEvent(rideId));
            ActiveRide.deleteAll(ActiveRide.class, "db_id = ?", rideId);
        }

        if (msgType != null && msgType.equals("accepted")) {
            FirebaseTopicsHandler.CheckSubFirebaseTopic(rideId);
            //new DeleteConflictingRequests().execute(rideId);
        }

        // TODO: Check msgType = "refused" e "quitter" e Tratar quando msg chat falhar em ser recebida

        if (SharedPref.getNotifPref().equals("true"))
            if (msgType != null && msgType.equals("chat")){
                if (!SharedPref.getChatActIsForeground()){
                    createNotification(msgType, senderName, message, rideId);
                } else {
                    App.getBus().post(rideId);
                }
            } else
                createNotification(msgType, senderName, message, rideId);

        startService(new Intent(getApplicationContext(), FetchReceivedMessagesService.class));
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
