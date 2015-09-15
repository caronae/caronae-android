package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.Ride;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OfferRideFrag extends Fragment {
    @Bind(R.id.origin_et)
    EditText origin_et;
    @Bind(R.id.destination_et)
    EditText destination_et;
    @Bind(R.id.date_et)
    EditText date_et;
    @Bind(R.id.time_et)
    EditText time_et;
    @Bind(R.id.slots_et)
    EditText slots_et;
    @Bind(R.id.hub_et)
    EditText hub_et;
    @Bind(R.id.description_et)
    EditText description_et;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;

    public OfferRideFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        String origin = origin_et.getText().toString();
        String destination = destination_et.getText().toString();
        String date = date_et.getText().toString();
        String time = time_et.getText().toString();
        String slots = slots_et.getText().toString();
        String hub = hub_et.getText().toString();
        String description = description_et.getText().toString();
        int id = radioGroup.getCheckedRadioButtonId();
        boolean go = id == R.id.go_rb;

        final Ride ride = new Ride(origin, destination, date, time, slots, hub, description, go);
        App.getApiaryService().offerRide(ride, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                ride.save();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(App.LOGTAG, error.getMessage());
            }
        });
    }
}
