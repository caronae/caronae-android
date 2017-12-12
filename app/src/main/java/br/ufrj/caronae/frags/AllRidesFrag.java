package br.ufrj.caronae.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.AllRidesFragmentPagerAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.models.modelsforjson.RideFiltersForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJsonDeserializer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllRidesFrag extends Fragment {

    private static boolean PAGE_WAS_GOING  = true;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;

    static FloatingActionMenu fab_menu;
    @BindView(R.id.fab_add_ride)
    com.github.clans.fab.FloatingActionButton fab_add_ride;
    @BindView(R.id.fab_active_rides)
    com.github.clans.fab.FloatingActionButton fab_active_rides;


    static CoordinatorLayout coordinatorLayout;

    Context context;

    ArrayList<RideForJson> goingRides = new ArrayList<>(), notGoingRides = new ArrayList<>();

    boolean isFabPrepared = false;

    public AllRidesFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides, container, false);
        ButterKnife.bind(this, view);

        context = getContext();

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.all_rides_coordinator);

        listAllRides(1);

        fab_menu = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        if(((MainAct)getActivity()).filterText.getText().equals(""))
        {
            ((MainAct)getActivity()).hideFilterCard(context);
        }
        return view;
    }

    private void listAllRides(final int pageNum) {

        final Snackbar snackbar = makeNoConexionSnack();

        progressBar2.setVisibility(View.VISIBLE);


        if (isAdded() && (getActivity() != null)) {

            String going = null;
            String neighborhoods = null;
            String zone = null;
            String hub = null;
            String filtersJsonString = SharedPref.getFiltersPref();
            if (!filtersJsonString.equals(SharedPref.MISSING_PREF)){
                RideFiltersForJson rideFilters = new Gson().fromJson(filtersJsonString, RideFiltersForJson.class);
                neighborhoods = rideFilters.getLocation();
                hub = rideFilters.getCenter();
                zone = rideFilters.getZone();
            }

            App.getNetworkService(getContext()).listAllRides(pageNum + "", going, neighborhoods, zone, hub)
                    .enqueue(new Callback<RideForJsonDeserializer>() {
                        @Override
                        public void onResponse(Call<RideForJsonDeserializer> call, Response<RideForJsonDeserializer> response) {
                            dismissSnack(snackbar);
                            if (response.isSuccessful()) {
                                progressBar2.setVisibility(View.GONE);

                                RideForJsonDeserializer data = response.body();
                                List<RideForJson> rideOffers = data.getData();

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
                                                if (!checkIfRideIsInList(goingRides, rideOffer))
                                                    goingRides.add(rideOffer);
                                            else
                                                if (!checkIfRideIsInList(notGoingRides, rideOffer))
                                                    notGoingRides.add(rideOffer);
                                        }
                                    }
                                }

                                if (isAdded()) {
                                    viewPager.setAdapter(new AllRidesFragmentPagerAdapter(getChildFragmentManager(), goingRides, notGoingRides, App.inst().getResources().getStringArray(R.array.tab_tags)));
                                    tabLayout.setupWithViewPager(viewPager);
                                    if (PAGE_WAS_GOING)
                                        viewPager.setCurrentItem(0);
                                    else
                                        viewPager.setCurrentItem(1);

                                    tabLayout.setBackground(ContextCompat.getDrawable(App.inst(), R.drawable.transparency_gradient_top_botton));

                                    configureTabIndicators();
                                }

                                showFAB();

                            } else {
                                Util.treatResponseFromServer(response);
                                progressBar2.setVisibility(View.GONE);
                                Log.e("listAllRides", response.message());
                            }
                            showFAB();
                        }

                        @Override
                        public void onFailure(Call<RideForJsonDeserializer> call, Throwable t) {
                            progressBar2.setVisibility(View.GONE);
                            Log.e("listAllRides", t.getMessage());
                            showFAB();
                            snackbar.setAction("CONECTAR", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listAllRides(pageNum);
                                }
                            });
                            showSnack(snackbar);
                        }
                    });
        } else {
            Util.toast("Activity not atached");
            return;
        }
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


    private void showFAB() {
        Animation anim = new AlphaAnimation(0, 1);
        anim.setDuration(600);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        fab_menu.startAnimation(anim);
    }

    private ArrayList<RideForJson> filterList(ArrayList<RideForJson> listToFilter, CharSequence searchText) {
        ArrayList<RideForJson> listFiltered = new ArrayList<>();
        for (int ride = 0; ride < listToFilter.size(); ride++) {
            if (listToFilter.get(ride).getNeighborhood().toLowerCase().contains(searchText.toString().toLowerCase()))
                listFiltered.add(listToFilter.get(ride));
        }
        return listFiltered;
    }

    public static Snackbar makeLoadingRidesSnack() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, coordinatorLayout.getResources().getString(R.string.load_more_rides), Snackbar.LENGTH_INDEFINITE);
        return snackbar;
    }

    public static void showSnack(Snackbar snackbar) {
        fab_menu.animate().translationY(-Util.convertDpToPixel(32));
        snackbar.show();
    }

    public static void dismissSnack(Snackbar snackbar) {
        fab_menu.animate().translationY(Util.convertDpToPixel(32));
        snackbar.dismiss();
    }

    public static Snackbar makeNoConexionSnack() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, coordinatorLayout.getResources().getString(R.string.no_conexion), Snackbar.LENGTH_INDEFINITE);
        return snackbar;
    }

    private boolean checkIfRideIsInList(ArrayList<RideForJson> list, RideForJson ride) {
        boolean contains = false;
        for (int counter = 0; counter < list.size(); counter++) {
            if (list.get(counter).getDbId() == ride.getDbId()) {
                contains = true;
            }
            if (!contains
                    && (list.get(counter).getDriver().getDbId() == (ride.getDriver().getDbId()))
                    && (list.get(counter).getDate().equals(ride.getDate()))
                    && (list.get(counter).getTime().equals(ride.getTime()))){
                contains = true;
            }
        }
        return contains;
    }

    public static void setPageThatWas(boolean page){
        PAGE_WAS_GOING = page;
    }
}
