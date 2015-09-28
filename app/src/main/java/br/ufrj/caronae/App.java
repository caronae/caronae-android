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

    public static final String LOGTAG = "caronae";

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
            String userJson = getSharedPref("user");
            if (!userJson.equals("missing"))
                user = new Gson().fromJson(userJson, User.class);
        }
        return user;
    }

    public static boolean isUserLoggedIn() {
        return getUser() != null;
    }

    public static void logOut() {
        user = null;
        deleteUser();
        Ride.deleteAll(Ride.class);
    }

    public static NetworkService getNetworkService() {
        if (component == null)
            component = DaggerNetworkComponent.builder().networkModule(new NetworkModule("http://private-5b9ed6-caronae.apiary-mock.com")).build();
        return component.provideNetworkService();
    }

    public static void putSharedPref(String key, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(inst());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSharedPref(String lastRideSearchFilters) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(inst());
        return sharedPref.getString(lastRideSearchFilters, "missing");
    }

    public static void saveUser(User user) {
        putSharedPref("user", new Gson().toJson(user));
    }

    private static void deleteUser() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(inst());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("user");
        editor.apply();
    }
}
