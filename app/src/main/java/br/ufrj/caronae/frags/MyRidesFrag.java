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
import br.ufrj.caronae.adapters.MyRidesAdapter;
import br.ufrj.caronae.models.Ride;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MyRidesFrag extends Fragment {

    @Bind(R.id.myRidesList)
    RecyclerView myRidesList;
    @Bind(R.id.norides_tv)
    TextView norides_tv;

    private MyRidesAdapter adapter;

    public MyRidesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);
        ButterKnife.bind(this, view);

        ArrayList<Ride> rides = (ArrayList<Ride>) Ride.listAll(Ride.class);

        if (!rides.isEmpty()) {
            adapter = new MyRidesAdapter(rides);
            myRidesList.setAdapter(adapter);
            myRidesList.setHasFixedSize(true);
            myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            norides_tv.setVisibility(View.VISIBLE);
        }

        return view;
    }
}