package br.ufrj.caronae.httpapis;

import java.util.List;

import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.FacebookFriendForJson;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import br.ufrj.caronae.models.modelsforjson.HistoryRideCountForJson;
import br.ufrj.caronae.models.modelsforjson.IdForJson;
import br.ufrj.caronae.models.modelsforjson.JoinRequestIDsForJson;
import br.ufrj.caronae.models.modelsforjson.RideFeedbackForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import br.ufrj.caronae.models.modelsforjson.RideSearchFiltersForJson;
import br.ufrj.caronae.models.modelsforjson.TokenForJson;
import br.ufrj.caronae.models.modelsforjson.UrlForJson;
import br.ufrj.caronae.models.modelsforjson.UserWithRidesForJson;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface NetworkService {

    int versionCode = 10;

    //user routes
    @GET("/user/signup/{name}/{token}")
    void signUp(@Path("name") String name, @Path("token") String token, Callback<User> cb);

    @GET("/user/signup/intranet/{id}/{token}")
    void signUpIntranet(@Path("id") String id, @Path("token") String token, Callback<User> cb);

    @POST("/user/login")
    void login(@Body TokenForJson token, Callback<UserWithRidesForJson> cb);

    @PUT("/user")
    void updateUser(@Body User user, Callback<Response> cb);

    @PUT("/user/saveGcmToken")
    void saveGcmToken(@Body TokenForJson token, Callback<Response> cb);

    @PUT("/user/saveFaceId")
    void saveFaceId(@Body IdForJson id, Callback<Response> cb);

    @PUT("/user/saveProfilePicUrl")
    void saveProfilePicUrl(@Body UrlForJson url, Callback<Response> cb);

    @GET("/user/{id}/mutualFriends")
    void getMutualFriends(@Header("Facebook-Token") String faceToken, @Path("id") String faceId, Callback<FacebookFriendForJson> cb);

    @GET("/user/intranetPhotoUrl")
    void getIntranetPhotoUrl(Callback<UrlForJson> cb);

    //ride routes
//    @Headers("Caronae/4 (Android; 6.0)")
    //TODO: Check Header
    @POST("/ride")
    void offerRide(@Header("Header") String header, @Body Ride ride, Callback<List<Ride>> cb);

    @DELETE("/ride/{rideId}")
    void deleteRide(@Path("rideId") String rideId, Callback<Response> cb);

    @DELETE("/ride/allFromUser/{stub}/{going}")
    void deleteAllRidesFromUser(@Path("stub") String stub, @Path("going") boolean going, Callback<Response> cb);

    @DELETE("/ride/allFromRoutine/{routineId}")
    void deleteAllRidesFromRoutine(@Path("routineId") String routineId, Callback<Response> cb);

    @POST("/ride/listFiltered")
    void listFiltered(@Body RideSearchFiltersForJson rideSearchFilters, Callback<List<RideForJson>> cb);

    @GET("/ride/all")
    void listAllRides(Callback<List<RideForJson>> cb);

    @POST("/ride/requestJoin")
    void requestJoin(@Body RideIdForJson rideId, Callback<Response> cb);

    @GET("/ride/getRequesters/{rideId}")
    void getRequesters(@Path("rideId") String rideId, Callback<List<User>> cb);

    @POST("/ride/answerJoinRequest")
    void answerJoinRequest(@Body JoinRequestIDsForJson joinRequestIDsForJson, Callback<Response> cb);

    @GET("/ride/getMyActiveRides")
    void getMyActiveRides(Callback<List<RideForJson>> cb);

    @POST("/ride/leaveRide")
    void leaveRide(@Body RideIdForJson rideId, Callback<Response> cb);

    @POST("/ride/finishRide")
    void finishRide(@Body RideIdForJson rideId, Callback<Response> cb);

    @GET("/ride/getRidesHistory")
    void getRidesHistory(Callback<List<RideHistoryForJson>> cb);

    @GET("/ride/getRidesHistoryCount/{userId}")
    void getRidesHistoryCount(@Path("userId") String userId, Callback<HistoryRideCountForJson> cb);

    @POST("/ride/saveFeedback")
    void saveFeedback(@Body RideFeedbackForJson rideFeedbackForJson, Callback<Response> cb);

    @DELETE("/ride/joinRequests")
    void deleteJoinRequests(@Body List<RideIdForJson> rideIdsList, Callback<Response> cb);

    //falae route
    @POST("/falae/sendMessage")
    void falaeSendMessage(@Body FalaeMsgForJson msg, Callback<Response> cb);
}
