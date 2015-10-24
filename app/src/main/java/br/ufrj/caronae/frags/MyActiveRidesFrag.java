package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.MyActiveRidesAdapter;
import br.ufrj.caronae.models.RideWithUsersForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyActiveRidesFrag extends Fragment {

    @Bind(R.id.myRidesList)
    RecyclerView myRidesList;
    @Bind(R.id.norides_tv)
    TextView norides_tv;
    @Bind(R.id.fab)
    ImageView fab;

    public MyActiveRidesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_active_rides, container, false);
        ButterKnife.bind(this, view);

        App.getNetworkService().getMyActiveRides(App.getUser(), new Callback<List<RideWithUsersForJson>>() {
            @Override
            public void success(List<RideWithUsersForJson> response, Response response2) {
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
                App.toast("Erro ao obter caronas ativas");
                Log.e("getMyActiveRides", error.getMessage());
            }
        });

        return view;
    }

    @OnClick(R.id.fab)
    public void fab() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new RideOfferFrag()).commit();
    }
}
