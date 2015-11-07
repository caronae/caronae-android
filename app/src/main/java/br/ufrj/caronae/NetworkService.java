package br.ufrj.caronae;

import java.util.List;

import br.ufrj.caronae.models.JoinRequestIDsForJson;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideIdForJson;
import br.ufrj.caronae.models.RideOfferForJson;
import br.ufrj.caronae.models.RideSearchFiltersForJson;
import br.ufrj.caronae.models.RideWithUsersForJson;
import br.ufrj.caronae.models.TokenForJson;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.UserWithRidesForJson;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface NetworkService {
    @POST("/auth")
    void sendToken(@Body TokenForJson token, Callback<UserWithRidesForJson> cb);

    @PUT("/user/a")
    void updateUser(@Body User user, Callback<Response> cb);

    @POST("/ride")
    void offerRide(@Body Ride ride, Callback<List<Ride>> cb);

    @POST("/ride/list")
    void getRideOffers(@Body RideSearchFiltersForJson rideSearchFilters, Callback<List<RideOfferForJson>> cb);

    @POST("/ride/requestJoin")
    void sendJoinRequest(@Body RideIdForJson rideId, Callback<Response> cb);

    @POST("/ride/delete")
    void deleteRide(@Body RideIdForJson rideId, Callback<Response> cb);

    @POST("/ride/getRequesters")
    void getRequesters(@Body RideIdForJson rideId, Callback<List<User>> cb);

    @POST("/ride/answerJoinRequest")
    void answerJoinRequest(@Body JoinRequestIDsForJson joinRequestIDsForJson, Callback<Response> cb);

    @POST("/ride/getMyActiveRides")
    void getMyActiveRides(@Body User user, Callback<List<RideWithUsersForJson>> cb);

    @POST("/ride/leaveRide")
    void leaveRide(@Body RideIdForJson rideId, Callback<Response> cb);

    @GET("/signup/{name}/{token}")
    void signUp(@Path("name") String name, @Path("token") String token, Callback<Response> cb);

    @POST("/user/saveGcmToken")
    void sendGcmToken(@Body TokenForJson token, Callback<Response> cb);
}
