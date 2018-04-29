package br.ufrj.caronae.acts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.Campi;
import br.ufrj.caronae.models.Institution;
import br.ufrj.caronae.models.Zone;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceAct extends AppCompatActivity {

    List<Campi> campi;
    List<Zone> zones;
    Institution institution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        CaronaeAPI.service(getApplicationContext()).getPlaces()
                .enqueue(new Callback<PlacesForJson>() {
                    @Override
                    public void onResponse(Call<PlacesForJson> call, Response<PlacesForJson> response) {
                        if (response.isSuccessful()) {
                            PlacesForJson places = response.body();
                            campi = places.getCampi();
                            zones = places.getZones();
                            institution = places.getInstitutions();
                            for(int i = 0; i < zones.size(); i++)
                            {
                                Util.debug(zones.get(i).getName());
                                for(int j = 0; j < zones.get(i).getNeighborhoods().size(); j++)
                                {
                                    Util.debug(zones.get(i).getNeighborhoods().get(j));
                                }
                            }
                        }
                        else {
                            Log.e("REQUEST FAILED: ", "NO CONNECTION");
                        }
                    }
                    @Override
                    public void onFailure(Call<PlacesForJson> call, Throwable t) {
                        Log.e("ERROR: ", t.getMessage());
                    }
            });
    }
}
