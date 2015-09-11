package br.ufrj.caronae.components;

import android.support.annotation.Nullable;

import javax.inject.Singleton;

import br.ufrj.caronae.User;
import br.ufrj.caronae.modules.UserModule;
import dagger.Component;

@Singleton
@Component(modules = {UserModule.class})
public interface UserComponent {
    @Nullable
    User provideLoggedInUser();
}
