package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Process;
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
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.AllRidesFragmentPagerAdapter;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideSearchFiltersForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class AllRidesListFrag extends Fragment implements Callback {
    @Bind(R.id.rvRides)
    RecyclerView rvRides;
    @Bind(R.id.norides_tv)
    TextView norides_tv;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
//    @Bind(R.id.list_all_rides_search_text)
//    EditText searchText;
//    @Bind(R.id.search_card_view)
//    CardView searchCardView;

    long totalBytesConsumed = 0;

    RideOfferAdapter adapter;

    int listCounter = 1;

    private EndlessRecyclerViewScrollListener scrollListener;

    LinearLayoutManager mLayoutManager;

    int pageIdentifier;

    CharSequence filter;

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
//                listCounter = 1;
                for (int counter = 1; counter <= listCounter; counter++) {
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
//        rvRides.setHasFixedSize(true);

        if (rideOffers == null || rideOffers.isEmpty()) {
            norides_tv.setVisibility(View.VISIBLE);
        } else {
            adapter.makeList(rideOffers);
        }

        App.getBus().register(this);

//        String[] neighborhoods = Util.getAllNeighborhoods();
//        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, neighborhoods);
//        searchText.setAdapter(autoCompleteAdapter);

//        searchText.addTextChangedListener(new TextWatcher() {
//            RideOfferAdapter searchAdapter;
//            boolean isGoing = true;
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                switch (AllRidesFragmentPagerAdapter.PAGE_GOING){
//                    case 0:
//                        isGoing = true;
//                        break;
//                    case 1:
//                        isGoing = false;
//                        break;
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    rvRides.setAdapter(adapter);
//                } else {
////                    String[] filters = Util.searchAlgorithin(s.toString(), Util.getAllNeighborhoods());
////
////                    List<RideForJson> listFiltered = makeSearchOnline(filters[0], filters[1], filters[2], filters[3], isGoing, filters[0]);
//
//                    if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING) {
//                        ArrayList<RideForJson> listFiltered = filterList(goingRides, s);
//                        searchAdapter = new RideOfferAdapter(listFiltered, getContext(), getActivity().getFragmentManager());
//                        searchAdapter.makeList(listFiltered);
//                    } else {
//                        ArrayList<RideForJson> listFiltered = filterList(notGoingRides, s);
//                        searchAdapter = new RideOfferAdapter(listFiltered, getContext(), getActivity().getFragmentManager());
//                        searchAdapter.makeList(listFiltered);
//                    }
//                    rvRides.setAdapter(searchAdapter);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        // After setting layout manager, adapter, etc...
        float offsetBottonPx = getResources().getDimension(R.dimen.recycler_view_botton_offset);
        float offsetTopPx = getResources().getDimension(R.dimen.recycler_view_top_offset);
        Util.OffsetDecoration OffsetDecoration = new Util.OffsetDecoration((int) offsetBottonPx, (int) offsetTopPx);
        rvRides.addItemDecoration(OffsetDecoration);

        animateListFadeIn();

        return view;
    }

//    @Subscribe
//    public void removeRideFromList(RideRequestSent ride) {
//        adapter.remove(ride.getDbId());
//        Log.i("removeRideFromList,all", "remove called");
//    }

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
    public void onResume() {
        super.onResume();
        refreshRideList(listCounter);
//        AllRidesFrag.prepareFloatingActionMenu(getContext());
    }

    void refreshRideList(final int pageNumber) {

        final long bytesSoFar = TrafficStats.getUidRxBytes(Process.myUid());
        App.getNetworkService(getContext()).listAllRides(pageNumber + "")
                .enqueue(new retrofit2.Callback<List<RideForJson>>() {
                    @Override
                    public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {
                        totalBytesConsumed = totalBytesConsumed + TrafficStats.getUidRxBytes(Process.myUid()) - bytesSoFar;
                        Log.e("CONSUMPTION", "Bytes Consumed: " + totalBytesConsumed);

                        switch (pageIdentifier){
                            case 0:
                                Log.e("CONSUMPTION", "Tamanho da lista " + goingRides.size());
                                break;
                            case 1:
                                Log.e("CONSUMPTION", "Tamanho da lista " + notGoingRides.size());
                                break;
                        }
                        if (response.isSuccessful()) {

                            if (listCounter == 1) {
                                goingRides = new ArrayList<RideForJson>();
                                notGoingRides = new ArrayList<RideForJson>();
                            }

                            List<RideForJson> rideOffers = response.body();

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
                                                goingRides.add(rideOffer);
                                        } else {
                                                notGoingRides.add(rideOffer);
                                        }
                                    }
                                }

                                Collections.sort(goingRides, new RideOfferComparatorByDateAndTime());
                                Collections.sort(notGoingRides, new RideOfferComparatorByDateAndTime());

                            }

//                            adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getContext());
//                            rvRides.setAdapter(adapter);
//                            rvRides.setHasFixedSize(true);
//                            rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

                            if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING) {
                                if (goingRides == null || goingRides.isEmpty()) {
                                    norides_tv.setVisibility(View.VISIBLE);
                                } else {
                                    norides_tv.setVisibility(View.INVISIBLE);
                                    if (filter == null) {
                                        adapter.makeList(goingRides);
                                    } else {
                                        adapter.makeList(filterList(goingRides, filter));
                                    }
                                    scrollListener.resetState();
                                }
                            } else {
                                if (notGoingRides == null || notGoingRides.isEmpty()) {
                                    norides_tv.setVisibility(View.VISIBLE);
                                } else {
                                    norides_tv.setVisibility(View.INVISIBLE);
                                    if (filter == null) {
                                        adapter.makeList(notGoingRides);
                                    } else {
                                        adapter.makeList(filterList(notGoingRides, filter));
                                    }
                                    scrollListener.resetState();
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

        if (filter != null){
            switch (pageNumber){
                case 0:
                    if (filteredGoingList.size() <= 8){
                        loadOneMorePage();
                    }
                    break;
                case 1:
                    if (filteredNotGoingList.size() <= 8){
                        loadOneMorePage();
                    }
                    break;
            }
        }

    }

    private boolean checkIfRideIsInList(ArrayList<RideForJson> list, RideForJson ride) {
        boolean contains = false;
        for (int counter = 0; counter < list.size(); counter++) {
            if (list.get(counter).getDbId() == ride.getId().intValue())
                contains = true;
        }
        return contains;
    }

    private ArrayList<RideForJson> filterList(ArrayList<RideForJson> listToFilter, CharSequence searchText) {
        ArrayList<RideForJson> listFiltered = new ArrayList<>();
        for (int ride = 0; ride < listToFilter.size(); ride++) {
            if (listToFilter.get(ride).getNeighborhood().toLowerCase().contains(searchText.toString().toLowerCase()))
                listFiltered.add(listToFilter.get(ride));
        }
        return listFiltered;
    }

    private void animateListFadeIn() {
        Animation anim = new AlphaAnimation(0, 1);
        anim.setDuration(300);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        rvRides.startAnimation(anim);
    }

    private List<RideForJson> makeSearchOnline(String location, String date, String time, String center, boolean go, String locationResumedField) {
        final List<RideForJson> listFiltered = new ArrayList<>();
        RideSearchFiltersForJson rideSearchFilters = new RideSearchFiltersForJson(location, date, time, center, go, locationResumedField);

        Log.e("INPUT", "location: " + location);
        Log.e("INPUT", "data: " + date);
        Log.e("INPUT", "hora: " + time);
        Log.e("INPUT", "center: " + center);
        Log.e("INPUT", "locationResumeField: " + locationResumedField);

        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", getContext().getString(R.string.wait), true, true);
        App.getNetworkService(getContext()).listFiltered(rideSearchFilters)
                .enqueue(new retrofit2.Callback<List<RideForJson>>() {
                    @Override
                    public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {
                        if (response.isSuccessful()) {
                            List<RideForJson> rideOffers = response.body();
                            if (rideOffers != null && !rideOffers.isEmpty()) {
                                Collections.sort(rideOffers, new RideOfferComparatorByDateAndTime());
                                for (RideForJson rideOffer : rideOffers) {
                                    rideOffer.setDbId(rideOffer.getId().intValue());
                                }
                                listFiltered.addAll(rideOffers);
                            } else {
                                Util.toast(R.string.frag_rideSearch_noRideFound);
                                adapter.makeList(new ArrayList<RideForJson>());
                            }
                            pd.dismiss();
                        } else {
                            pd.dismiss();
                            Util.toast(R.string.frag_rideSearch_errorListFiltered);
                            Log.e("listFiltered", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                        pd.dismiss();
                        Util.toast(R.string.frag_rideSearch_errorListFiltered);
                        Log.e("listFiltered", t.getMessage());
                    }
                });
        return listFiltered;
    }

    @Subscribe
    public void updateAdapter(ArrayList<Object> listFiltered) {
        filteredGoingList.clear();
        filteredNotGoingList.clear();

        for (int rides = 1 ; rides < listFiltered.size(); rides++){
            RideForJson ride = (RideForJson) listFiltered.get(rides);
            if (ride.isGoing()){
                filteredGoingList.add((RideForJson)listFiltered.get(rides));
            } else {
                filteredNotGoingList.add((RideForJson)listFiltered.get(rides));
            }
        }

        if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING) {
            if (filteredGoingList.size() == 0){
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
            if (filteredNotGoingList.size() == 0){
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

    private void loadOneMorePage(){
        listCounter++;
        refreshRideList(listCounter);
    }
}
