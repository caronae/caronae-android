package br.ufrj.caronae.httpapis;

import android.os.Build;
import android.util.Log;

import java.io.IOException;

import br.ufrj.caronae.Util;
import br.ufrj.caronae.data.SharedPref;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static android.text.TextUtils.isEmpty;

class APIInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("User-Agent", userAgent())
                .method(original.method(), original.body());

        addAuthenticationHeaders(builder);

        Response response = chain.proceed(builder.build());
        parseAuthenticationHeaders(response);

        return response;
    }

    private void addAuthenticationHeaders(Request.Builder builder) {
        String jwtToken = SharedPref.getUserJWTToken();
        if (!isEmpty(jwtToken)) {
            builder.header("Authorization", "Bearer " + jwtToken);
        } else {
            builder.header("token", SharedPref.getUserToken());
        }
    }

    private void parseAuthenticationHeaders(Response response) {
        String authHeader = response.header("Authorization");
        if (isEmpty(authHeader)) {
            return;
        }

        String[] tokenParts = authHeader.split("Bearer ");
        if (tokenParts.length > 1) {
            String newToken = tokenParts[1];
            SharedPref.saveUserJWTToken(newToken);
            Log.d("API", "User token was refreshed.");
        }
    }

    private String userAgent() {
        String brand = Build.BRAND;
        brand = brand.substring(0, 1).toUpperCase() + brand.substring(1, brand.length());
        return "Caronae/"
                + Util.getAppVersionName()
                + " ("
                + brand
                + ": "
                + Build.MODEL
                + "; "
                + "Android: "
                + Build.VERSION.RELEASE
                + ")";
    }
}
