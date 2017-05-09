package br.ufrj.caronae.httpapis;

import java.util.List;

import br.ufrj.caronae.models.ChatMessageSendResponse;
import br.ufrj.caronae.models.ModelReceivedFromChat;
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
import br.ufrj.caronae.models.modelsforjson.RideFeedbackForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJsonDeserializer;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import br.ufrj.caronae.models.modelsforjson.RideSearchFiltersForJson;
import br.ufrj.caronae.models.modelsforjson.TokenForJson;
import br.ufrj.caronae.models.modelsforjson.UrlForJson;
import br.ufrj.caronae.models.modelsforjson.UserWithRidesForJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {

    int versionCode = 10;

    //user routes
    @GET("user/signup/{name}/{token}")
    Call<User> signUp(@Path("name") String name, @Path("token") String token);

    @GET("user/signup/intranet/{id}/{token}")
    Call<User> signUpIntranet(@Path("id") String id, @Path("token") String token);

    @POST("user/login")
    Call<UserWithRidesForJson> login(@Body LoginForJson token);

    @PUT("user")
    Call<ResponseBody> updateUser(@Body User user);

    @PUT("user/saveGcmToken")
    Call<ResponseBody> saveGcmToken(@Body TokenForJson token);

    @PUT("user/saveFaceId")
    Call<ResponseBody> saveFaceId(@Body IdForJson id);

    @PUT("user/saveProfilePicUrl")
    Call<ResponseBody> saveProfilePicUrl(@Body UrlForJson url);

    @GET("user/{id}/mutualFriends")
    Call<FacebookFriendForJson> getMutualFriends(@Header("Facebook-Token") String faceToken, @Path("id") String faceId);

    @GET("user/intranetPhotoUrl")
    Call<UrlForJson> getIntranetPhotoUrl();

    @POST("ride")
    Call<List<RideRountine>> offerRide(@Header("Header") String header, @Body Ride ride);

    @DELETE("ride/{rideId}")
    Call<ResponseBody> deleteRide(@Path("rideId") String rideId);

    @DELETE("ride/allFromUser/{stub}/{going}")
    Call<ResponseBody> deleteAllRidesFromUser(@Path("stub") String stub, @Path("going") boolean going);

    @DELETE("ride/allFromRoutine/{routineId}")
    Call<ResponseBody> deleteAllRidesFromRoutine(@Path("routineId") String routineId);

    @POST("ride/listFiltered")
    Call<List<RideForJson>> listFiltered(@Body RideSearchFiltersForJson rideSearchFilters);

    @GET("rides")
    Call<RideForJsonDeserializer> listAllRides(@Query("page") String pageNum, @Query("going") String going, @Query("neighborhoods") String neighborhoods, @Query("zone") String zone, @Query("hub") String hub);

    @POST("ride/requestJoin")
    Call<ResponseBody> requestJoin(@Body RideIdForJson rideId);

    @GET("ride/getRequesters/{rideId}")
    Call<List<User>> getRequesters(@Path("rideId") String rideId);

    @POST("ride/answerJoinRequest")
    Call<ResponseBody> answerJoinRequest(@Body JoinRequestIDsForJson joinRequestIDsForJson);

    @GET("ride/getMyActiveRides")
//    void getMyActiveRides(Callback<List<RideForJson>> cb);
    Call<List<RideForJson>> getMyActiveRides();

    @POST("ride/leaveRide")
    Call<ResponseBody> leaveRide(@Body RideIdForJson rideId);

    @POST("ride/finishRide")
    Call<ResponseBody> finishRide(@Body RideIdForJson rideId);

    @GET("ride/getRidesHistory")
    Call<List<RideHistoryForJson>> getRidesHistory();

    @GET("ride/getRidesHistoryCount/{userId}")
    Call<HistoryRideCountForJson> getRidesHistoryCount(@Path("userId") String userId);

    @POST("ride/saveFeedback")
    Call<ResponseBody> saveFeedback(@Body RideFeedbackForJson rideFeedbackForJson);

    @DELETE("ride/joinRequests")
    void deleteJoinRequests(@Body List<RideIdForJson> rideIdsList, Callback<Response> cb);

    //falae route
    @POST("falae/sendMessage")
    Call<ResponseBody> falaeSendMessage(@Body FalaeMsgForJson msg);

    @POST("ride/{rideId}/chat")
    Call<ChatMessageSendResponse> sendChatMsg(@Path("rideId") String rideId, @Body ChatSendMessageForJson message);


    @GET("ride/{rideId}/chat")
    Call<ModelReceivedFromChat> requestChatMsgs(@Path("rideId") String rideId, @Query("since") String since);
}
