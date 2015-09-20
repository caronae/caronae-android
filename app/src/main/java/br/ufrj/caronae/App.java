package br.ufrj.caronae;

import com.orm.SugarApp;
import com.orm.query.Select;

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
        if (user == null)
            user = Select.from(User.class).first();
        return user;
    }

    public static boolean isUserLoggedIn() {
        return getUser() != null;
    }

    public static ApiaryService getApiaryService() {
        if (component == null)
            component = DaggerNetworkComponent.builder().networkModule(new NetworkModule("http://private-5b9ed6-caronae.apiary-mock.com")).build();
        return component.provideApiaryService();
    }

    public static void logOut() {
        user = null;
        User.deleteAll(User.class);
        Ride.deleteAll(Ride.class);
    }
}
