package br.ufrj.caronae;

import android.support.annotation.Nullable;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ModuloUsuario.class})
public interface ComponenteUsuario {
    @Nullable
    Usuario proverUsuario();
}
