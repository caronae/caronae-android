package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AllRidesListFrag extends Fragment {
    @Bind(R.id.rvRides)
    RecyclerView rvRides;
    @Bind(R.id.norides_tv)
    TextView norides_tv;
    @Bind(R.id.helpText_tv)
    TextView helpText_tv;

    RideOfferAdapter adapter;

    public AllRidesListFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides_list, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        ArrayList<RideForJson> rideOffers = bundle.getParcelableArrayList("rides");

        adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getContext());
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (rideOffers == null || rideOffers.isEmpty()) {
            norides_tv.setVisibility(View.VISIBLE);
            helpText_tv.setVisibility(View.INVISIBLE);
        } else {
            adapter.makeList(rideOffers);
        }

        App.getBus().register(this);

        return view;
    }

    @Subscribe
    public void removeRideFromList(RideRequestSent ride) {
        adapter.remove(ride.getDbId());
        Log.i("removeRideFromList,all", "remove called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.getBus().unregister(this);
    }
}
