package br.ufrj.caronae;

import java.util.List;

import br.ufrj.caronae.models.modelsforjson.JoinRequestIDsForJson;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import br.ufrj.caronae.models.modelsforjson.RideOfferForJson;
import br.ufrj.caronae.models.modelsforjson.RideSearchFiltersForJson;
import br.ufrj.caronae.models.modelsforjson.RideWithUsersForJson;
import br.ufrj.caronae.models.modelsforjson.TokenForJson;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.UserWithRidesForJson;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface NetworkService {
    //user routes
    @GET("/user/signup/{name}/{token}")
    void signUp(@Path("name") String name, @Path("token") String token, Callback<Response> cb);

    @POST("/user/login")
    void login(@Body TokenForJson token, Callback<UserWithRidesForJson> cb);

    @PUT("/user/update")
    void updateUser(@Body User user, Callback<Response> cb);

    @PUT("/user/saveGcmToken")
    void saveGcmToken(@Body TokenForJson token, Callback<Response> cb);

    @PUT("/user/clearGcmToken")
    void clearGcmToken(@Body TokenForJson token, Callback<Response> cb);

    //ride routes
    @POST("/ride")
    void offerRide(@Body Ride ride, Callback<List<Ride>> cb);

    @DELETE("/ride/{rideId}")
    void deleteRide(@Path("rideId") String rideId, Callback<Response> cb);

    @POST("/ride/listFiltered")
    void listFiltered(@Body RideSearchFiltersForJson rideSearchFilters, Callback<List<RideOfferForJson>> cb);

    @POST("/ride/requestJoin")
    void requestJoin(@Body RideIdForJson rideId, Callback<Response> cb);

    @GET("/ride/getRequesters/{rideId}")
    void getRequesters(@Path("rideId") String rideId, Callback<List<User>> cb);

    @POST("/ride/answerJoinRequest")
    void answerJoinRequest(@Body JoinRequestIDsForJson joinRequestIDsForJson, Callback<Response> cb);

    @GET("/ride/getMyActiveRides")
    void getMyActiveRides(Callback<List<RideWithUsersForJson>> cb);

    @POST("/ride/leaveRide")
    void leaveRide(@Body RideIdForJson rideId, Callback<Response> cb);
}
