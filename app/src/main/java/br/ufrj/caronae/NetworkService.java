package br.ufrj.caronae;

import java.util.List;

import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideIdForJson;
import br.ufrj.caronae.models.RideOffer;
import br.ufrj.caronae.models.RideSearchFilters;
import br.ufrj.caronae.models.TokenForJson;
import br.ufrj.caronae.models.User;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.PUT;

public interface NetworkService {
    @POST("/auth")
    void sendToken(@Body TokenForJson token, Callback<User> cb);

    @PUT("/user/a")
    void updateUser(@Body User user, Callback<Response> cb);

    @POST("/ride")
    void offerRide(@Body Ride ride, Callback<String> cb);

    @POST("/ride/list")
    void getRideOffers(@Body RideSearchFilters rideSearchFilters, Callback<List<RideOffer>> cb);

    @POST("/ride/requestJoin")
    void sendJoinRequest(@Body RideIdForJson rideId, Callback<Response> cb);

    @POST("/ride/delete")
    void deleteRide(@Body RideIdForJson rideId, Callback<Response> cb);
}
