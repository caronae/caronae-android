package br.ufrj.caronae.httpapis;

import java.util.List;

import br.ufrj.caronae.models.ChatMessageSendResponse;
import br.ufrj.caronae.models.ModelReceivedFromChat;
import br.ufrj.caronae.models.Ride;
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
//    void signUp(@Path("name") String name, @Path("token") String token, Callback<User> cb);
    Call<User> signUp(@Path("name") String name, @Path("token") String token);

    @GET("user/signup/intranet/{id}/{token}")
//    void signUpIntranet(@Path("id") String id, @Path("token") String token, Callback<User> cb);
    Call<User> signUpIntranet(@Path("id") String id, @Path("token") String token);

    @POST("user/login")
//    void login(@Body LoginForJson token, Callback<UserWithRidesForJson> cb);
    Call<UserWithRidesForJson> login(@Body LoginForJson token);

    @PUT("user")
//    void updateUser(@Body User user, Callback<Response> cb);
    Call<ResponseBody> updateUser(@Body User user);

    @PUT("user/saveGcmToken")
//    void saveGcmToken(@Body TokenForJson token, Callback<Response> cb);
    Call<ResponseBody> saveGcmToken(@Body TokenForJson token);

    @PUT("user/saveFaceId")
//    void saveFaceId(@Body IdForJson id, Callback<Response> cb);
    Call<ResponseBody> saveFaceId(@Body IdForJson id);

    @PUT("user/saveProfilePicUrl")
//    void saveProfilePicUrl(@Body UrlForJson url, Callback<Response> cb);
    Call<ResponseBody> saveProfilePicUrl(@Body UrlForJson url);

    @GET("user/{id}/mutualFriends")
//    void getMutualFriends(@Header("Facebook-Token") String faceToken, @Path("id") String faceId, Callback<FacebookFriendForJson> cb);
    Call<FacebookFriendForJson> getMutualFriends(@Header("Facebook-Token") String faceToken, @Path("id") String faceId);

    @GET("user/intranetPhotoUrl")
//    void getIntranetPhotoUrl(Callback<UrlForJson> cb);
    Call<UrlForJson> getIntranetPhotoUrl();

    @POST("ride")
//    void offerRide(@Header("Header") String header, @Body Ride ride, Callback<List<Ride>> cb);
    Call<List<Ride>> offerRide(@Header("Header") String header, @Body Ride ride);

    @DELETE("ride/{rideId}")
//    void deleteRide(@Path("rideId") String rideId, Callback<Response> cb);
    Call<ResponseBody> deleteRide(@Path("rideId") String rideId);

    @DELETE("ride/allFromUser/{stub}/{going}")
//    void deleteAllRidesFromUser(@Path("stub") String stub, @Path("going") boolean going, Callback<Response> cb);
    Call<ResponseBody> deleteAllRidesFromUser(@Path("stub") String stub, @Path("going") boolean going);

    @DELETE("ride/allFromRoutine/{routineId}")
//    void deleteAllRidesFromRoutine(@Path("routineId") String routineId, Callback<Response> cb);
    Call<ResponseBody> deleteAllRidesFromRoutine(@Path("routineId") String routineId);

    @POST("ride/listFiltered")
//    void listFiltered(@Body RideSearchFiltersForJson rideSearchFilters, Callback<List<RideForJson>> cb);
    Call<List<RideForJson>> listFiltered(@Body RideSearchFiltersForJson rideSearchFilters);

    @GET("ride/all")
//    void listAllRides(Callback<List<RideForJson>> cb);
    Call<List<RideForJson>> listAllRides();

    @POST("ride/requestJoin")
//    void requestJoin(@Body RideIdForJson rideId, Callback<Response> cb);
    Call<ResponseBody> requestJoin(@Body RideIdForJson rideId);

    @GET("ride/getRequesters/{rideId}")
//    void getRequesters(@Path("rideId") String rideId, Callback<List<User>> cb);
    Call<List<User>> getRequesters(@Path("rideId") String rideId);

    @POST("ride/answerJoinRequest")
//    void answerJoinRequest(@Body JoinRequestIDsForJson joinRequestIDsForJson, Callback<Response> cb);
    Call<ResponseBody> answerJoinRequest(@Body JoinRequestIDsForJson joinRequestIDsForJson);

    @GET("ride/getMyActiveRides")
//    void getMyActiveRides(Callback<List<RideForJson>> cb);
    Call<List<RideForJson>> getMyActiveRides();

    @POST("ride/leaveRide")
//    void leaveRide(@Body RideIdForJson rideId, Callback<Response> cb);
    Call<ResponseBody> leaveRide(@Body RideIdForJson rideId);

    @POST("ride/finishRide")
//    void finishRide(@Body RideIdForJson rideId, Callback<Response> cb);
    Call<ResponseBody> finishRide(@Body RideIdForJson rideId);

    @GET("ride/getRidesHistory")
//    void getRidesHistory(Callback<List<RideHistoryForJson>> cb);
    Call<List<RideHistoryForJson>> getRidesHistory();

    @GET("ride/getRidesHistoryCount/{userId}")
//    void getRidesHistoryCount(@Path("userId") String userId, Callback<HistoryRideCountForJson> cb);
    Call<HistoryRideCountForJson> getRidesHistoryCount(@Path("userId") String userId);

    @POST("ride/saveFeedback")
//    void saveFeedback(@Body RideFeedbackForJson rideFeedbackForJson, Callback<Response> cb);
    Call<ResponseBody> saveFeedback(@Body RideFeedbackForJson rideFeedbackForJson);

    @DELETE("ride/joinRequests")
    void deleteJoinRequests(@Body List<RideIdForJson> rideIdsList, Callback<Response> cb);

    //falae route
    @POST("falae/sendMessage")
//    void falaeSendMessage(@Body FalaeMsgForJson msg, Callback<Response> cb);
    Call<ResponseBody> falaeSendMessage(@Body FalaeMsgForJson msg);

    @POST("ride/{rideId}/chat")
//    void sendChatMsg(@Path("rideId") String rideId, @Body ChatSendMessageForJson message, Callback<ChatMessageSendResponse> cb);
    Call<ChatMessageSendResponse> sendChatMsg(@Path("rideId") String rideId, @Body ChatSendMessageForJson message);


    @GET("ride/{rideId}/chat")
//    void requestChatMsgs(@Path("rideId") String rideId, @Query("since") String since, Callback<ModelReceivedFromChat> cb);
    Call<ModelReceivedFromChat> requestChatMsgs(@Path("rideId") String rideId, @Query("since") String since);
}
