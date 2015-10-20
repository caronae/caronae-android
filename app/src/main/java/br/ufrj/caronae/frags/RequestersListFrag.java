package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.RequestersAdapter;
import br.ufrj.caronae.models.User;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RequestersListFrag extends Fragment {

    @Bind(R.id.requestersList)
    RecyclerView requestersList;

    public RequestersListFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requesters_list, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        ArrayList<User> users = bundle.getParcelableArrayList("users");
        int rideId = bundle.getInt("rideId");

        requestersList.setAdapter(new RequestersAdapter(users, rideId, getActivity()));
        requestersList.setHasFixedSize(true);
        requestersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

}
