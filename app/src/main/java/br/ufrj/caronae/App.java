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

import br.ufrj.caronae.components.DaggerNetworkComponent;
import br.ufrj.caronae.components.NetworkComponent;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.modules.NetworkModule;
import retrofit.client.Response;

public class App extends SugarApp {

    public static final String USER_PREF_KEY = "user";
    public static final String MISSING_PREF = "missing";
    public static final String LAST_RIDE_OFFER_PREF_KEY = "lastRideOffer";
    public static final String LAST_RIDE_SEARCH_FILTERS_PREF_KEY = "lastRideSearchFilters";
    public static final String TOKEN_PREF_KEY = "token";

    public static final String APIARY_ENDPOINT = "http://private-5b9ed6-caronae.apiary-mock.com";
    public static final String LUISDIGOCEAN_ENDPOINT = "http://104.131.31.224/";
    public static final String LOCAL_SERV_ENDPOINT = "http://192.168.0.13/";

    private static App inst;
    private static User user;
    private static NetworkComponent component;
    private static NetworkComponent component2;

    public App() {
        inst = this;
    }

    public static App inst() {
        return inst;
    }

    public static User getUser() {
        if (user == null) {
            String userJson = getPref(USER_PREF_KEY);
            if (!userJson.equals(MISSING_PREF))
                user = new Gson().fromJson(userJson, User.class);
        }

        return user;
    }

    public static boolean isUserLoggedIn() {
        return getUser() != null;
    }

    public static void logOut() {
        user = null;
        removePref(USER_PREF_KEY);
        removePref(LAST_RIDE_OFFER_PREF_KEY);
        removePref(LAST_RIDE_SEARCH_FILTERS_PREF_KEY);
        Ride.deleteAll(Ride.class);
    }

    public static void saveUser(User user) {
        putPref(USER_PREF_KEY, new Gson().toJson(user));
    }

    public static String getUserToken() {
        return getPref(TOKEN_PREF_KEY);
    }

    public static void saveToken(String token) {
        putPref(TOKEN_PREF_KEY, token);
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(inst());
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
        if (component == null) {
            String endpoint = LUISDIGOCEAN_ENDPOINT;
            //String endpoint = LOCAL_SERV_ENDPOINT;
            component = DaggerNetworkComponent.builder().networkModule(new NetworkModule(endpoint)).build();
        }
        return component.provideNetworkService();
    }

    public static NetworkService getApiaryNetworkService() {
        if (component2 == null)
            component2 = DaggerNetworkComponent.builder().networkModule(new NetworkModule(APIARY_ENDPOINT)).build();
        return component2.provideNetworkService();
    }

    public static void expandOrCollapse(final View v, boolean expand) {
        TranslateAnimation anim;
        if (expand) {
            anim = new TranslateAnimation(0.0f, 0.0f, -v.getHeight(), 0.0f);
            v.setVisibility(View.VISIBLE);
        } else {
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -v.getHeight());
            Animation.AnimationListener collapselistener = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }
            };

            anim.setAnimationListener(collapselistener);
        }

        // To Collapse
        //

        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }

    public static void printResponseBody(Response response) {
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = sb.toString();
    }

    public static void toast(String msg) {
        Toast.makeText(App.inst(), msg, Toast.LENGTH_SHORT).show();
    }
}
