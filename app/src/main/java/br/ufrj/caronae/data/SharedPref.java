package br.ufrj.caronae.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;

public class SharedPref {

    private static final String INSTITUTION_KEY                      = "institution";
    private static final String GOING_LABEL_KEY                      = "goingLabel";
    private static final String LEAVING_LABEL_KEY                    = "leavingLabel";
    private static final String IS_GOING_PREF_KEY                    = "going";
    private static final String USER_PREF_KEY                        = "user";
    private static final String LAST_RIDE_OFFER_GOING_PREF_KEY       = "lastRideOfferGoing";
    private static final String LAST_RIDE_OFFER_NOT_GOING_PREF_KEY   = "lastRideOfferNotGoing";
    private static final String LAST_RIDE_SEARCH_FILTERS_PREF_KEY    = "lastRideSearchFilters";
    private static final String USER_PIC_SAVED_KEY                   = "SavedImage";
    private static final String TOKEN_PREF_KEY                       = "token";
    private static final String JWT_TOKEN_PREF_KEY                   = "jwtToken";
    private static final String IDUFRJ_PREF_KEY                      = "idUfrj";
    private static final String RIDESTAKEN_PREF_KEY                  = "taken";
    private static final String RIDESOFFERED_PREF_KEY                = "ridesOffered";
    private static final String NOTIFICATIONS_ON_PREF_KEY            = "notifOn";
    private static final String RM_RIDE_LIST                         = "removeRideFromList";
    private static final String RIDE_FILTER_CENTER_KEY               = "filterCenter";
    private static final String RIDE_FILTER_LOCATION_KEY             = "filterLocation";
    private static final String RIDE_SEARCH_CENTER_KEY               = "searchCenter";
    private static final String RIDE_SEARCH_LOCATION_KEY             = "searchLocation";
    private static final String RIDE_SEARCH_DATE_KEY                 = "searchDate";
    private static final String RIDE_SEARCH_TIME_KEY                 = "searchTime";
    public static final String RIDE_FILTER_PREF_KEY                  = "filter";
    public static final String MISSING_PREF                          = "missing";
    public static final String TOPIC_GERAL                           = "general";
    public static final String PLACE_KEY                             = "preloadedplaces";
    public static final String MYPENDINGRIDESID_KEY                  = "preloadedmypendingridesid";
    public static final String MYACTIVERIDESID_KEY                   = "preloadedmyactiveridesid";
    private static String CHAT_ACT_STATUS                            = "chatStatus";
    public static String NAV_INDICATOR                               = "AllRides";
    public static String FRAGMENT_INDICATOR                          = "";
    public static String LOCATION_INFO                               = "";
    public static String CAMPI_INFO                                  = "";
    public static String isGoing                                     = "1";
    public static Date lastAllRidesUpdate                            = null;
    public static int lastMyRidesUpdate                              = 0;
    public static int lastSearchRidesUpdate                          = 0;
    public static boolean OPEN_ALL_RIDES                             = false;
    public static boolean OPEN_MY_RIDES                              = false;
    public static ArrayList<RideForJson> ALL_RIDES_GOING             = new ArrayList<>();
    public static ArrayList<RideForJson> ALL_RIDES_LEAVING           = new ArrayList<>();
    public static List<RideForJson> MY_RIDES_PENDING                 = new ArrayList<>();
    public static List<RideForJson> MY_RIDES_ACTIVE                  = new ArrayList<>();
    public static List<RideForJson> MY_RIDES_OFFERED                 = new ArrayList<>();

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.getInst());
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

    public static boolean checkExistence(String key)
    {
        return getSharedPreferences().contains(key);
    }

    public static void removeAllPrefButGcm() {
        removePref(USER_PREF_KEY);
        removePref(LAST_RIDE_OFFER_GOING_PREF_KEY);
        removePref(LAST_RIDE_OFFER_NOT_GOING_PREF_KEY);
        removePref(LAST_RIDE_SEARCH_FILTERS_PREF_KEY);
        removePref(TOKEN_PREF_KEY);
        removePref(JWT_TOKEN_PREF_KEY);
        removePref(NOTIFICATIONS_ON_PREF_KEY);
        removePref(USER_PIC_SAVED_KEY);
        removePref(RM_RIDE_LIST);
        removePref(IS_GOING_PREF_KEY);
        removePref(INSTITUTION_KEY);
        removePref(GOING_LABEL_KEY);
        removePref(LEAVING_LABEL_KEY);
        removePref(RIDE_FILTER_PREF_KEY);
        removePref(RIDE_FILTER_CENTER_KEY);
        removePref(RIDE_FILTER_LOCATION_KEY);
        removePref(MYPENDINGRIDESID_KEY);
        removePref(MYACTIVERIDESID_KEY);
        removePref(PLACE_KEY);
        NAV_INDICATOR = "AllRides";
        CHAT_ACT_STATUS = "chatStatus";
        FRAGMENT_INDICATOR = "";
        LOCATION_INFO = "";
        CAMPI_INFO = "";
        isGoing = "1";
        lastAllRidesUpdate = null;
        lastMyRidesUpdate = 0;
        lastSearchRidesUpdate = 0;
        OPEN_ALL_RIDES = false;
        OPEN_MY_RIDES = false;
        ALL_RIDES_GOING = new ArrayList<>();
        ALL_RIDES_LEAVING = new ArrayList<>();
        MY_RIDES_PENDING = new ArrayList<>();
        MY_RIDES_ACTIVE = new ArrayList<>();
        MY_RIDES_OFFERED = new ArrayList<>();
    }

    public static void setChatActIsForeground(boolean isForeground){
        putBooleanPref(CHAT_ACT_STATUS, isForeground);
    }

    public static boolean getChatActIsForeground(){
        return getBooleanPref(CHAT_ACT_STATUS);
    }

    public static void setRidesTaken(String ridesTaken)
    {
        putPref(RIDESTAKEN_PREF_KEY, ridesTaken);
    }

    public static void setRidesOffered(String ridesOffered)
    {
        putPref(RIDESOFFERED_PREF_KEY, ridesOffered);
    }

    public static String getRidesTaken()
    {
        return getPref(RIDESTAKEN_PREF_KEY);
    }

    public static String getRidesOffered()
    {
        return getPref(RIDESOFFERED_PREF_KEY);
    }

    public static void setSavedPic(boolean open)
    {
        putBooleanPref(USER_PIC_SAVED_KEY, open);
    }

    public static boolean getSavedPic()
    {
        return getBooleanPref(USER_PIC_SAVED_KEY);
    }

    public static void setIsGoingPref(String isGoing)
    {
        putPref(IS_GOING_PREF_KEY, isGoing);
    }

    public static void setPlace(PlacesForJson place)
    {
        Gson gson = new Gson();
        String placeToJson = gson.toJson(place);
        putPref(PLACE_KEY, placeToJson);
    }

    public static PlacesForJson getPlace()
    {
        Gson gson = new Gson();
        String placeToObject = getPref(PLACE_KEY);
        return gson.fromJson(placeToObject, PlacesForJson.class);
    }

    public static String getGoingLabel()
    {
        if(getSharedPreferences().contains(PLACE_KEY))
            return getPlace().getInstitutions().getGoing_label();
        else
            return null;
    }

    public static String getLeavingLabel()
    {
        if(getSharedPreferences().contains(PLACE_KEY))
            return getPlace().getInstitutions().getLeaving_label();
        else
            return null;
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

    public static void setFilterPref(boolean filtering){
        putBooleanPref(RIDE_FILTER_PREF_KEY, filtering);
    }

    public static Boolean getFiltersPref() {
        return getBooleanPref(RIDE_FILTER_PREF_KEY);
    }

    public static void saveUser(User user) {
        putPref(USER_PREF_KEY, new Gson().toJson(user));
    }

    public static String getUserToken() {
        return getPref(TOKEN_PREF_KEY);
    }

    public static String getUserJWTToken() {
        if (!checkExistence(JWT_TOKEN_PREF_KEY)) {
            return null;
        }
        return getPref(JWT_TOKEN_PREF_KEY);
    }

    public static void saveUserToken(String token) {
        putPref(TOKEN_PREF_KEY, token.toUpperCase());
    }

    public static void saveUserJWTToken(String token) {
        putPref(JWT_TOKEN_PREF_KEY, token);
    }

    public static void saveUserIdUfrj(String idUfrj) {
        putPref(IDUFRJ_PREF_KEY, idUfrj);
    }

    public static void setLocationFilter(String location)
    {
        putPref(RIDE_FILTER_LOCATION_KEY, location);
    }

    public static String getLocationFilter()
    {
        return getPref(RIDE_FILTER_LOCATION_KEY);
    }

    public static void setCenterFilter(String center)
    {
        putPref(RIDE_FILTER_CENTER_KEY, center);
    }

    public static String getCenterFilter()
    {
        return getPref(RIDE_FILTER_CENTER_KEY);
    }

    public static void setCenterSearch(String center)
    {
        putPref(RIDE_SEARCH_CENTER_KEY, center);
    }

    public static String getCenterSearch()
    {
        return getPref(RIDE_SEARCH_CENTER_KEY);
    }

    public static void setLocationSearch(String location)
    {
        putPref(RIDE_SEARCH_LOCATION_KEY, location);
    }

    public static String getLocationSearch()
    {
        return getPref(RIDE_SEARCH_LOCATION_KEY);
    }

    public static void setDateSearch(String date)
    {
        putPref(RIDE_SEARCH_DATE_KEY, date);
    }

    public static String getDateSearch()
    {
        return getPref(RIDE_SEARCH_DATE_KEY);
    }

    public static void setTimeSearch(String time)
    {
        putPref(RIDE_SEARCH_TIME_KEY, time);
    }

    public static String getTimeSearch()
    {
        return getPref(RIDE_SEARCH_TIME_KEY);
    }

    public static void setMyPendingRidesId(List<Integer> myPendingRidesId)
    {
        String formatedIds = "";
        for(int i = 0; i < myPendingRidesId.size(); i++)
        {
            if(i != myPendingRidesId.size()-1)
            {
                formatedIds = formatedIds.concat(myPendingRidesId.get(i) + ",");
            }
            else
            {
                formatedIds = formatedIds.concat(Integer.toString(myPendingRidesId.get(i)));
            }
        }
        putPref(MYPENDINGRIDESID_KEY, formatedIds);
    }

    public static List<Integer> getMyPendingRidesId()
    {
        String[] listTextIds = getPref(MYPENDINGRIDESID_KEY).split(",");
        List<Integer> ids = new ArrayList<>();
        for(int i = 0; i < listTextIds.length; i++) {
            ids.add(Integer.parseInt(listTextIds[i]));
        }
        return ids;
    }

    public static void setMyActiveRidesId(List<Integer> myActiveRidesId)
    {
        String formatedIds = "";
        for(int i = 0; i < myActiveRidesId.size(); i++)
        {
            if(i != myActiveRidesId.size()-1)
            {
                formatedIds = formatedIds.concat(myActiveRidesId.get(i) + ",");
            }
            else
            {
                formatedIds = formatedIds.concat(Integer.toString(myActiveRidesId.get(i)));
            }
        }
        putPref(MYACTIVERIDESID_KEY, formatedIds);
    }

    public static List<Integer> getMyActiveRidesId()
    {
        String[] listTextIds = getPref(MYACTIVERIDESID_KEY).split(",");
        List<Integer> ids = new ArrayList<>();
        for(int i = 0; i < listTextIds.length; i++) {
            ids.add(Integer.parseInt(listTextIds[i]));
        }
        return ids;
    }
}
