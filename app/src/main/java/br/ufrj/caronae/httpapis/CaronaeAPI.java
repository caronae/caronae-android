package br.ufrj.caronae.httpapis;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.ufrj.caronae.Constants.API_PRODUCTION_BASE_URL;
import static java.util.concurrent.TimeUnit.SECONDS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class CaronaeAPI {
    public static final String BASE_URL = API_PRODUCTION_BASE_URL;
            //public static final String BASE_URL = API_DEV_BASE_URL;

    private static CaronaeAPIService service;

    public static CaronaeAPIService service(final Context context) {
        if (service == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(BODY);
            APIInterceptor apiInterceptor = new APIInterceptor(context);

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(30, SECONDS)
                    .connectTimeout(30, SECONDS)
                    .addInterceptor(apiInterceptor)
                    .addInterceptor(loggingInterceptor);

            OkHttpClient client = enableTls12OnPreLollipop(builder)
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

    private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception e) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", e);
            }
        }

        return client;
    }
}
