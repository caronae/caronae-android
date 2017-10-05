package br.ufrj.caronae;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import br.ufrj.caronae.models.User;

public class SharedPref {

    private static final String USER_PREF_KEY                        = "user";
    private static final String LAST_RIDE_OFFER_GOING_PREF_KEY       = "lastRideOfferGoing";
    private static final String LAST_RIDE_OFFER_NOT_GOING_PREF_KEY   = "lastRideOfferNotGoing";
    private static final String LAST_RIDE_SEARCH_FILTERS_PREF_KEY    = "lastRideSearchFilters";
    private static final String LAST_RIDE_FILTERS_PREF_KEY           = "lastRideFilters";
    public static final String RIDE_FILTER_PREF_KEY                 = "filter";
    private static final String TOKEN_PREF_KEY                       = "token";
    private static final String IDUFRJ_PREF_KEY                       = "idUfrj";
    private static final String NOTIFICATIONS_ON_PREF_KEY            = "notifOn";
    private static final String DRAWER_PIC_PREF                      = "drawerPic";
    private static final String RM_RIDE_LIST                         = "removeRideFromList";
    public static final String MISSING_PREF                          = "missing";
    public static final String TOPIC_GERAL                           = "general";
    public static final String MY_RIDE_LIST_KEY                            = "myRides";
    public static String CHAT_ACT_STATUS                            = "chatStatus";
    public static String FRAGMENT_INDICATOR                            = "";
    public static final String DIALOG_CAMPUS_SEARCH_KEY            = "campus";
    public static final String DIALOG_CENTER_SEARCH_KEY            = "centro";
    public static final String DIALOG_DISMISS_KEY                  = "dismiss_key";
    public static final String MY_RIDES_DELETE_TUTORIAL            = "my_rides_delete_tut";



    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.inst());
    }

    private static SharedPreferences.Editor getSharedPrefEditor() {
        return getSharedPreferences().edit();
    }

    public static void putPref(String key, String value) {
        getSharedPrefEditor().putString(key, value).apply();
    }

    public static void putBooleanPref(String key, boolean value) {
        getSharedPrefEditor().putBoolean(key, value).apply();
    }

    public static String getPref(String key) {
        return getSharedPreferences().getString(key, MISSING_PREF);
    }

    public static boolean getBooleanPref(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    private static void removePref(String key) {
        getSharedPrefEditor().remove(key).apply();
    }

    public static void saveNotifPref(String value) {
        putPref(NOTIFICATIONS_ON_PREF_KEY, value);
    }

    public static String getNotifPref() {
        return getPref(NOTIFICATIONS_ON_PREF_KEY);
    }

    public static void saveLastRideGoingPref(String lastRideOffer) {
        putPref(LAST_RIDE_OFFER_GOING_PREF_KEY, lastRideOffer);
    }

    public static void saveDialogSearchPref(String key, String search) {
        putPref(key, search);
    }

    public static String getDialogSearchPref(String key) {
        return getPref(key);
    }

    public static void saveDialogTypePref(String key, String type) {
        putPref(key, type);
    }

    public static String getDialogTypePref(String Type) {
        return getPref(Type);
    }

    public static String getLastRideGoingPref() {
        return getPref(LAST_RIDE_OFFER_GOING_PREF_KEY);
    }

    public static void saveLastRideNotGoingPref(String lastRideOffer) {
        putPref(LAST_RIDE_OFFER_NOT_GOING_PREF_KEY, lastRideOffer);
    }

    public static String getLastRideNotGoingPref() {
        return getPref(LAST_RIDE_OFFER_NOT_GOING_PREF_KEY);
    }

    public static String getUserPref() {
        return getPref(USER_PREF_KEY);
    }

    public static String getLastRideSearchFiltersPref() {
        return getPref(LAST_RIDE_SEARCH_FILTERS_PREF_KEY);
    }

    public static String getRideFiltersPref() {
        return getPref(LAST_RIDE_FILTERS_PREF_KEY);
    }

    public static void saveLastFiltersPref(String lastFilters){
        putPref(LAST_RIDE_FILTERS_PREF_KEY, lastFilters);
    }

    public static void saveFilterPref(String filters){
        putPref(RIDE_FILTER_PREF_KEY, filters);
    }

    public static String getFiltersPref() {
        return getPref(RIDE_FILTER_PREF_KEY);
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

    public static String getUserIdUfrj() {
        return getPref(IDUFRJ_PREF_KEY);
    }

    public static void saveUserToken(String token) {
        putPref(TOKEN_PREF_KEY, token.toUpperCase());
    }

    public static void saveUserIdUfrj(String idUfrj) {
        putPref(IDUFRJ_PREF_KEY, idUfrj);
    }

    public static void saveDrawerPic(String profilePicUrl) {
        putPref(DRAWER_PIC_PREF, profilePicUrl);
    }

    public static String getDrawerPic() {
        return getPref(DRAWER_PIC_PREF);
    }

    public static void saveRemoveRideFromList(String profilePicUrl) {
        putPref(RM_RIDE_LIST, profilePicUrl);
    }

    public static String getRemoveRideFromList() {
        return getPref(RM_RIDE_LIST);
    }

    public static void removeRemoveRideFromList() {
        removePref(RM_RIDE_LIST);
    }

    public static void removeAllPrefButGcm() {
        removePref(USER_PREF_KEY);
        removePref(LAST_RIDE_OFFER_GOING_PREF_KEY);
        removePref(LAST_RIDE_OFFER_NOT_GOING_PREF_KEY);
        removePref(LAST_RIDE_SEARCH_FILTERS_PREF_KEY);
        removePref(TOKEN_PREF_KEY);
        removePref(NOTIFICATIONS_ON_PREF_KEY);
        removePref(DRAWER_PIC_PREF);
        removePref(RM_RIDE_LIST);
    }

    public static void setChatActIsForeground(boolean isForeground){
        putBooleanPref(CHAT_ACT_STATUS, isForeground);
    }

    public static boolean getChatActIsForeground(){
        return getBooleanPref(CHAT_ACT_STATUS);
    }
}
