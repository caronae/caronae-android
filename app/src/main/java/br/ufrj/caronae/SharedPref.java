package br.ufrj.caronae;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import br.ufrj.caronae.models.User;

public class SharedPref {

    private static final String USER_PREF_KEY                        = "user";
    private static final String LAST_RIDE_OFFER_PREF_KEY             = "lastRideOffer";
    private static final String LAST_RIDE_SEARCH_FILTERS_PREF_KEY    = "lastRideSearchFilters";
    private static final String TOKEN_PREF_KEY                       = "token";
    private static final String GCM_TOKEN_PREF_KEY                   = "gcmToken";
    private static final String NOTIFICATIONS_ON_PREF_KEY            = "notifOn";
    private static final String DRAWER_PIC_PREF                      = "drawerPic";
    public static final String MISSING_PREF                         = "missing";

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.inst());
    }

    private static SharedPreferences.Editor getSharedPrefEditor() {
        return getSharedPreferences().edit();
    }

    public static void putPref(String key, String value) {
        getSharedPrefEditor().putString(key, value).apply();
    }

    public static String getPref(String key) {
        return getSharedPreferences().getString(key, MISSING_PREF);
    }

    public static void removePref(String key) {
        getSharedPrefEditor().remove(key).apply();
    }

    public static void saveNotifPref(String value) {
        putPref(NOTIFICATIONS_ON_PREF_KEY, value);
    }

    public static String getNotifPref() {
        return getPref(NOTIFICATIONS_ON_PREF_KEY);
    }

    public static String getLastRidePref() {
        return getPref(LAST_RIDE_OFFER_PREF_KEY);
    }

    public static void saveLastRidePref(String lastRideOffer) {
        putPref(LAST_RIDE_OFFER_PREF_KEY, lastRideOffer);
    }

    public static String getUserPref() {
        return getPref(USER_PREF_KEY);
    }

    public static String getLastRideSearchFiltersPref() {
        return getPref(LAST_RIDE_SEARCH_FILTERS_PREF_KEY);
    }

    public static void saveLastRideSearchFiltersPref(String lastRideSearchFilters) {
        putPref(LAST_RIDE_SEARCH_FILTERS_PREF_KEY, lastRideSearchFilters);
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

    public static void saveDrawerPic(String profilePicUrl) {
        putPref(DRAWER_PIC_PREF, profilePicUrl);
    }

    public static String getDrawerPic() {
        return getPref(DRAWER_PIC_PREF);
    }
}
