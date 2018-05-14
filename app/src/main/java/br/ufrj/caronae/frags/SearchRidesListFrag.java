package br.ufrj.caronae.frags;

import android.content.Context;
import android.os.Bundle;
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
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.callback.Callback;

import br.ufrj.caronae.App;
import br.ufrj.caronae.CustomDialogClass;
import br.ufrj.caronae.EndlessRecyclerViewScrollListener;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJsonDeserializer;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class SearchRidesListFrag extends Fragment implements Callback {

    @BindView(R.id.rvRides)
    RecyclerView rvRides;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.norides_tv)
    TextView noRides;

    RideOfferAdapter adapter;
    LinearLayoutManager mLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    private final int FIRST_PAGE_TO_LOAD = 0;
    int pageCounter = FIRST_PAGE_TO_LOAD;
    String going;

    ArrayList<RideForJson> rides = new ArrayList<>();


    public SearchRidesListFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_rides_list, container, false);
        ButterKnife.bind(this, view);

        Context ctx;
        ctx = getContext();
        refreshLayout.setProgressViewOffset(false, getResources().getDimensionPixelSize(R.dimen.refresher_offset), getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        try {
            going = getArguments().getString("isGoing", "1");
        }catch (Exception e){}
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPref.lastAllRidesUpdate = 0;
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

        if(!Util.isNetworkAvailable(ctx))
        {
            noRides.setText(R.string.allrides_norides);
            noRides.setVisibility(View.VISIBLE);
        }
        else
        {
            noRides.setText(R.string.charging);
            noRides.setVisibility(View.VISIBLE);
        }

        App.getBus().register(this);

        // After setting layout manager, adapter, etc...
        float offsetBottonPx = getResources().getDimension(R.dimen.recycler_view_botton_offset);
        float offsetTopPx = getResources().getDimension(R.dimen.recycler_view_top_offset);
        Util.OffsetDecoration OffsetDecoration = new Util.OffsetDecoration((int) offsetBottonPx, (int) offsetTopPx);
        rvRides.addItemDecoration(OffsetDecoration);

        animateListFadeIn();

        reloadRidesIfNecessary();
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
        final Fragment frag = this;
        String neighborhoods = null;
        String zone = null;
        String hub = null;
        String campus = null;
        String date = Util.formatBadDateWithYear(SharedPref.getDateSearch());
        String time = SharedPref.getTimeSearch();

        if(Util.isZone(SharedPref.getLocationSearch()))
        {
            zone = SharedPref.getLocationSearch();
        }
        else
        {
            if(!SharedPref.getLocationSearch().equals("Todos os Bairros")) {
                neighborhoods = SharedPref.getLocationSearch();
            }
        }
        if(Util.isCampus(SharedPref.getCenterSearch()))
        {
            campus = SharedPref.getCenterSearch();
        }
        else
        {
            if(!SharedPref.getCenterSearch().equals("Todos os Campi")) {
                hub = SharedPref.getCenterSearch();
            }
        }
        time = time.replace(" ", "");
        CaronaeAPI.service(getContext()).listAllRides(pageNumber + "", going, neighborhoods, zone, hub,  "", campus, date, time)
                .enqueue(new retrofit2.Callback<RideForJsonDeserializer>() {
                    @Override
                    public void onResponse(Call<RideForJsonDeserializer> call, Response<RideForJsonDeserializer> response) {
                        if (response.isSuccessful()) {
                            if (pageCounter == FIRST_PAGE_TO_LOAD) {
                                rides = new ArrayList<>();
                            }
                            RideForJsonDeserializer data = response.body();
                            List<RideForJson> rideOffers = data.getData();
                            if(rideOffers.size() != 0) {
                                noRides.setVisibility(View.GONE);
                                setRides(rideOffers);
                            }
                            else
                            {
                                refreshLayout.setRefreshing(false);
                                CustomDialogClass cdc = new CustomDialogClass(getActivity(), "searchList", frag);
                                cdc.show();
                                cdc.enableOnePositiveOption();
                                cdc.setPButtonText(getResources().getString(R.string.ok));
                                cdc.setTitleText(getResources().getString(R.string.no_rides_found_title));
                                cdc.setMessageText(getResources().getString(R.string.no_rides_found_msg));
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
                        noRides.setText(R.string.allrides_norides);
                        scrollListener.resetState();
                    }
                });
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

    private void setRides(List<RideForJson> rideOffers)
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
                    if (!checkIfRideIsInList(rides, rideOffer)){
                        rides.add(rideOffer);
                    }
                }
            }
            Collections.sort(rides, new RideOfferComparatorByDateAndTime());
        }
        if (rides != null && !rides.isEmpty()) {
            adapter.makeList(rides);
            scrollListener.resetState();
            adapter.notifyDataSetChanged();
        }
        refreshLayout.setRefreshing(false);
        rvRides.setVisibility(View.VISIBLE);
    }

    private void loadOneMorePage() {
        pageCounter++;
        refreshRideList(pageCounter);
    }

    private void reloadRidesIfNecessary()
    {
        //Verifies every half second if a reload is necessary
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                if(SharedPref.lastAllRidesUpdate >= 300)
                {
                    pageCounter = FIRST_PAGE_TO_LOAD;
                    for (int counter = FIRST_PAGE_TO_LOAD; counter <= pageCounter; counter++) {
                        refreshRideList(counter);
                    }
                }
            }
        };
        timer.schedule (hourlyTask, 0, 500);
    }
}