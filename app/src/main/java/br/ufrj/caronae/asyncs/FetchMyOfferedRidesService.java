package br.ufrj.caronae.asyncs;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.modelsforjson.MyRidesForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchMyOfferedRidesService extends IntentService {


    public FetchMyOfferedRidesService() {
        super("FetchMyOfferedRidesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        CaronaeAPI.service().getMyRides(Integer.toString(App.getUser().getDbId()))
                .enqueue(new Callback<MyRidesForJson>() {
                    @Override
                    public void onResponse(Call<MyRidesForJson> call, Response<MyRidesForJson> response) {
                        if (response.isSuccessful()) {
                            MyRidesForJson data = response.body();
                            List<RideForJson> rides = data.getOfferedRides();
                            if (rides != null) {
                                Ride.deleteAll(Ride.class);
                                for (RideForJson ride : rides) {
                                    new Ride(ride).save();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyRidesForJson> call, Throwable t) {
                    }
                });
    }
}
