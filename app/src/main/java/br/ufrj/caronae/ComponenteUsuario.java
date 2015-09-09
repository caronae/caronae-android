package br.ufrj.caronae;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ModuloUsuario.class})
public interface ComponenteUsuario {
    Usuario proverUsuario();
}
