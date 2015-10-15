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

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.MyActiveRidesAdapter;
import br.ufrj.caronae.models.RideWithUsers;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyActiveRidesFrag extends Fragment {

    @Bind(R.id.myRidesList)
    RecyclerView myRidesList;
    @Bind(R.id.norides_tv)
    TextView norides_tv;

    public MyActiveRidesFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_active_rides, container, false);
        ButterKnife.bind(this, view);

        App.getNetworkService().getMyActiveRides(App.getUser(), new Callback<List<RideWithUsers>>() {
            @Override
            public void success(List<RideWithUsers> response, Response response2) {
                if (response == null || response.isEmpty()) {
                    norides_tv.setVisibility(View.VISIBLE);
                    return;
                }

                myRidesList.setAdapter(new MyActiveRidesAdapter(response, (MainAct) getActivity()));
                myRidesList.setHasFixedSize(true);
                myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void failure(RetrofitError error) {
                norides_tv.setVisibility(View.VISIBLE);
                Log.e("getMyActiveRides", error.getMessage());
            }
        });

        return view;
    }


}
