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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        App.getChatService().requestChatMsgs(rideId, since, new Callback<ModelReceivedFromChat>() {
            @Override
            public void success(ModelReceivedFromChat chatMessagesReceived, Response response) {
                Log.i("GetMessages", "Sulcefully Retrieved Chat Messages");
                List<ChatMessageReceivedFromJson> listMessages = chatMessagesReceived.getMessages();
                for (int mensagesNum = 0; mensagesNum < listMessages.size(); mensagesNum++) {
                    ChatMessageReceived cmr = new ChatMessageReceived(listMessages.get(mensagesNum).getUser().getName(),
                            String.valueOf(listMessages.get(mensagesNum).getUser().getId()),
                            listMessages.get(mensagesNum).getMessage(),
                            listMessages.get(mensagesNum).getRideId(),
                            listMessages.get(mensagesNum).getTime());
                    cmr.save();
                    App.getBus().post(cmr);
                    Log.v("SIRVICE", cmr.getMessage());
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
}
