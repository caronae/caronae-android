package br.ufrj.caronae;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orm.SugarApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.httpapis.ChatService;
import br.ufrj.caronae.httpapis.NetworkService;
import br.ufrj.caronae.models.User;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;

public class App extends SugarApp {

    public static final String USER_PREF_KEY                        = "user";
    public static final String LAST_RIDE_OFFER_PREF_KEY             = "lastRideOffer";
    public static final String LAST_RIDE_SEARCH_FILTERS_PREF_KEY    = "lastRideSearchFilters";
    public static final String TOKEN_PREF_KEY                       = "token";
    public static final String GCM_TOKEN_PREF_KEY                   = "gcmToken";
    public static final String NOTIFICATIONS_ON_PREF_KEY            = "notifOn";
    public static final String MISSING_PREF                         = "missing";

    public static final String APIARY_ENDPOINT              = "http://private-5b9ed6-caronae.apiary-mock.com";
    public static final String MEUDIGOCEAN_PROD_ENDPOINT    = "http://45.55.46.90:80/";
    public static final String MEUDIGOCEAN_DEV_ENDPOINT     = "http://45.55.46.90:8080/";
    public static final String LOCAL_SERV_ENDPOINT          = "http://192.168.0.13/";
    public static final String TIC_ENDPOINT                 = "http://web1.tic.ufrj.br/caronae/";

    public static final String GCM_ENDPOINT = "https://android.googleapis.com/gcm";
    public static final String GCM_API_KEY  = "AIzaSyBtGz81bar_LcwtN_fpPTKRMBL5glp2T18";

    private static App inst;
    private static User user;
    private static NetworkService networkService;
    private static ChatService chatService;
    private static MainThreadBus bus;

    public App() {
        inst = this;
    }

    public static App inst() {
        return inst;
    }

    public static boolean isUserLoggedIn() {
        return getUser() != null;
    }

    public static void clearUserVar() {
        user = null;
    }

    public static User getUser() {
        if (user == null) {
            String userJson = getPref(USER_PREF_KEY);
            if (!userJson.equals(MISSING_PREF))
                user = new Gson().fromJson(userJson, User.class);
        }

        return user;
    }

    public static void saveUser(User user) {
        putPref(USER_PREF_KEY, new Gson().toJson(user));
    }

    public static String getUserToken() {
        return getPref(TOKEN_PREF_KEY);
    }

    public static void saveUserToken(String token) {
        putPref(TOKEN_PREF_KEY, token);
    }

    public static String getUserGcmToken() {
        return getPref(GCM_TOKEN_PREF_KEY);
    }

    public static void saveUserGcmToken(String token) {
        putPref(GCM_TOKEN_PREF_KEY, token);
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(inst);
    }

    private static SharedPreferences.Editor getSharedPrefEditor() {
        return getSharedPreferences().edit();
    }

    public static void putPref(String key, String value) {
        getSharedPrefEditor().putString(key, value).apply();
    }

    public static void removePref(String key) {
        getSharedPrefEditor().remove(key).apply();
    }

    public static String getPref(String key) {
        return getSharedPreferences().getString(key, MISSING_PREF);
    }

    public static NetworkService getNetworkService() {
        if (networkService == null) {
            //String endpoint = MEUDIGOCEAN_DEV_ENDPOINT;
            //String endpoint = MEUDIGOCEAN_PROD_ENDPOINT;
            //String endpoint = LOCAL_SERV_ENDPOINT;
            String endpoint = TIC_ENDPOINT;

            networkService = new RestAdapter.Builder()
                    .setEndpoint(endpoint)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            if (App.isUserLoggedIn()) {
                                request.addHeader("token", App.getUserToken());
                            }
                        }
                    })
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
                    //.setLogLevel(RestAdapter.LogLevel.HEADERS)
                    //.setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
                    //.setLogLevel(RestAdapter.LogLevel.FULL)
                    .build()
                    .create(NetworkService.class);
        }

        return networkService;
    }

    public static ChatService getChatService() {
        if (chatService == null) {
            chatService = new RestAdapter.Builder()
                    .setEndpoint(GCM_ENDPOINT)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("Content-Type", "application/json");
                            request.addHeader("Authorization", "key=" + GCM_API_KEY);
                        }
                    })
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
                    //.setLogLevel(RestAdapter.LogLevel.HEADERS)
                    //.setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
                    //.setLogLevel(RestAdapter.LogLevel.FULL)
                    .build()
                    .create(ChatService.class);
        }

        return chatService;
    }

    public static MainThreadBus getBus() {
        if (bus == null) {
            bus = new MainThreadBus();
        }

        return bus;
    }
}
