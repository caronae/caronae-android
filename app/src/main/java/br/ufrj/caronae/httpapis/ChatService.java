package br.ufrj.caronae.httpapis;

import br.ufrj.caronae.models.ChatMessageSendResponse;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import br.ufrj.caronae.models.modelsforjson.ChatSendMessageForJson;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ChatService {

//    @POST("/send")
//    void sendChatMsg(@Body ChatMessageSent chatMessageSent, Callback<Response> cb);

    @POST("/ride/{rideId}/chat")
    void sendChatMsg(@Path("rideId") String rideId, @Body ChatSendMessageForJson message, Callback<ChatMessageSendResponse> cb);


    @GET("/ride/{rideId}/chat")
    void requestChatMsgs(@Path("rideId") String rideId, @Query("since") String since, Callback<ModelReceivedFromChat> cb);
}
