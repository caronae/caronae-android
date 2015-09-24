package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.RidesAdapter;
import br.ufrj.caronae.models.Ride;

public class SearchRide extends Fragment {
    public SearchRide() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_ride, container, false);

        RecyclerView rvRides = (RecyclerView) view.findViewById(R.id.rvRides);

        Ride lastRide = Select.from(Ride.class).first();
        List<Ride> l = new ArrayList<Ride>();
        l.add(lastRide);
        l.add(lastRide);
        l.add(lastRide);
        l.add(lastRide);
        l.add(lastRide);

        RidesAdapter adapter = new RidesAdapter(l);
        rvRides.setAdapter(adapter);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}
