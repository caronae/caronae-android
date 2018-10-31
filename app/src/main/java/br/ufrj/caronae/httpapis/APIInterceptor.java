package br.ufrj.caronae.httpapis;

import android.os.Build;

import java.io.IOException;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
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

        String jwtToken = SharedPref.getUserJWTToken();
        if (!isEmpty(jwtToken)) {
            builder.header("Authorization", "Bearer " + jwtToken);
        } else {
            builder.header("token", SharedPref.getUserToken());
        }

        return chain.proceed(builder.build());
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
