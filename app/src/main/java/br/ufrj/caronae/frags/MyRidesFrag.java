package br.ufrj.caronae.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.callback.Callback;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.comparators.SortRides;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.MyRidesAdapter;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.modelsforjson.MyRidesForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class MyRidesFrag extends Fragment implements Callback
{
    @BindView(R.id.rvRides)
    RecyclerView rvRides;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.norides_tv)
    TextView noRides;
    LinearLayoutManager mLayoutManager;

    List<RideForJson> myRides;

    MyRidesAdapter adapter;

    boolean userRequestedUpdate;

    public MyRidesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);
        ButterKnife.bind(this, view);

        refreshLayout.setProgressViewOffset(false, getResources().getDimensionPixelSize(R.dimen.refresher_offset), getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));

        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPref.lastMyRidesUpdate = 0;
                userRequestedUpdate = true;
                refreshRideList();
            }
        });

        setHasOptionsMenu(true);
        ((MainAct)getActivity()).showMainItems();

        adapter = new MyRidesAdapter(getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        rvRides.setLayoutManager(mLayoutManager);
        rvRides.setAdapter(adapter);

        if(SharedPref.OPEN_MY_RIDES)
        {
            if(!SharedPref.MY_RIDES_ACTIVE.isEmpty() || !SharedPref.MY_RIDES_OFFERED.isEmpty() || !SharedPref.MY_RIDES_PENDING.isEmpty())
            {
                noRides.setVisibility(View.GONE);
                setMyRides(SharedPref.MY_RIDES_ACTIVE, SharedPref.MY_RIDES_OFFERED, SharedPref.MY_RIDES_PENDING);
            }
            else
            {
                rvRides.setVisibility(View.INVISIBLE);
                noRides.setText(R.string.fragment_myrides_no_ride_found);
                noRides.setVisibility(View.VISIBLE);
            }
        }
        else if(!Util.isNetworkAvailable(getContext()))
        {
            rvRides.setVisibility(View.INVISIBLE);
            noRides.setText(R.string.fragment_allrides_norides);
            noRides.setVisibility(View.VISIBLE);
        }
        else
        {
            rvRides.setVisibility(View.INVISIBLE);
            noRides.setText(R.string.charging);
            noRides.setVisibility(View.VISIBLE);
        }

        refreshRideList();

        App.getBus().register(this);

        float offsetBottonPx = getResources().getDimension(R.dimen.recycler_my_rides_botton_offset);
        Util.OffsetDecoration OffsetDecoration = new Util.OffsetDecoration((int) offsetBottonPx, 0);
        rvRides.addItemDecoration(OffsetDecoration);
        reloadMyRidesIfNecessary();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_my_rides, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    private void refreshRideList()
    {
        CaronaeAPI.service().getMyRides(Integer.toString(App.getUser().getDbId()))
            .enqueue(new retrofit2.Callback<MyRidesForJson>() {
                @Override
                public void onResponse(Call<MyRidesForJson> call, Response<MyRidesForJson> response) {
                    if (response.isSuccessful()) {
                        MyRidesForJson data = response.body();
                        List<RideForJson> activeRides = data.getActiveRides();
                        List<RideForJson> offeredRides = data.getOfferedRides();
                        List<RideForJson> pendingRides = data.getPendingRides();
                        SharedPref.OPEN_MY_RIDES = true;
                        SharedPref.MY_RIDES_ACTIVE = activeRides;
                        SharedPref.MY_RIDES_OFFERED = offeredRides;
                        SharedPref.MY_RIDES_PENDING = pendingRides;
                        if(!activeRides.isEmpty()) {
                            List<Integer> aRideId = new ArrayList<>();
                            for(int i = 0; i < activeRides.size(); i++)
                            {
                                aRideId.add(activeRides.get(i).getId().intValue());
                            }
                            SharedPref.setMyActiveRidesId(aRideId);
                        }
                        if(!pendingRides.isEmpty())
                        {
                            List<Integer> pRideId = new ArrayList<>();
                            for(int i = 0; i < pendingRides.size(); i++)
                            {
                                pRideId.add(pendingRides.get(i).getId().intValue());
                            }
                            SharedPref.setMyPendingRidesId(pRideId);
                        }
                        if(!activeRides.isEmpty() || !offeredRides.isEmpty() || !pendingRides.isEmpty())
                        {
                            setMyRides(activeRides, offeredRides, pendingRides);
                            noRides.setVisibility(View.GONE);
                        }
                        else
                        {
                            rvRides.setVisibility(View.INVISIBLE);
                            noRides.setText(R.string.fragment_myrides_no_ride_found);
                            noRides.setVisibility(View.VISIBLE);
                        }
                        refreshLayout.setRefreshing(false);
                    } else {
                        Util.treatResponseFromServer(response);
                        refreshLayout.setRefreshing(false);
                        if(userRequestedUpdate) {
                            userRequestedUpdate = false;
                            rvRides.setVisibility(View.INVISIBLE);
                            noRides.setText(R.string.fragment_allrides_norides);
                            noRides.setVisibility(View.VISIBLE);
                        }else if(!SharedPref.OPEN_MY_RIDES)
                        {
                            rvRides.setVisibility(View.INVISIBLE);
                            noRides.setText(R.string.fragment_myrides_no_ride_found);
                            noRides.setVisibility(View.VISIBLE);
                        }
                        Util.debug(response.message());
                    }
                }
                @Override
                public void onFailure(Call<MyRidesForJson> call, Throwable t) {
                    refreshLayout.setRefreshing(false);
                    if(userRequestedUpdate) {
                        userRequestedUpdate = false;
                        rvRides.setVisibility(View.INVISIBLE);
                        noRides.setText(R.string.fragment_allrides_norides);
                        noRides.setVisibility(View.VISIBLE);
                    }else if(!SharedPref.OPEN_MY_RIDES)
                    {
                        rvRides.setVisibility(View.INVISIBLE);
                        noRides.setText(R.string.fragment_myrides_no_ride_found);
                        noRides.setVisibility(View.VISIBLE);
                    }
                    Util.debug(t.getMessage());
                }
            });
    }

    private void setMyRides(List<RideForJson> activeRides, List<RideForJson> offeredRides, List<RideForJson> pendingRides)
    {
        myRides = new ArrayList<>();

        if (pendingRides != null && !pendingRides.isEmpty()) {
            Collections.sort(pendingRides, new SortRides());
            pendingRides.get(0).type = "Pendentes";
            myRides.addAll(pendingRides);
        }
        if (activeRides != null && !activeRides.isEmpty())
        {
            Collections.sort(activeRides, new SortRides());
            activeRides.get(0).type = "Ativas";
            myRides.addAll(activeRides);
        }
        if (offeredRides != null && !offeredRides.isEmpty())
        {
            Collections.sort(offeredRides, new SortRides());
            offeredRides.get(0).type = "Ofertadas";
            myRides.addAll(offeredRides);
        }

        if (myRides != null && !myRides.isEmpty())
        {
            if(activeRides != null && !activeRides.isEmpty())
            {
                myRides.get(myRides.size()-1).showWarningText = true;
            }
            for(int i = 0; i < myRides.size(); i++)
            {
                myRides.get(i).fromWhere = "Minhas";
            }
            adapter.makeList(myRides);
            adapter.notifyDataSetChanged();
        }
        rvRides.setVisibility(View.VISIBLE);
    }

    private void reloadMyRidesIfNecessary()
    {
        //Verifies every second if a reload is necessary
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                if(SharedPref.lastMyRidesUpdate >= 300)
                {
                    SharedPref.lastMyRidesUpdate = 0;
                    refreshRideList();
                }
            }
        };
        timer.schedule (hourlyTask, 0, 1000);
    }
}
