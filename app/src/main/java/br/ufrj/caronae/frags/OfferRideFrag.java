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

public class OfferRideFrag extends Fragment {
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

    private Ride lastRide;

    public OfferRideFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        ButterKnife.bind(this, view);

        lastRide = Select.from(Ride.class).first();

        if (lastRide != null) {
            neighborhood_et.setText(lastRide.getNeighborhood());
            place_et.setText(lastRide.getPlace());
            way_et.setText(lastRide.getWay());
            date_et.setText(lastRide.getDate());
            time_et.setText(lastRide.getTime());
            slots_et.setText(lastRide.getSlots());
            hub_et.setText(lastRide.getHub());
            description_et.setText(lastRide.getDescription());
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

        App.getNetworkService().offerRide(ride, new Callback<Response>() {
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
