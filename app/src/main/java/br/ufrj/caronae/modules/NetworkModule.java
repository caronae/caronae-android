package br.ufrj.caronae.modules;

import javax.inject.Singleton;

import br.ufrj.caronae.NetworkService;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module
public class NetworkModule {
    private final String endpoint;

    public NetworkModule(String endpoint) {
        this.endpoint = endpoint;
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.BASIC)/*.
                        setLog(new RestAdapter.Log() {
                            @Override
                            public void log(String msg) {
                                Log.i(App.LOGTAG, msg);
                            }
                        })*/
                .build();
    }

    @Provides
    @Singleton
    NetworkService provideNetworkService(RestAdapter restAdapter) {
        return restAdapter.create(NetworkService.class);
    }
}
