package br.ufrj.caronae;

import br.ufrj.caronae.models.modelsforjson.ChatMessageToSend;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ChatService {
    @POST("/send")
    void sendChatMsg(@Body ChatMessageToSend chatMessageToSend, Callback<Response> cb);
}
