package br.ufrj.caronae.httpapis;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.ufrj.caronae.Constants.API_PRODUCTION_BASE_URL;
import static java.util.concurrent.TimeUnit.SECONDS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class CaronaeAPI {
    public static final String BASE_URL = API_PRODUCTION_BASE_URL;
    //      public static final String BASE_URL = API_DEV_BASE_URL;

    private static CaronaeAPIService service;

    public static CaronaeAPIService service(final Context context) {
        if (service == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(BODY);
            APIInterceptor apiInterceptor = new APIInterceptor(context);

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(30, SECONDS)
                    .connectTimeout(30, SECONDS)
                    .addInterceptor(apiInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            service = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build()
                    .create(CaronaeAPIService.class);
        }

        return service;
    }
}
