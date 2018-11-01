package br.ufrj.caronae.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.RidesHistoryAdapter;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.RideHistory;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class RidesHistoryFrag extends Fragment {

    @BindView(R.id.rvRides)
    RecyclerView rvRides;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.norides_tv)
    TextView noRides;
    LinearLayoutManager mLayoutManager;

    List<RideHistory> rides;
    RidesHistoryAdapter adapter;

    public RidesHistoryFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides_history, container, false);
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
                refreshRideList();
            }
        });

        adapter = new RidesHistoryAdapter(getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        rvRides.setLayoutManager(mLayoutManager);
        rvRides.setAdapter(adapter);

        if(!Util.isNetworkAvailable(getContext()))
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

        return view;
    }

    private void refreshRideList()
    {
        CaronaeAPI.service().getRidesHistory(Integer.toString(App.getUser().getDbId()))
                .enqueue(new retrofit2.Callback<RideHistoryForJson>() {
                    @Override
                    public void onResponse(Call<RideHistoryForJson> call, Response<RideHistoryForJson> response) {
                        if (response.isSuccessful()) {
                            RideHistoryForJson data = response.body();
                            List<RideHistory> ridesH = data.getRides();

                            if(ridesH != null && !ridesH.isEmpty())
                            {
                                setMyRides(ridesH);
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
                            rvRides.setVisibility(View.INVISIBLE);
                            noRides.setText(R.string.fragment_myrides_no_ride_found);
                            noRides.setVisibility(View.VISIBLE);
                            Util.debug(response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<RideHistoryForJson> call, Throwable t) {
                        refreshLayout.setRefreshing(false);
                        rvRides.setVisibility(View.INVISIBLE);
                        noRides.setText(R.string.fragment_myrides_no_ride_found);
                        noRides.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void setMyRides(List<RideHistory> ridesH)
    {
        rides = new ArrayList<>();
        rides.addAll(ridesH);
        if (rides != null && !rides.isEmpty())
        {
            adapter.makeList(rides);
            adapter.notifyDataSetChanged();
        }
        rvRides.setVisibility(View.VISIBLE);
    }
}
