package br.ufrj.caronae.firebase;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.ChatMessageReceivedFromJson;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchReceivedMessagesService extends IntentService {

    private static List<ChatMessageReceived> chatMsgsList;
    private String rideId;

    public FetchReceivedMessagesService() {
        super("MyFirebaseMessagingService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        rideId = intent.getExtras().getString("rideId");

        chatMsgsList = ChatMessageReceived.find(ChatMessageReceived.class, "ride_id = ?", rideId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        rideId = intent.getStringExtra("rideId");
        String since = intent.getStringExtra("since");
        CaronaeAPI.service().requestChatMsgs(rideId, since)
                .enqueue(new Callback<ModelReceivedFromChat>() {
                             @Override
                             public void onResponse(Call<ModelReceivedFromChat> call, Response<ModelReceivedFromChat> response) {
                                 if (response.isSuccessful()) {
                                     ModelReceivedFromChat chatMessagesReceived = response.body();
                                     if (chatMessagesReceived != null && chatMessagesReceived.getMessages().size() != 0) {

                                         final List<ChatMessageReceived> messagesFetched = new ArrayList<>();

                                         List<ChatMessageReceivedFromJson> listMessages = chatMessagesReceived.getMessages();
                                         for (int mensagesNum = 0; mensagesNum < listMessages.size(); mensagesNum++) {
                                             ChatMessageReceived cmr = new ChatMessageReceived(listMessages.get(mensagesNum).getUser().getName(),
                                                     String.valueOf(listMessages.get(mensagesNum).getUser().getId()),
                                                     listMessages.get(mensagesNum).getMessage(),
                                                     rideId,
                                                     listMessages.get(mensagesNum).getTime());
                                             cmr.setId(Long.parseLong(listMessages.get(mensagesNum).getMessageId()));

                                             messagesFetched.add(cmr);

                                         }
                                         new Thread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 for (int messageIndex = 0; messageIndex < messagesFetched.size(); messageIndex++) {
                                                     if (!messageAlrealdyExist(messagesFetched.get(messageIndex).getId())) {
                                                         messagesFetched.get(messageIndex).save();
                                                     }
                                                 }
                                                 if (SharedPref.getChatActIsForeground()) {
                                                     App.getBus().post(messagesFetched.get(0));
                                                 }
                                             }
                                         }).start();
                                     } else {
                                         ChatMessageReceived cmr = new ChatMessageReceived();
                                         if (SharedPref.getChatActIsForeground()) {
                                             App.getBus().post(cmr);
                                         }
                                         getApplicationContext().sendBroadcast(new Intent("br.ufrj.caronae.acts.ChatAct.BROADCAST_NEW_MESSAGES_NULL"));
                                     }
                                 } else {
                                     Util.treatResponseFromServer(response);
                                     Util.toast("Erro ao Recuperar mensagem de chat");
                                     Log.e("GetMessages", response.message());
                                 }
                             }

                             @Override
                             public void onFailure(Call<ModelReceivedFromChat> call, Throwable t) {
                                 Util.toast("Erro ao Recuperar mensagem de chat");
                                 Log.e("GetMessages", t.getMessage());
                             }
                         }

                );
    }

    private boolean messageAlrealdyExist(long messageId) {
        int counter = chatMsgsList.size() - 1;
        while (counter >= 0) {
            if (chatMsgsList.get(counter).getId() == messageId)
                return true;
            counter--;
        }
        return false;
    }
}
