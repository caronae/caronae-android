package br.ufrj.caronae.httpapis;

import br.ufrj.caronae.models.modelsforjson.ChatMessageSent;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ChatService {
    @POST("/send")
    void sendChatMsg(@Body ChatMessageSent chatMessageSent, Callback<Response> cb);
}
