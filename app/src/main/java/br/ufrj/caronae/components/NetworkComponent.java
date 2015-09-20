package br.ufrj.caronae.components;

import javax.inject.Singleton;

import br.ufrj.caronae.NetworkService;
import br.ufrj.caronae.modules.NetworkModule;
import dagger.Component;
import retrofit.RestAdapter;

@Singleton
@Component(modules = {NetworkModule.class})
public interface NetworkComponent {
    RestAdapter provideRestAdapter();
    NetworkService provideNetworkService();
}
