package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AllRidesListFrag extends Fragment {
    @Bind(R.id.rvRides)
    RecyclerView rvRides;
    @Bind(R.id.norides_tv)
    TextView norides_tv;

    public AllRidesListFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides_list, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        ArrayList<RideForJson> rideOffers = bundle.getParcelableArrayList("rides");

        RideOfferAdapter adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getActivity());
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (rideOffers == null || rideOffers.isEmpty()) {
            norides_tv.setVisibility(View.VISIBLE);
        } else {
            adapter.makeList(rideOffers);
        }

        return view;
    }

}
