package br.ufrj.caronae;

import android.support.annotation.Nullable;

import com.activeandroid.query.Select;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {
    @Provides
    @Singleton
    @Nullable
    User provideLoggedInUser() {
        return new Select().from(User.class).executeSingle();
    }
}
