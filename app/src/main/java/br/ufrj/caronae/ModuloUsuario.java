package br.ufrj.caronae;

import com.activeandroid.query.Select;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ModuloUsuario {
    @Provides
    @Singleton
    Usuario proverUsuarioLogado() {
        return new Select().from(Usuario.class).executeSingle();
    }
}
