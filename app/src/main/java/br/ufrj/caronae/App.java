package br.ufrj.caronae;

import com.google.gson.Gson;
import com.orm.SugarApp;

import br.ufrj.caronae.httpapis.ChatService;
import br.ufrj.caronae.httpapis.NetworkService;
import br.ufrj.caronae.models.User;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class App extends SugarApp {

    private static final String DEV_SERVER_ENDPOINT          = "http://dev.caronae.tic.ufrj.br/";
    private static final String MEUDIGOCEAN_PROD_ENDPOINT    = "http://45.55.46.90:80/";
    private static final String MEUDIGOCEAN_DEV_ENDPOINT     = "http://45.55.46.90:8080/";
    private static final String LOCAL_SERV_ENDPOINT          = "http://192.168.0.13/";
    private static final String TIC_ENDPOINT                 = "https://api.caronae.ufrj.br/";

    private static final String GCM_ENDPOINT = "https://android.googleapis.com/gcm";
    private static final String GCM_API_KEY = "AIzaSyBtGz81bar_LcwtN_fpPTKRMBL5glp2T18";

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
            String userJson = SharedPref.getUserPref();
            if (!userJson.equals(SharedPref.MISSING_PREF))
                user = new Gson().fromJson(userJson, User.class);
        }

        return user;
    }

    public static String getHost() {
        //return MEUDIGOCEAN_DEV_ENDPOINT;
        //return MEUDIGOCEAN_PROD_ENDPOINT;
        //return LOCAL_SERV_ENDPOINT;
        return DEV_SERVER_ENDPOINT;
        //return TIC_ENDPOINT;
    }

    public static NetworkService getNetworkService() {
        if (networkService == null) {
            String endpoint = getHost();

            networkService = new RestAdapter.Builder()
                    .setEndpoint(endpoint)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            if (App.isUserLoggedIn()) {
                                request.addHeader("token", SharedPref.getUserToken());
                            }
                        }
                    })
                    //.setLogLevel(RestAdapter.LogLevel.BASIC)
                    //.setLogLevel(RestAdapter.LogLevel.HEADERS)
                    //.setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
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
