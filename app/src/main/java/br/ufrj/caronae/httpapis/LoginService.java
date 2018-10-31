package br.ufrj.caronae.httpapis;

import android.util.Log;

import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.UserForJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginService {
    public void signIn(String id, String token, ServiceCallback<User> callback) {
        SharedPref.saveUserJWTToken(token);

        CaronaeAPI.service(null).getUser(id).enqueue(new Callback<UserForJson>()
        {
            @Override
            public void onResponse(Call<UserForJson> call, Response<UserForJson> response)
            {
                if (!response.isSuccessful()) {
                    Log.e("Login", "Failure: " + response.message());
                    callback.fail(null);
                    return;
                }

                UserForJson userJson = response.body();
                if (userJson == null || userJson.getUser() == null)
                {
                    callback.fail(null);
                    return;
                }

                User user = userJson.getUser();
                SharedPref.saveUser(user);
                SharedPref.saveNotifPref("true");
                callback.success(user);
            }

            @Override
            public void onFailure(Call<UserForJson> call, Throwable t) {
                callback.fail(t);
            }
        });
    }
}
