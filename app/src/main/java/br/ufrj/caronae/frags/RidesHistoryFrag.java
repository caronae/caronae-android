package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.RidesHistoryAdapter;
import br.ufrj.caronae.comparators.RideComparatorByDateAndTimeReverse;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidesHistoryFrag extends Fragment {

    @BindView(R.id.myRidesList)
    RecyclerView myRidesList;
    @BindView(R.id.norides_tv)
    TextView norides_tv;

    public RidesHistoryFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides_history, container, false);
        ButterKnife.bind(this, view);

        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", getContext().getString(R.string.wait), true, true);
        CaronaeAPI.service(getContext()).getRidesHistory()
                .enqueue(new Callback<List<RideHistoryForJson>>() {
                    @Override
                    public void onResponse(Call<List<RideHistoryForJson>> call, Response<List<RideHistoryForJson>> response) {
                        if (response.isSuccessful()) {
                            List<RideHistoryForJson> historyRides = response.body();

                            if (historyRides == null || historyRides.isEmpty()) {
                                norides_tv.setVisibility(View.VISIBLE);
                                pd.dismiss();
                                return;
                            }

                            for (RideForJson rideHistory : historyRides) {
                                rideHistory.setDbId(rideHistory.getId().intValue());
                                rideHistory.setTime(Util.formatTime(rideHistory.getTime()));
                                rideHistory.setDate(Util.formatBadDateWithYear(rideHistory.getDate()));
                            }
                            Collections.sort(historyRides, new RideComparatorByDateAndTimeReverse());
                            myRidesList.setAdapter(new RidesHistoryAdapter(historyRides, (MainAct) getActivity()));
                            myRidesList.setHasFixedSize(true);
                            myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));

                            pd.dismiss();
                        } else {
                            Util.treatResponseFromServer(response);
                            norides_tv.setVisibility(View.VISIBLE);
                            pd.dismiss();
                            Util.toast(R.string.frag_rideshistory_errorGetRides);
                            Log.e("getRidesHistory", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RideHistoryForJson>> call, Throwable t) {
                        norides_tv.setVisibility(View.VISIBLE);
                        pd.dismiss();
                        Util.toast(R.string.frag_rideshistory_errorGetRides);
                        Log.e("getRidesHistory", t.getMessage());
                    }
                });

        return view;
    }
}
