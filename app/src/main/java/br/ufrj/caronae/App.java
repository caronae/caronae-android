package br.ufrj.caronae;

public class App extends com.activeandroid.app.Application {

    public static final String LOGTAG = "caronae";

    private static App inst;

    public App() {
        inst = this;
    }

    public static App inst() {
        return inst;
    }

    User getUser() {
        return DaggerUserComponent.create().provideUser();
    }

    public boolean isUserLoggedIn() {
        return getUser() != null;
    }
}
