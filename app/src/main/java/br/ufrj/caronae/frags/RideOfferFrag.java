package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orm.query.Select;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.Ride;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RideOfferFrag extends Fragment {
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;

    @Bind(R.id.neighborhood_et)
    EditText neighborhood_et;
    @Bind(R.id.place_et)
    EditText place_et;
    @Bind(R.id.way_et)
    EditText way_et;
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

    @Bind(R.id.routine_cb)
    CheckBox routine_cb;
    @Bind(R.id.days_lo)
    LinearLayout days_lo;

    @Bind(R.id.monday_cb)
    CheckBox monday_cb;
    @Bind(R.id.tuesday_cb)
    CheckBox tuesday_cb;
    @Bind(R.id.wednesday_cb)
    CheckBox wednesday_cb;
    @Bind(R.id.thursday_cb)
    CheckBox thursday_cb;
    @Bind(R.id.friday_cb)
    CheckBox friday_cb;
    @Bind(R.id.saturday_cb)
    CheckBox saturday_cb;

    public RideOfferFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        ButterKnife.bind(this, view);

        String lastRideOffer = App.getSharedPref("lastRideOffer");
        if (!lastRideOffer.equals("missing")) {
            Ride ride = new Gson().fromJson(lastRideOffer, Ride.class);
            neighborhood_et.setText(ride.getNeighborhood());
            place_et.setText(ride.getPlace());
            way_et.setText(ride.getWay());
            date_et.setText(ride.getDate());
            time_et.setText(ride.getTime());
            slots_et.setText(ride.getSlots());
            hub_et.setText(ride.getHub());
            description_et.setText(ride.getDescription());
            radioGroup.check(ride.isGo() ? R.id.go_rb : R.id.back_rb);
            boolean isRoutine = ride.isRoutine();
            routine_cb.setChecked(isRoutine);
            if (isRoutine) {
                routineCb();
                monday_cb.setChecked(ride.isMonday());
                tuesday_cb.setChecked(ride.isTuesday());
                wednesday_cb.setChecked(ride.isWednesday());
                thursday_cb.setChecked(ride.isThursday());
                friday_cb.setChecked(ride.isFriday());
                saturday_cb.setChecked(ride.isSaturday());
            }
        }

        return view;
    }

    @OnClick(R.id.routine_cb)
    public void routineCb() {
        days_lo.setVisibility(routine_cb.isChecked() ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        String neighborhood = neighborhood_et.getText().toString();
        String place = place_et.getText().toString();
        String way = way_et.getText().toString();
        String date = date_et.getText().toString();
        String time = time_et.getText().toString();
        String slots = slots_et.getText().toString();
        String hub = hub_et.getText().toString();
        String description = description_et.getText().toString();
        int id = radioGroup.getCheckedRadioButtonId();
        boolean go = id == R.id.go_rb;
        boolean routine = routine_cb.isChecked();
        boolean[] routineDays = {monday_cb.isChecked(), tuesday_cb.isChecked(), wednesday_cb.isChecked(), thursday_cb.isChecked(), friday_cb.isChecked(), saturday_cb.isChecked()};

        final Ride ride = new Ride(neighborhood, place, way, date, time, slots, hub, description, go, routine, routineDays);

        String lastRideOffer = new Gson().toJson(ride);
        App.putSharedPref("lastRideOffer", lastRideOffer);

        App.getNetworkService().offerRide(ride, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                ride.save();
                Toast.makeText(App.inst(), "Carona salva", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(App.LOGTAG, error.getMessage());
                Toast.makeText(App.inst(), "Erro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
