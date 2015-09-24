package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RideOffersAdapter;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideOffer;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchRide extends Fragment {
    @Bind(R.id.search_bt)
    Button searchBt;
    private RecyclerView rvRides;
    private RideOffersAdapter adapter;

    public SearchRide() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_ride, container, false);
        ButterKnife.bind(this, view);

        rvRides = (RecyclerView) view.findViewById(R.id.rvRides);

        List<RideOffer> l = new ArrayList<>();
        adapter = new RideOffersAdapter(l);
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
    
    @OnClick(R.id.search_bt)
    public void searchBt() {
        Ride lastRide = Select.from(Ride.class).first();
        RideOffer rideOffer = new RideOffer(lastRide, App.getUser());
        List<RideOffer> l = new ArrayList<>();
        l.add(rideOffer);
        adapter.makeList(l);
    }
}
