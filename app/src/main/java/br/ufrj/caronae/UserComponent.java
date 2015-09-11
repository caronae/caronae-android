package br.ufrj.caronae;

import android.support.annotation.Nullable;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {UserModule.class})
public interface UserComponent {
    @Nullable
    User provideUser();
}
