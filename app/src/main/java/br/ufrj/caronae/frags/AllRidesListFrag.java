package br.ufrj.caronae.frags;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
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
import br.ufrj.caronae.EndlessRecyclerViewScrollListener;
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
import butterknife.OnClick;
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
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.list_all_rides_search_text)
    EditText searchText;
    @Bind(R.id.search_card_view)
    CardView searchCardView;

    RideOfferAdapter adapter;

    int listCounter = 1;

    private EndlessRecyclerViewScrollListener scrollListener;

    LinearLayoutManager mLayoutManager;

    int pageIdentifier;

    ArrayList<RideForJson> goingRides = new ArrayList<RideForJson>();
    ArrayList<RideForJson> notGoingRides = new ArrayList<RideForJson>();

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
                listCounter = 1;
                refreshRideList(listCounter);
            }
        });

        adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getContext());

        mLayoutManager = new LinearLayoutManager(getContext());
        rvRides.setLayoutManager(mLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.e("LOAD", "load: " + totalItemsCount);
                listCounter++;
                refreshRideList(listCounter);
            }
        };
        rvRides.addOnScrollListener(scrollListener);


        rvRides.setAdapter(adapter);
//        rvRides.setHasFixedSize(true);

        if (rideOffers == null || rideOffers.isEmpty()) {
            norides_tv.setVisibility(View.VISIBLE);
            helpText_tv.setVisibility(View.INVISIBLE);
        } else {
            adapter.makeList(rideOffers);
        }

        App.getBus().register(this);

        searchText.addTextChangedListener(new TextWatcher() {
            RideOfferAdapter searchAdapter;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (pageIdentifier == AllRidesFragmentPagerAdapter.PAGE_GOING)
                    searchAdapter = new RideOfferAdapter(filterList(goingRides, s), getContext());
                else
                    searchAdapter = new RideOfferAdapter(filterList(notGoingRides, s), getContext());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    rvRides.setAdapter(searchAdapter);
                } else {
                    rvRides.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        final Activity activity = getActivity();
//        final View content = view;
//        if (content.getWidth() > 0) {
//            Bitmap image = Util.BlurBuilder.blur(content);
//            searchCardView.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
//        } else {
//            content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    Bitmap image = Util.BlurBuilder.blur(content);
//                    searchCardView.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
//                }
//            });
//        }

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

    @Override
    public void onResume() {
        super.onResume();
        refreshRideList(listCounter);
    }

    void refreshRideList(final int pageNumber) {

        App.getNetworkService(getContext()).listAllRides(pageNumber + "")
                .enqueue(new retrofit2.Callback<List<RideForJson>>() {
                    @Override
                    public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {
                        if (response.isSuccessful()) {

                            boolean newGoingRides = false;
                            boolean newNotGoindRides = false;

                            if (listCounter == 1){
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
                                            if (!checkIfRideIsInList(goingRides, rideOffer)) {
                                                goingRides.add(rideOffer);
                                                newGoingRides = true;
                                            }
                                        }
                                        else {
                                            if (!checkIfRideIsInList(notGoingRides, rideOffer)) {
                                                notGoingRides.add(rideOffer);
                                                newNotGoindRides = true;
                                            }
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
                                    helpText_tv.setVisibility(View.INVISIBLE);
                                } else {
                                    norides_tv.setVisibility(View.INVISIBLE);
                                    helpText_tv.setVisibility(View.VISIBLE);
                                    if (newGoingRides) {
                                        adapter.makeList(goingRides);
                                    }
                                    scrollListener.resetState();
                                }
                            } else {
                                if (notGoingRides == null || notGoingRides.isEmpty()) {
                                    norides_tv.setVisibility(View.VISIBLE);
                                    helpText_tv.setVisibility(View.INVISIBLE);
                                } else {
                                    norides_tv.setVisibility(View.INVISIBLE);
                                    helpText_tv.setVisibility(View.VISIBLE);
                                    if (newNotGoindRides) {
                                        adapter.makeList(notGoingRides);
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


    }

    private boolean checkIfRideIsInList(ArrayList<RideForJson> list, RideForJson ride){
        boolean contains = false;
        for (int counter = 0; counter < list.size(); counter++){
            if (list.get(counter).getDbId() == ride.getId().intValue())
                contains = true;
        }
        return contains;
    }

    private ArrayList<RideForJson> filterList(ArrayList<RideForJson> listToFilter, CharSequence searchText){
        ArrayList<RideForJson> listFiltered = new ArrayList<>();
        for (int ride = 0; ride < listToFilter.size(); ride++){
            if (listToFilter.get(ride).getNeighborhood().toLowerCase().contains(searchText.toString().toLowerCase()))
                listFiltered.add(listToFilter.get(ride));
        }
        return listFiltered;
    }

    @OnClick(R.id.fab)
    public void fab() {
        ((MainAct) getActivity()).showRideOfferFrag();
    }
}
