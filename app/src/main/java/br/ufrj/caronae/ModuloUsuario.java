package br.ufrj.caronae;

import android.support.annotation.Nullable;

import com.activeandroid.query.Select;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ModuloUsuario {
    @Provides
    @Singleton
    @Nullable
    Usuario proverUsuarioLogado() {
        return new Select().from(Usuario.class).executeSingle();
    }
}
