package br.ufrj.caronae.httpapis;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import br.ufrj.caronae.App;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class APIInterceptor implements Interceptor {
    private Context context;

    public APIInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        if (App.isUserLoggedIn()) {
            Request request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("token", SharedPref.getUserToken())
                    .header("User-Agent", Util.getHeaderForHttp(context))
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        }
        Request request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("User-Agent", Util.getHeaderForHttp(context))
                .method(original.method(), original.body())
                .build();

        Log.e("Specify TLS: ",""+chain.proceed(request).protocol());
        return chain.proceed(request);
    }
}
