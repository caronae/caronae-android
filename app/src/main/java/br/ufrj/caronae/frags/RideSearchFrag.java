package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByTime;
import br.ufrj.caronae.models.RideOffer;
import br.ufrj.caronae.models.RideSearchFilters;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RideSearchFrag extends Fragment {
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.zone_et)
    EditText zone_et;
    @Bind(R.id.neighborhood_et)
    EditText neighborhood_et;
    @Bind(R.id.date_et)
    EditText date_et;

    @Bind(R.id.rvRides)
    RecyclerView rvRides;

    private RideOfferAdapter adapter;

    public RideSearchFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_ride, container, false);
        ButterKnife.bind(this, view);

        adapter = new RideOfferAdapter(new ArrayList<RideOffer>());
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        String lastRideSearchFilters = App.getSharedPref("lastRideSearchFilters");
        if (!lastRideSearchFilters.equals("missing")) {
            RideSearchFilters rideSearchFilters = new Gson().fromJson(lastRideSearchFilters, RideSearchFilters.class);

            zone_et.setText(rideSearchFilters.getZone());
            neighborhood_et.setText(rideSearchFilters.getNeighborhood());
            date_et.setText(rideSearchFilters.getDate());
            boolean go = rideSearchFilters.isGo();
            radioGroup.check(go ? R.id.go_rb : R.id.back_rb);
        }

        return view;
    }

    @OnClick(R.id.search_bt)
    public void searchBt() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Aguarde", true, true);

        String zone = zone_et.getText().toString();
        String neighborhood = neighborhood_et.getText().toString();
        String date = date_et.getText().toString();
        boolean go = radioGroup.getCheckedRadioButtonId() == R.id.go_rb;
        RideSearchFilters rideSearchFilters = new RideSearchFilters(zone, neighborhood, date, go);

        String lastRideSearchFilters = new Gson().toJson(rideSearchFilters);
        App.putSharedPref("lastRideSearchFilters", lastRideSearchFilters);

        App.getNetworkService().getRideOffers(rideSearchFilters, new Callback<List<RideOffer>>() {
            @Override
            public void success(List<RideOffer> rideOffer, Response response) {
                Collections.sort(rideOffer, new RideOfferComparatorByTime());
                adapter.makeList(rideOffer);
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(App.LOGTAG, error.getMessage());
                pd.dismiss();
            }
        });
    }
}
