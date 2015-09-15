package br.ufrj.caronae.modules;

import android.support.annotation.Nullable;

import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Singleton;

import br.ufrj.caronae.models.User;
import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {
    @Provides
    @Singleton
    @Nullable
    User provideLoggedInUser() {
        return Select.from(User.class).first();
    }
}
