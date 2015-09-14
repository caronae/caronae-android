package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
    @Bind(R.id.from_et)
    EditText from_et;
    @Bind(R.id.to_et)
    EditText to_et;
    @Bind(R.id.date_et)
    EditText date_et;
    @Bind(R.id.time_et)
    EditText time_et;
    @Bind(R.id.slots_et)
    EditText slots_et;
    @Bind(R.id.hub_et)
    EditText hub_et;

    public static OfferRideFrag newInstance() {
        OfferRideFrag fragment = new OfferRideFrag();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    public OfferRideFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.save_bt)
    public void sendBt() {
        String from = from_et.getText().toString();
        String to = to_et.getText().toString();
        String date = date_et.getText().toString();
        String time = time_et.getText().toString();
        String slots = slots_et.getText().toString();
        String hub = hub_et.getText().toString();

        Ride ride = new Ride(from, to, date, time, slots, hub);
        App.getApiaryService().offerRide(ride, new Callback<Ride>() {
            @Override
            public void success(Ride user, Response response) {
                //ride.save();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(App.LOGTAG, error.getMessage());
            }
        });
    }
}
