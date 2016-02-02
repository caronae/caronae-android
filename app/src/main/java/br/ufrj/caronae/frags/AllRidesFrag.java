package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.leakcanary.RefWatcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.AllRidesFragmentPagerAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AllRidesFrag extends Fragment {

    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.progressBar2)
    ProgressBar progressBar2;

    public AllRidesFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides, container, false);
        ButterKnife.bind(this, view);

        final ArrayList<RideForJson> goingRides = new ArrayList<>(), notGoingRides = new ArrayList<>();

        App.getNetworkService().listAllRides(new Callback<List<RideForJson>>() {
            @Override
            public void success(List<RideForJson> rideOffers, Response response) {
                progressBar2.setVisibility(View.GONE);

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

                viewPager.setAdapter(new AllRidesFragmentPagerAdapter(getChildFragmentManager(), goingRides, notGoingRides));
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void failure(RetrofitError error) {
                progressBar2.setVisibility(View.GONE);
                Util.toast(R.string.frag_allrides_errorGetRides);
                Log.e("listAllRides", error.getMessage());
            }
        });

        return view;
    }

    @OnClick(R.id.fab)
    public void fab() {
        ((MainAct) getActivity()).showRideOfferFrag();
    }

    /*@Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }*/
}
