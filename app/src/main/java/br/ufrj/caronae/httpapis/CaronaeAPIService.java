package br.ufrj.caronae.httpapis;

import java.util.List;

import br.ufrj.caronae.models.ChatMessageSendResponse;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import br.ufrj.caronae.models.ModelValidateDuplicate;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRountine;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.ChatSendMessageForJson;
import br.ufrj.caronae.models.modelsforjson.FacebookFriendForJson;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import br.ufrj.caronae.models.modelsforjson.HistoryRideCountForJson;
import br.ufrj.caronae.models.modelsforjson.IdForJson;
import br.ufrj.caronae.models.modelsforjson.JoinRequestIDsForJson;
import br.ufrj.caronae.models.modelsforjson.LoginForJson;
import br.ufrj.caronae.models.modelsforjson.MyRidesForJson;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJsonDeserializer;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import br.ufrj.caronae.models.modelsforjson.UrlForJson;
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

    @GET("api/v1/rides/{rideId}/messages")
    Call<ModelReceivedFromChat> requestChatMsgs(@Path("rideId") String rideId, @Query("since") String since);

    @GET("api/v1/rides/validateDuplicate")
    Call<ModelValidateDuplicate> validateDuplicates(@Query("date") String date, @Query("time") String time, @Query("going") int going);

    @GET("api/v1/rides/{rideId}")
    Call<RideForJson> getRide(@Path("rideId") String rideId);

    @GET("api/v1/rides")
    Call<RideForJsonDeserializer> listAllRides(@Query("page") String pageNum, @Query("going") String going, @Query("neighborhoods") String neighborhoods, @Query("zone") String zone, @Query("hubs") String hub, @Query("place") String place, @Query("campus") String campus, @Query("date") String date, @Query("time") String time);

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
    Call<List<RideRountine>> offerRide(@Body Ride ride);

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

    @GET("user/signup/{name}/{token}")
    Call<User> signUp(@Path("name") String name, @Path("token") String token);

    @GET("user/signup/intranet/{id}/{token}")
    Call<User> signUpIntranet(@Path("id") String id, @Path("token") String token);

    @PUT("user/saveFaceId")
    Call<ResponseBody> saveFaceId(@Body IdForJson id);

    @PUT("user/saveProfilePicUrl")
    Call<ResponseBody> saveProfilePicUrl(@Body UrlForJson url);

    @GET("user/{id}/mutualFriends")
    Call<FacebookFriendForJson> getMutualFriends(@Header("Facebook-Token") String faceToken, @Path("id") String faceId);

    @DELETE("ride/allFromRoutine/{routineId}")
    Call<ResponseBody> deleteAllRidesFromRoutine(@Path("routineId") String routineId);

    @GET("ride/getRidesHistory")
    Call<List<RideHistoryForJson>> getRidesHistory();

    @GET("ride/getRidesHistoryCount/{userId}")
    Call<HistoryRideCountForJson> getRidesHistoryCount(@Path("userId") String userId);
}
