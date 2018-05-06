package br.ufrj.caronae.frags;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.gson.Gson;
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
import br.ufrj.caronae.EndlessRecyclerViewScrollListener;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.AllRidesFragmentPagerAdapter;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideFiltersForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJsonDeserializer;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class AllRidesListFrag extends Fragment implements Callback {

    @BindView(R.id.rvRides)
    RecyclerView rvRides;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.all_rides_list_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.norides_tv)
    TextView noRides;

    private final int FIRST_PAGE_TO_LOAD = 0;

    RideOfferAdapter adapter;

    int pageCounter = FIRST_PAGE_TO_LOAD;

    private EndlessRecyclerViewScrollListener scrollListener;

    LinearLayoutManager mLayoutManager;

    int pageIdentifier;

    CharSequence filter = null;

    ArrayList<RideForJson> goingRides = new ArrayList<>();
    ArrayList<RideForJson> notGoingRides = new ArrayList<>();

    ArrayList<RideForJson> filteredGoingList = new ArrayList<>();
    ArrayList<RideForJson> filteredNotGoingList = new ArrayList<>();

    public AllRidesListFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides_list, container, false);
        ButterKnife.bind(this, view);

        Context ctx;
        ctx = getContext();

        Bundle bundle = getArguments();
        ArrayList<RideForJson> rideOffers = bundle.getParcelableArrayList("rides");
        pageIdentifier = bundle.getInt("ID");

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCounter = FIRST_PAGE_TO_LOAD;
                for (int counter = FIRST_PAGE_TO_LOAD; counter <= pageCounter; counter++) {
                    refreshRideList(counter);
                }
            }
        });

        adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getContext(), getActivity().getFragmentManager());
        mLayoutManager = new LinearLayoutManager(getContext());
        rvRides.setLayoutManager(mLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadOneMorePage();
            }
        };
        rvRides.addOnScrollListener(scrollListener);

        rvRides.setAdapter(adapter);

        if(SharedPref.OPEN_ALL_RIDES)
        {
            noRides.setVisibility(View.GONE);
            if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING) {
                if (SharedPref.ALL_RIDES_GOING != null && !SharedPref.ALL_RIDES_GOING.isEmpty()) {
                    adapter.makeList(SharedPref.ALL_RIDES_GOING);
                    scrollListener.resetState();
                }
            } else {
                if (SharedPref.ALL_RIDES_LEAVING != null && !SharedPref.ALL_RIDES_LEAVING.isEmpty()) {
                    adapter.makeList(SharedPref.ALL_RIDES_LEAVING);
                    scrollListener.resetState();
                }
            }
        }
        else if(!Util.isNetworkAvailable(ctx))
        {
            noRides.setText(R.string.allrides_norides);
            noRides.setVisibility(View.VISIBLE);
        }
        else
        {
            noRides.setText(R.string.charging);
            noRides.setVisibility(View.VISIBLE);
        }

        if (!(rideOffers == null || rideOffers.isEmpty())) {
            adapter.makeList(rideOffers);
        }

        App.getBus().register(this);

        // After setting layout manager, adapter, etc...
        float offsetBottonPx = getResources().getDimension(R.dimen.recycler_view_botton_offset);
        float offsetTopPx = getResources().getDimension(R.dimen.recycler_view_top_offset);
        Util.OffsetDecoration OffsetDecoration = new Util.OffsetDecoration((int) offsetBottonPx, (int) offsetTopPx);
        rvRides.addItemDecoration(OffsetDecoration);

        animateListFadeIn();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getInst());

        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(SharedPref.RIDE_FILTER_PREF_KEY)){
                    pageCounter = FIRST_PAGE_TO_LOAD;
                    refreshRideList(pageCounter);
                }
            }
        });

        return view;
    }

    @Subscribe
    public void updateAfterResquest(RideRequestSent ride) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.getBus().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void refreshRideList(final int pageNumber) {
        String going = null;
        if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING)
            going = "1";
        else
            going = "0";
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
        CaronaeAPI.service(getContext()).listAllRides(pageNumber + "", going, neighborhoods, zone, hub,  "", campus)
                .enqueue(new retrofit2.Callback<RideForJsonDeserializer>() {
                    @Override
                    public void onResponse(Call<RideForJsonDeserializer> call, Response<RideForJsonDeserializer> response) {
                        if (response.isSuccessful()) {

                            if (pageCounter == FIRST_PAGE_TO_LOAD) {
                                goingRides = new ArrayList<>();
                                notGoingRides = new ArrayList<>();
                            }

                            RideForJsonDeserializer data = response.body();
                            List<RideForJson> rideOffers = data.getData();
                            if(rideOffers.size() != 0) {
                                if (!filtersJsonString.equals(SharedPref.MISSING_PREF)){
                                    setRides(rideOffers, false);
                                }
                                else
                                {
                                    SharedPref.OPEN_ALL_RIDES = true;
                                    setRides(rideOffers, true);
                                }
                                noRides.setVisibility(View.GONE);
                            }
                            else if(!SharedPref.OPEN_ALL_RIDES)
                            {
                                noRides.setText(R.string.frag_rideSearch_noRideFound);
                            }
                        } else {
                            Util.treatResponseFromServer(response);
                            noRides.setText(R.string.allrides_norides);
                            refreshLayout.setRefreshing(false);
                            Log.e("listAllRides", response.message());
                        }
                        scrollListener.resetState();
                    }

                    @Override
                    public void onFailure(Call<RideForJsonDeserializer> call, Throwable t) {
                        refreshLayout.setRefreshing(false);
                        Log.e("listAllRides", t.getMessage());
                        if(!SharedPref.OPEN_ALL_RIDES) {
                            noRides.setText(R.string.allrides_norides);
                        }
                        scrollListener.resetState();
                    }
                });

        if (filter != null) {
            switch (pageNumber) {
                case 0:
                    if (filteredGoingList.size() <= 8) {
                        loadOneMorePage();
                    }
                    if (filteredNotGoingList.size() <= 8) {
                        loadOneMorePage();
                    }
                    break;
                case 1:
                    if (filteredGoingList.size() <= 8) {
                        loadOneMorePage();
                    }
                    if (filteredNotGoingList.size() <= 8) {
                        loadOneMorePage();
                    }
                    break;
            }
        }

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

    private void animateListFadeIn() {
        Animation anim = new AlphaAnimation(0, 1);
        anim.setDuration(300);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        rvRides.startAnimation(anim);
    }

    private void setRides(List<RideForJson> rideOffers, boolean saveList)
    {
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
                    if (rideOffer.isGoing()) {
                        if (!checkIfRideIsInList(goingRides, rideOffer)){
                            goingRides.add(rideOffer);
                        }
                    } else {
                        if (!checkIfRideIsInList(notGoingRides, rideOffer)) {
                            notGoingRides.add(rideOffer);
                        }
                    }
                }
            }
            Collections.sort(goingRides, new RideOfferComparatorByDateAndTime());
            Collections.sort(notGoingRides, new RideOfferComparatorByDateAndTime());
        }

        if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING) {
            if (goingRides == null || goingRides.isEmpty()) {
            } else {
                SharedPref.ALL_RIDES_GOING = goingRides;
                adapter.makeList(goingRides);
                scrollListener.resetState();
            }
        } else {
            if (notGoingRides == null || notGoingRides.isEmpty()) {
            } else {
                SharedPref.ALL_RIDES_LEAVING = notGoingRides;
                adapter.makeList(notGoingRides);
                scrollListener.resetState();
            }
        }
        refreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void updateAdapter(ArrayList<Object> listFiltered) {
        filteredGoingList.clear();
        filteredNotGoingList.clear();

        for (int rides = 1; rides < listFiltered.size(); rides++) {
            RideForJson ride = (RideForJson) listFiltered.get(rides);
            if (ride.isGoing()) {
                filteredGoingList.add((RideForJson) listFiltered.get(rides));
            } else {
                filteredNotGoingList.add((RideForJson) listFiltered.get(rides));
            }
        }

        if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING) {
            if (filteredGoingList.size() == 0) {
                filter = null;
                adapter.makeList(goingRides);
                Util.toast("Nenhuma carona encontrada para esta busca");
            } else {
                filter = (CharSequence) listFiltered.get(0);
                adapter.makeList(filteredGoingList);
            }
        } else {
            if (filteredNotGoingList.size() == 0) {
                filter = null;
                adapter.makeList(notGoingRides);
                Util.toast("Nenhuma carona encontrada para esta busca");
            } else {
                filter = (CharSequence) listFiltered.get(0);
                adapter.makeList(filteredNotGoingList);
            }
        }

    }

    private void loadOneMorePage() {
        pageCounter++;
        refreshRideList(pageCounter);
    }
}