package br.ufrj.caronae;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.orm.SugarApp;

import br.ufrj.caronae.components.DaggerNetworkComponent;
import br.ufrj.caronae.components.NetworkComponent;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.modules.NetworkModule;

public class App extends SugarApp {

    public static final String USER_PREF_KEY = "user";
    public static final String MISSING_PREF = "missing";

    private static App inst;
    private static User user;
    private static NetworkComponent component;

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
        Ride.deleteAll(Ride.class);
    }

    public static void saveUser(User user) {
        putPref("user", new Gson().toJson(user));
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
        if (component == null)
            component = DaggerNetworkComponent.builder().networkModule(new NetworkModule("http://private-5b9ed6-caronae.apiary-mock.com")).build();
        return component.provideNetworkService();
    }
}
