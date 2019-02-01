package br.ufrj.caronae.httpapis;

import java.util.List;

import br.ufrj.caronae.models.ChatMessageSendResponse;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import br.ufrj.caronae.models.ModelValidateDuplicate;
import br.ufrj.caronae.models.RideOffer;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.ChatSendMessageForJson;
import br.ufrj.caronae.models.modelsforjson.FacebookFriendForJson;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import br.ufrj.caronae.models.modelsforjson.JoinRequestIDsForJson;
import br.ufrj.caronae.models.modelsforjson.LoginForJson;
import br.ufrj.caronae.models.modelsforjson.MyRidesForJson;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJsonDeserializer;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import br.ufrj.caronae.models.modelsforjson.UserForJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CaronaeAPIService {

    @GET("api/v1/users/{userId}")
    Call<UserForJson> getUser(@Path("userId") String userId);

    @GET("api/v1/users/{userId}/token")
    Call<ResponseBody> getToken(@Path("userId") String userId);

    @GET("api/v1/users/{userId}/rides/history")
    Call<RideHistoryForJson> getRidesHistory(@Path("userId") String userId);

    @GET("api/v1/rides/{rideId}/messages")
    Call<ModelReceivedFromChat> requestChatMsgs(@Path("rideId") String rideId, @Query("since") String since);

    @GET("api/v1/rides/validateDuplicate")
    Call<ModelValidateDuplicate> validateDuplicates(@Query("date") String date, @Query("time") String time, @Query("going") int going);

    @GET("api/v1/rides/{rideId}")
    Call<RideForJson> getRide(@Path("rideId") String rideId);

    @GET("api/v1/rides")
    Call<RideForJsonDeserializer> listAllRides(@Query("page") int pageNum, @Query("going") String going, @Query("neighborhoods") String neighborhoods, @Query("zone") String zone, @Query("hubs") String hub, @Query("place") String place, @Query("campus") String campus, @Query("date") String date, @Query("time") String time);

    @GET("api/v1/places")
    Call<PlacesForJson> getPlaces();

    @GET("api/v1/rides/{rideId}/requests")
    Call<List<User>> getRequesters(@Path("rideId") String rideId);

    @GET("api/v1/users/{userId}/rides")
    Call<MyRidesForJson> getMyRides(@Path("userId") String userId);

    @POST("api/v1/falae/messages")
    Call<ResponseBody> falaeSendMessage(@Body FalaeMsgForJson msg);

    @POST("api/v1/rides/{rideId}/messages")
    Call<ChatMessageSendResponse> sendChatMsg(@Path("rideId") String rideId, @Body ChatSendMessageForJson message);

    @POST("api/v1/rides")
    Call<ResponseBody> offerRide(@Body RideOffer ride);

    @POST("api/v1/rides/{rideId}/requests")
    Call<ResponseBody> requestJoin(@Path("rideId") String rideId);

    @POST("api/v1/rides/{rideId}/leave")
    Call<ResponseBody> leaveRide(@Path("rideId") String rideId);

    @POST("api/v1/rides/{rideId}/finish")
    Call<ResponseBody> finishRide(@Path("rideId") String rideId);

    @POST("api/v1/users/login")
    Call<UserForJson> login(@Body LoginForJson token);

    @PUT("api/v1/users/{userId}")
    Call<ResponseBody> updateUser(@Path("userId") String userId, @Body User user);

    @PUT("api/v1/rides/{rideId}/requests")
    Call<ResponseBody> answerJoinRequest(@Path("rideId") String rideId, @Body JoinRequestIDsForJson joinRequestIDsForJson);

    @GET("user/{id}/mutualFriends")
    Call<FacebookFriendForJson> getMutualFriends(@Header("Facebook-Token") String faceToken, @Path("id") String faceId);

    @DELETE("api/v1/rides/allFromRoutine/{routineId}")
    Call<ResponseBody> deleteAllRidesFromRoutine(@Path("routineId") String routineId);
}
