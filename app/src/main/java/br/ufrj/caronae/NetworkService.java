package br.ufrj.caronae;

import java.util.List;

import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideOffer;
import br.ufrj.caronae.models.RideSearchFilters;
import br.ufrj.caronae.models.User;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface NetworkService {
    @POST("/auth")
    void sendToken(@Body String token, Callback<User> cb);

    @POST("/user")
    void updateUser(@Body User user, Callback<Response> cb);

    @POST("/ride")
    void offerRide(@Body Ride ride, Callback<Response> cb);

    @POST("/ride/list")
    void getRideOffers(@Body RideSearchFilters rideSearchFilters, Callback<List<RideOffer>> cb);
}
