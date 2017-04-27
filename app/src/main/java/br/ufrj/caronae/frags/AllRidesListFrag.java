package br.ufrj.caronae.frags;

import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideFiltersForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJsonDeserializer;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static br.ufrj.caronae.frags.AllRidesFrag.dismissSnack;
import static br.ufrj.caronae.frags.AllRidesFrag.makeLoadingRidesSnack;
import static br.ufrj.caronae.frags.AllRidesFrag.makeNoConexionSnack;
import static br.ufrj.caronae.frags.AllRidesFrag.showSnack;


public class AllRidesListFrag extends Fragment implements Callback {

    private int TOTAL_PAGES_COUNT = 5;
    @Bind(R.id.rvRides)
    RecyclerView rvRides;
//    @Bind(R.id.norides_tv)
//    TextView norides_tv;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.all_rides_list_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
//    @Bind(R.id.list_all_rides_search_text)
//    EditText searchText;
//    @Bind(R.id.search_card_view)
//    CardView searchCardView;

    private final int FIRST_PAGE_TO_LOAD = 0;

    long totalBytesConsumed = 0;

    RideOfferAdapter adapter;

    int pageCounter = FIRST_PAGE_TO_LOAD;

    private EndlessRecyclerViewScrollListener scrollListener;

    LinearLayoutManager mLayoutManager;

    int pageIdentifier;

    CharSequence filter = null;

    ArrayList<RideForJson> goingRides = new ArrayList<RideForJson>();
    ArrayList<RideForJson> notGoingRides = new ArrayList<RideForJson>();

    ArrayList<RideForJson> filteredGoingList = new ArrayList<>();
    ArrayList<RideForJson> filteredNotGoingList = new ArrayList<>();

    public AllRidesListFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides_list, container, false);
        ButterKnife.bind(this, view);

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.inst());

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
//        refreshRideList(pageCounter);
    }

    @Override
    public void onResume() {
        super.onResume();
//        refreshRideList(pageCounter);
//        AllRidesFrag.prepareFloatingActionMenu(getContext());
    }

    void refreshRideList(final int pageNumber) {

        final Snackbar snackbar = makeLoadingRidesSnack();
        final Snackbar snackbar1 = makeNoConexionSnack();
        if (snackbar1.isShown()){
            dismissSnack(snackbar1);
        }
        showSnack(snackbar);
        final long bytesSoFar = TrafficStats.getUidRxBytes(Process.myUid());

        String going = null;
        if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING)
            going = "1";
        else
            going = "0";
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

        App.getNetworkService(getContext()).listAllRides(pageNumber + "", going, neighborhoods, zone, hub)
                .enqueue(new retrofit2.Callback<RideForJsonDeserializer>() {
                    @Override
                    public void onResponse(Call<RideForJsonDeserializer> call, Response<RideForJsonDeserializer> response) {
                        totalBytesConsumed = totalBytesConsumed + TrafficStats.getUidRxBytes(Process.myUid()) - bytesSoFar;
                        Log.e("CONSUMPTION", "Bytes Consumed: " + totalBytesConsumed);

                        switch (pageIdentifier) {
                            case 0:
                                Log.e("CONSUMPTION", "Tamanho da lista " + goingRides.size());
                                break;
                            case 1:
                                Log.e("CONSUMPTION", "Tamanho da lista " + notGoingRides.size());
                                break;
                        }
                        if (response.isSuccessful()) {

                            if (pageCounter == FIRST_PAGE_TO_LOAD) {
                                goingRides = new ArrayList<RideForJson>();
                                notGoingRides = new ArrayList<RideForJson>();
                            }

                            RideForJsonDeserializer data = response.body();
                            List<RideForJson> rideOffers = data.getData();

                            if (rideOffers != null && !rideOffers.isEmpty()) {
//                                Collections.sort(rideOffers, new RideOfferComparatorByDateAndTime());

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
//                                    norides_tv.setVisibility(View.VISIBLE);
                                } else {
//                                    norides_tv.setVisibility(View.INVISIBLE);
//                                    if (filter == null) {
                                        adapter.makeList(goingRides);
//                                    }
//                                    else {
//                                        adapter.makeList(filterList(goingRides, filter));
//                                    }
                                    scrollListener.resetState();
                                }
                            } else {
                                if (notGoingRides == null || notGoingRides.isEmpty()) {
//                                    norides_tv.setVisibility(View.VISIBLE);
                                } else {
//                                    norides_tv.setVisibility(View.INVISIBLE);
//                                    if (filter == null) {
                                        adapter.makeList(notGoingRides);
//                                    }
//                                    else {
//                                        adapter.makeList(filterList(notGoingRides, filter));
//                                    }
                                    scrollListener.resetState();
                                }
                            }
                            refreshLayout.setRefreshing(false);
                        } else {
                            Util.treatResponseFromServer(response);
                            Util.toast(R.string.frag_allrides_errorGetRides);
                            refreshLayout.setRefreshing(false);
                            Log.e("listAllRides", response.message());
                        }
                        dismissSnack(snackbar);
                        scrollListener.resetState();
                    }

                    @Override
                    public void onFailure(Call<RideForJsonDeserializer> call, Throwable t) {
                        dismissSnack(snackbar);
                        snackbar1.setAction("CONECTAR", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refreshRideList(pageNumber);
                            }
                        });
                        showSnack(snackbar1);
//                        Util.toast(R.string.frag_allrides_errorGetRides);
                        refreshLayout.setRefreshing(false);
                        Log.e("listAllRides", t.getMessage());
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
//                searchAdapter = new RideOfferAdapter(listFiltered, context, activity.getFragmentManager());
//                searchAdapter.makeList(listFiltered);
                adapter.makeList(filteredGoingList);
            }
        } else {
            if (filteredNotGoingList.size() == 0) {
                filter = null;
                adapter.makeList(notGoingRides);
                Util.toast("Nenhuma carona encontrada para esta busca");
            } else {
                filter = (CharSequence) listFiltered.get(0);
//                searchAdapter = new RideOfferAdapter(listFiltered, context, activity.getFragmentManager());
//                searchAdapter.makeList(listFiltered);
                adapter.makeList(filteredNotGoingList);
            }
        }

    }

    private void loadOneMorePage() {
        pageCounter++;
        refreshRideList(pageCounter);
    }
}
