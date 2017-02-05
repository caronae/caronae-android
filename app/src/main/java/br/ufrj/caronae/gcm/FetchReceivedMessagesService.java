package br.ufrj.caronae.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.ChatMessageReceived;
import br.ufrj.caronae.models.ChatMessageReceivedFromJson;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import br.ufrj.caronae.models.NewChatMsgIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Luis on 1/12/2017.
 */
public class FetchReceivedMessagesService extends IntentService {

    public FetchReceivedMessagesService() {
        super("MyFirebaseMessagingService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("SIRVICE", "Entered Service");
        String rideId = intent.getStringExtra("ride_id");
        String since = intent.getStringExtra("since");
        App.getChatService().requestChatMsgs(rideId, since)
                .enqueue(new Callback<ModelReceivedFromChat>() {
                    @Override
                    public void onResponse(Call<ModelReceivedFromChat> call, Response<ModelReceivedFromChat> response) {
                        if (response.isSuccessful()) {
                            ModelReceivedFromChat chatMessagesReceived = response.body();
                            Log.i("GetMessages", "Sulcefully Retrieved Chat Messages");
                            List<ChatMessageReceivedFromJson> listMessages = chatMessagesReceived.getMessages();
                            for (int mensagesNum = 0; mensagesNum < listMessages.size(); mensagesNum++) {
                                ChatMessageReceived cmr = new ChatMessageReceived(listMessages.get(mensagesNum).getUser().getName(),
                                        String.valueOf(listMessages.get(mensagesNum).getUser().getId()),
                                        listMessages.get(mensagesNum).getMessage(),
                                        listMessages.get(mensagesNum).getMessageId(),
                                        listMessages.get(mensagesNum).getTime());
                                cmr.save();
                                App.getBus().post(cmr);
                                Log.v("SIRVICE", cmr.getMessage());
                            }
                            new NewChatMsgIndicator(Integer.valueOf(listMessages.get(0).getMessageId())).save();
                        } else {
                            Util.toast("Erro ao Recuperar mensagem de chat");
                            Log.e("GetMessages", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelReceivedFromChat> call, Throwable t) {
                        Util.toast("Erro ao Recuperar mensagem de chat");
                        Log.e("GetMessages", t.getMessage());
                    }
                });
    }
}
