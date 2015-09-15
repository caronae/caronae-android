package br.ufrj.caronae;

import com.squareup.okhttp.Response;

import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ApiaryService {
    @POST("/auth")
    void sendToken(@Body String token, Callback<User> cb);

    @POST("/user")
    void updateUser(@Body User user, Callback<User> cb);

    @POST("/ride")
    void offerRide(@Body Ride ride, Callback<Ride> cb);
}
