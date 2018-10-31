package br.ufrj.caronae.httpapis;

import android.content.Context;
import java.io.IOException;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static android.text.TextUtils.isEmpty;

class APIInterceptor implements Interceptor {
    private Context context;

    public APIInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("User-Agent", Util.getHeaderForHttp(context))
                .method(original.method(), original.body());

        String jwtToken = SharedPref.getUserJWTToken();
        if (!isEmpty(jwtToken)) {
            builder.header("Authorization", "Bearer " + jwtToken);
        } else {
            builder.header("token", SharedPref.getUserToken());
        }

        return chain.proceed(builder.build());
    }
}
