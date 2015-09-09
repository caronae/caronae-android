package br.ufrj.caronae;

import com.activeandroid.query.Select;

public class App extends com.activeandroid.app.Application {

    private static App inst;

    public App() {
        inst = this;
    }

    public static App inst() {
        return inst;
    }

    Usuario getUsuario() {
        return new Select().from(Usuario.class).executeSingle();
    }

    public boolean isUsuarioLogado() {
        return getUsuario() != null;
    }
}
