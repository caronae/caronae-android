package br.ufrj.caronae;

import br.ufrj.caronae.components.DaggerNetworkComponent;
import br.ufrj.caronae.components.DaggerUserComponent;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.modules.NetworkModule;

public class App extends com.activeandroid.app.Application {

    public static final String LOGTAG = "caronae";

    private static App inst;

    public App() {
        inst = this;
    }

    public static App inst() {
        return inst;
    }

    public static User getUser() {
        return DaggerUserComponent.create().provideLoggedInUser();
    }

    public static boolean isUserLoggedIn() {
        return getUser() != null;
    }

    public static ApiaryService getApiaryService() {
        return DaggerNetworkComponent.builder().networkModule(new NetworkModule("http://private-5b9ed6-caronae.apiary-mock.com")).build().provideApiaryService();
    }
}
