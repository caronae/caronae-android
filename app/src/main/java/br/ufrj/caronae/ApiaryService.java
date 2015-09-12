package br.ufrj.caronae;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ApiaryService {
    @POST("/auth")
    void sendToken(@Body String token, Callback<User> cb);

    @POST("/user")
    void updateUser(@Body String user, Callback<User> cb);
}
