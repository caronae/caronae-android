package br.ufrj.caronae.httpapis;

import br.ufrj.caronae.models.ChatMessageSendResponse;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import br.ufrj.caronae.models.modelsforjson.ChatSendMessageForJson;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatService {

//    @POST("/send")
//    void sendChatMsg(@Body ChatMessageSent chatMessageSent, Callback<Response> cb);

    @POST("ride/{rideId}/chat")
    void sendChatMsg(@Path("rideId") String rideId, @Body ChatSendMessageForJson message, Callback<ChatMessageSendResponse> cb);


    @GET("ride/{rideId}/chat")
    void requestChatMsgs(@Path("rideId") String rideId, @Query("since") String since, Callback<ModelReceivedFromChat> cb);
}
