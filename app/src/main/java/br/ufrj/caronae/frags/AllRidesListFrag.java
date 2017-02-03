package br.ufrj.caronae.frags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.security.auth.callback.Callback;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.AllRidesFragmentPagerAdapter;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class AllRidesListFrag extends Fragment implements Callback {
    @Bind(R.id.rvRides)
    RecyclerView rvRides;
    @Bind(R.id.norides_tv)
    TextView norides_tv;
    @Bind(R.id.helpText_tv)
    TextView helpText_tv;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    RideOfferAdapter adapter;

    int pageIdentifier;

    public AllRidesListFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides_list, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        ArrayList<RideForJson> rideOffers = bundle.getParcelableArrayList("rides");
        pageIdentifier = bundle.getInt("ID");

        //No Refresh incializa a activity principal novamente para atualizar as caronas
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRideList();
            }
        });

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

    void refreshRideList() {

        App.getNetworkService(getContext()).listAllRides()
                .enqueue(new retrofit2.Callback<List<RideForJson>>() {
                    @Override
                    public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {
                        if (response.isSuccessful()) {

                            ArrayList<RideForJson> goingRides = new ArrayList<RideForJson>();
                            ArrayList<RideForJson> notGoingRides = new ArrayList<RideForJson>();

                            List<RideForJson> rideOffers = response.body();

                            if (rideOffers != null && !rideOffers.isEmpty()) {
                                Collections.sort(rideOffers, new RideOfferComparatorByDateAndTime());

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                                Date todayDate = new Date();
                                String todayString = simpleDateFormat.format(todayDate);
                                simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                                String time = simpleDateFormat.format(todayDate);

                                Iterator<RideForJson> it = rideOffers.iterator();
                                while (it.hasNext()) {
                                    RideForJson rideOffer = it.next();
                                    if (Util.formatBadDateWithYear(rideOffer.getDate()).equals(todayString) && Util.formatTime(rideOffer.getTime()).compareTo(time) < 0)
                                        it.remove();
                                    else {
                                        rideOffer.setDbId(rideOffer.getId().intValue());
                                        if (rideOffer.isGoing())
                                            goingRides.add(rideOffer);
                                        else
                                            notGoingRides.add(rideOffer);
                                    }
                                }
                            }

                            adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getContext());
                            rvRides.setAdapter(adapter);
                            rvRides.setHasFixedSize(true);
                            rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

                            if (pageIdentifier == 0) {
                                if (goingRides == null || goingRides.isEmpty()) {
                                    norides_tv.setVisibility(View.VISIBLE);
                                    helpText_tv.setVisibility(View.INVISIBLE);
                                } else {
                                    adapter.makeList(goingRides);
                                }
                            } else {
                                if (notGoingRides == null || notGoingRides.isEmpty()) {
                                    norides_tv.setVisibility(View.VISIBLE);
                                    helpText_tv.setVisibility(View.INVISIBLE);
                                } else {
                                    adapter.makeList(notGoingRides);
                                }
                            }
                            refreshLayout.setRefreshing(false);
                        } else {
                            Util.toast(R.string.frag_allrides_errorGetRides);
                            refreshLayout.setRefreshing(false);
                            Log.e("listAllRides", response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                        Util.toast(R.string.frag_allrides_errorGetRides);
                        refreshLayout.setRefreshing(false);
                        Log.e("listAllRides", t.getMessage());
                    }
                });


    }
}
