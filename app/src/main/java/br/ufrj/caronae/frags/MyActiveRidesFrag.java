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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.MyActiveRidesAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyActiveRidesFrag extends Fragment {

    @Bind(R.id.myRidesList)
    RecyclerView myRidesList;
    @Bind(R.id.norides_tv)
    TextView norides_tv;
    @Bind(R.id.helpText2_tv)
    TextView helpText2_tv;

    private MyActiveRidesAdapter adapter;

    public MyActiveRidesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_active_rides, container, false);
        ButterKnife.bind(this, view);

        App.getBus().register(this);

        final ProgressDialog pd = ProgressDialog.show(getContext(), "", getActivity().getString(R.string.wait), true, true);
        App.getNetworkService(getContext()).getMyActiveRides()
                .enqueue(new Callback<List<RideForJson>>() {
                    @Override
                    public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {

                        if (response.isSuccessful()){

                            List<RideForJson> rideWithUsersList = response.body();
                            if (rideWithUsersList == null || rideWithUsersList.isEmpty()) {
                                pd.dismiss();

                                myRidesList.setAdapter(new MyActiveRidesAdapter(new ArrayList<RideForJson>(), (MainAct) getActivity()));
                                myRidesList.setHasFixedSize(true);
                                myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));

                                norides_tv.setVisibility(View.VISIBLE);
                                return;
                            }

                            ActiveRide.deleteAll(ActiveRide.class);
                            //subscribe to ride id topic
                            for (RideForJson rideWithUsers : rideWithUsersList) {
                                int rideId = rideWithUsers.getId().intValue();
                                rideWithUsers.setDbId(rideId);

                                FirebaseTopicsHandler.subscribeFirebaseTopic(rideId + "");

                                new ActiveRide(rideWithUsers.getDbId(),rideWithUsers.isGoing(), rideWithUsers.getDate()).save();
                            }

                            Collections.sort(rideWithUsersList, new RideOfferComparatorByDateAndTime());
                            adapter = new MyActiveRidesAdapter(rideWithUsersList, (MainAct) getActivity());
                            myRidesList.setAdapter(adapter);
                            myRidesList.setHasFixedSize(true);
                            myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));

                            helpText2_tv.setVisibility(View.VISIBLE);
                            pd.dismiss();
                        } else {
                            pd.dismiss();

                            norides_tv.setVisibility(View.VISIBLE);
                            Util.toast(R.string.frag_myactiverides_errorGetActiveRides);

                            Log.e("getMyActiveRides", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                        pd.dismiss();

                        norides_tv.setVisibility(View.VISIBLE);
                        Util.toast(R.string.frag_myactiverides_errorGetActiveRides);

                        Log.e("getMyActiveRides", t.getMessage());
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String rideId = SharedPref.getRemoveRideFromList();
        if (!rideId.equals(SharedPref.MISSING_PREF)) {
            removeRideFromList(rideId);
        }
    }

    public void removeRideFromList(String rideId) {
        adapter.remove(Integer.valueOf(rideId));
        SharedPref.removeRemoveRideFromList();
        Log.i("removeRideFromList,actv", "remove called");
    }
}
