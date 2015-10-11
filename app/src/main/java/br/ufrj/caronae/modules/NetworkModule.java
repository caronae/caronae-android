package br.ufrj.caronae.modules;

import javax.inject.Singleton;

import br.ufrj.caronae.App;
import br.ufrj.caronae.NetworkService;
import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
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
                .setEndpoint(endpoint).setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (App.isUserLoggedIn()) {
                            request.addHeader("token", App.getUserToken());
                        }
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                //.setLogLevel(RestAdapter.LogLevel.HEADERS)
                //.setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
                //.setLogLevel(RestAdapter.LogLevel.FULL)
                /*.
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
