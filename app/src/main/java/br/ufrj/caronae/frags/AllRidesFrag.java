package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllRidesFrag extends Fragment {

    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.progressBar2)
    ProgressBar progressBar2;
    @Bind(R.id.fab_menu)
    FloatingActionMenu fab_menu;
    @Bind(R.id.fab_add_ride)
    com.github.clans.fab.FloatingActionButton fab_add_ride;
    @Bind(R.id.fab_active_rides)
    com.github.clans.fab.FloatingActionButton fab_active_rides;

    ArrayList<RideForJson> goingRides = new ArrayList<>(), notGoingRides = new ArrayList<>();

    boolean isFabPrepared = false;

    public AllRidesFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides, container, false);
        ButterKnife.bind(this, view);
        listAllRides(1);

        return view;
    }

    private void listAllRides(int pageNum) {

        App.getNetworkService(getContext()).listAllRides(pageNum + "")
                .enqueue(new Callback<List<RideForJson>>() {
                    @Override
                    public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {
                        if (response.isSuccessful()) {
                            progressBar2.setVisibility(View.GONE);

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

                            viewPager.setAdapter(new AllRidesFragmentPagerAdapter(getChildFragmentManager(), goingRides, notGoingRides,  getResources().getStringArray(R.array.tab_tags)));
                            tabLayout.setupWithViewPager(viewPager);

                            tabLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.transparency_gradient));

                            configureTabIndicators();

                            showFAB();

                        } else {
                            progressBar2.setVisibility(View.GONE);
                            Log.e("listAllRides", response.message());
                        }
                        showFAB();
                    }

                    @Override
                    public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                        progressBar2.setVisibility(View.GONE);
                        Log.e("listAllRides", t.getMessage());
                        showFAB();
                    }
                });
    }

    private void configureTabIndicators() {

        View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
        p.setMargins(25, 0, 25, 0);
        tab.requestLayout();

        tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
        p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
        p.setMargins(25, 0, 25, 0);
        tab.requestLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFabPrepared)
            prepareFloatingActionMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        isFabPrepared = false;
    }

    @OnClick(R.id.fab_add_ride)
    public void fab_add_ride() {
        ((MainAct) getActivity()).showRideOfferFrag();
    }

    @OnClick(R.id.fab_active_rides)
    public void fab_active_rides() {
        ((MainAct) getActivity()).showActiveRidesFrag();
    }

    private void prepareFloatingActionMenu() {
        final ArrayList<Integer> colorOptions = new ArrayList<>();
        colorOptions.add(R.color.zone_baixada);
        colorOptions.add(R.color.zone_niteroi);
        colorOptions.add(R.color.zone_sul);
        colorOptions.add(R.color.zone_centro);
        colorOptions.add(R.color.light_zone_baixada_transparency);
        colorOptions.add(R.color.light_zone_niteroi_transparency);
        colorOptions.add(R.color.light_zone_sul_transparency);
        colorOptions.add(R.color.light_zone_centro_transparency);
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(4);

        fab_menu.setMenuButtonColorNormal(ContextCompat.getColor(getContext(), colorOptions.get(randomInt)));
        fab_menu.setMenuButtonColorPressed(ContextCompat.getColor(getContext(), colorOptions.get(randomInt + 4)));


        randomInt++;

        if (randomInt >= 4) {
            randomInt = 0;
        }

        fab_add_ride.setColorNormal(ContextCompat.getColor(getContext(), colorOptions.get(randomInt)));
        fab_add_ride.setColorPressed(ContextCompat.getColor(getContext(), colorOptions.get(randomInt + 4)));

        randomInt++;

        if (randomInt >= 4) {
            randomInt = 0;
        }

        fab_active_rides.setColorNormal(ContextCompat.getColor(getContext(), colorOptions.get(randomInt)));
        fab_active_rides.setColorPressed(ContextCompat.getColor(getContext(), colorOptions.get(randomInt + 4)));

        isFabPrepared = true;
    }

    private void showFAB() {
        Animation anim = new AlphaAnimation(0, 1);
        anim.setDuration(600);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        fab_menu.startAnimation(anim);
    }
}
