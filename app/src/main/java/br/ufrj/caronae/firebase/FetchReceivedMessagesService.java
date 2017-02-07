package br.ufrj.caronae.firebase;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.ChatMessageReceivedFromJson;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Luis on 1/12/2017.
 */
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
        Log.v("onMessageReceived", "Entered Service");
        rideId = intent.getStringExtra("rideId");
        String since = intent.getStringExtra("since");
        App.getNetworkService(getApplicationContext()).requestChatMsgs(rideId, since)
                .enqueue(new Callback<ModelReceivedFromChat>() {
                    @Override
                    public void onResponse(Call<ModelReceivedFromChat> call, Response<ModelReceivedFromChat> response) {
                        if (response.isSuccessful()) {
                            ModelReceivedFromChat chatMessagesReceived = response.body();
                            if (chatMessagesReceived != null && chatMessagesReceived.getMessages().size() != 0) {

                                List<ChatMessageReceivedFromJson> listMessages = chatMessagesReceived.getMessages();
                                for (int mensagesNum = 0; mensagesNum < listMessages.size(); mensagesNum++) {
                                    ChatMessageReceived cmr = new ChatMessageReceived(listMessages.get(mensagesNum).getUser().getName(),
                                            String.valueOf(listMessages.get(mensagesNum).getUser().getId()),
                                            listMessages.get(mensagesNum).getMessage(),
                                            rideId,
                                            listMessages.get(mensagesNum).getTime());
                                    cmr.setId(Long.parseLong(listMessages.get(mensagesNum).getMessageId()));

                                    Log.v("onMessageReceived", "id1:" + cmr.getId() + " " + cmr.getMessage());


                                    if (!messageAlrealdyExist(Long.parseLong(listMessages.get(mensagesNum).getMessageId()))) {
                                        Log.v("onMessageReceived", "Salvou mensgaem");
                                        cmr.save();
                                        if(SharedPref.getChatActIsForeground()) {
                                            Log.v("onMessageReceived", "Postou");
                                            App.getBus().post(cmr);
                                        }
                                    }
                                }
                            }
                        } else {
                            Util.toast("Erro ao Recuperar mensagem de chat");
                            Log.e("GetMessages", response.message());
                            if(SharedPref.getChatActIsForeground()){
                                App.getBus().post(true);
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelReceivedFromChat> call, Throwable t) {
                        Util.toast("Erro ao Recuperar mensagem de chat");
                        Log.e("GetMessages", t.getMessage());
                        if(SharedPref.getChatActIsForeground()){
                            App.getBus().post(true);
                        }
                    }
                });
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
