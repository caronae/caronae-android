package br.ufrj.caronae.frags;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.httpapis.CaronaeAPI;
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
    @BindView(R.id.norides_tv)
    TextView noRides;
    @BindView(R.id.tab1)
    RelativeLayout isGoing_bt;
    @BindView(R.id.tab2)
    RelativeLayout isLeaving_bt;
    @BindView(R.id.tab1_tv)
    TextView isGoing_tv;
    @BindView(R.id.tab2_tv)
    TextView isLeaving_tv;

    boolean isGoing;

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

        isGoing = true;

        context = getContext();

        setHasOptionsMenu(true);

        MainAct.showMainItems();

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.all_rides_coordinator);

        listAllRides(1);

        if(((MainAct)getActivity()).filterText.getText().equals(""))
        {
            ((MainAct)getActivity()).hideFilterCard(context);
        }
        return view;
    }

    private void listAllRides(final int pageNum) {

        final Snackbar snackbar = makeNoConexionSnack();
        if(!SharedPref.OPEN_ALL_RIDES)
        {
            SharedPref.OPEN_ALL_RIDES = true;
            noRides.setText(R.string.charging);
            noRides.setVisibility(View.VISIBLE);
        }

        if (isAdded() && (getActivity() != null)) {

            String going = null;
            String neighborhoods = null;
            String zone = null;
            String hub = null;
            String campus = null;
            String filtersJsonString = SharedPref.getFiltersPref();
            if (!filtersJsonString.equals(SharedPref.MISSING_PREF)){
                RideFiltersForJson rideFilters = new Gson().fromJson(filtersJsonString, RideFiltersForJson.class);
                neighborhoods = rideFilters.getLocation();
                if(!rideFilters.getCampus().equals("Todos os Campi"))
                {
                    hub = rideFilters.getCenter();
                    campus = rideFilters.getCampus();
                }
                zone = rideFilters.getZone();
            }

            CaronaeAPI.service(getContext()).listAllRides(pageNum + "", going, neighborhoods, zone, hub,  "", campus)
                    .enqueue(new Callback<RideForJsonDeserializer>() {
                        @Override
                        public void onResponse(Call<RideForJsonDeserializer> call, Response<RideForJsonDeserializer> response) {
                            if (response.isSuccessful()) {
                                noRides.setVisibility(View.INVISIBLE);

                                RideForJsonDeserializer data = response.body();
                                List<RideForJson> rideOffers = data.getData();

                                if (rideOffers != null && !rideOffers.isEmpty()) {
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
                                    Collections.sort(goingRides, new RideOfferComparatorByDateAndTime());
                                    Collections.sort(notGoingRides, new RideOfferComparatorByDateAndTime());
                                }

                                if (isAdded()) {
                                    viewPager.setAdapter(new AllRidesFragmentPagerAdapter(getChildFragmentManager(), goingRides, notGoingRides, App.getInst().getResources().getStringArray(R.array.tab_tags)));
                                    tabLayout.setupWithViewPager(viewPager);
                                    if (PAGE_WAS_GOING)
                                        viewPager.setCurrentItem(0);
                                    else
                                        viewPager.setCurrentItem(1);

                                    if(Build.VERSION.SDK_INT >= 16) {
                                        tabLayout.setBackground(ContextCompat.getDrawable(App.getInst(), R.drawable.transparency_gradient_top_botton));
                                    }
                                    configureTabIndicators();
                                }
                            } else {
                                Util.treatResponseFromServer(response);
                                noRides.setText(R.string.allrides_norides);
                                Log.e("listAllRides", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<RideForJsonDeserializer> call, Throwable t) {
                            noRides.setText(R.string.frag_allrides_nointernettocharge);
                            noRides.setVisibility(View.VISIBLE);
                            Log.e("listAllRides", t.getMessage());
                            snackbar.setAction("CONECTAR", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listAllRides(pageNum);
                                }
                            });
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

    private ArrayList<RideForJson> filterList(ArrayList<RideForJson> listToFilter, CharSequence searchText) {
        ArrayList<RideForJson> listFiltered = new ArrayList<>();
        for (int ride = 0; ride < listToFilter.size(); ride++) {
            if (listToFilter.get(ride).getNeighborhood().toLowerCase().contains(searchText.toString().toLowerCase()))
                listFiltered.add(listToFilter.get(ride));
        }
        return listFiltered;
    }

    public static Snackbar makeLoadingRidesSnack() {
        return Snackbar.make(coordinatorLayout, coordinatorLayout.getResources().getString(R.string.load_more_rides), Snackbar.LENGTH_INDEFINITE);
    }

    public static Snackbar makeNoConexionSnack() {
        return Snackbar.make(coordinatorLayout, coordinatorLayout.getResources().getString(R.string.no_conexion), Snackbar.LENGTH_INDEFINITE);
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

    //Creates the toolbar menu with options to access the filter/search fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public static void setPageThatWas(boolean page){
        PAGE_WAS_GOING = page;
    }

    @OnClick(R.id.tab1)
    public void goingTabSelected()
    {
        if(!isGoing)
        {
            isGoing = true;
            isGoing_bt.setFocusable(false);
            isGoing_bt.setClickable(false);
            isLeaving_bt.setFocusable(true);
            isLeaving_bt.setClickable(true);
            GradientDrawable isGoingShape = (GradientDrawable)isGoing_bt.getBackground();
            GradientDrawable isLeavingShape = (GradientDrawable)isLeaving_bt.getBackground();
            isGoingShape.setColor(getResources().getColor(R.color.dark_gray));
            isLeavingShape.setColor(getResources().getColor(R.color.white));
            isLeaving_tv.setTextColor(getResources().getColor(R.color.dark_gray));
            isGoing_tv.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @OnClick(R.id.tab2)
    public void leavingTabSelected()
    {
        if(isGoing)
        {
            isGoing = false;
            isGoing_bt.setFocusable(true);
            isGoing_bt.setClickable(true);
            isLeaving_bt.setFocusable(false);
            isLeaving_bt.setClickable(false);
            GradientDrawable isGoingShape = (GradientDrawable)isGoing_bt.getBackground();
            GradientDrawable isLeavingShape = (GradientDrawable)isLeaving_bt.getBackground();
            isGoingShape.setColor(getResources().getColor(R.color.white));
            isLeavingShape.setColor(getResources().getColor(R.color.dark_gray));
            isLeaving_tv.setTextColor(getResources().getColor(R.color.white));
            isGoing_tv.setTextColor(getResources().getColor(R.color.dark_gray));
        }
    }
}