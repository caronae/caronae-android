package br.ufrj.caronae.asyncs;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJsonDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Luis on 6/8/2017.
 */

public class FetchMyOfferedRidesService extends IntentService {


    public FetchMyOfferedRidesService() {
        super("FetchMyOfferedRidesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        CaronaeAPI.service(App.getInst()).getOfferedRides(App.getUser().getDbId() + "")
                .enqueue(new Callback<RideForJsonDeserializer>() {
                    @Override
                    public void onResponse(Call<RideForJsonDeserializer> call, Response<RideForJsonDeserializer> response) {
                        if (response.isSuccessful()) {
                            RideForJsonDeserializer deserializer = response.body();
                            List<RideForJson> rides = deserializer.getRides();
                            if (rides != null) {
                                Ride.deleteAll(Ride.class);
                                for (RideForJson ride : rides) {
                                    new Ride(ride).save();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RideForJsonDeserializer> call, Throwable t) {
                    }
                });
    }
}
