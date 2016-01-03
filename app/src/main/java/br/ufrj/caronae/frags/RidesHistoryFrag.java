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

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.RidesHistoryAdapter;
import br.ufrj.caronae.comparators.RideComparatorByDateAndTime;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RidesHistoryFrag extends Fragment {

    @Bind(R.id.myRidesList)
    RecyclerView myRidesList;
    @Bind(R.id.norides_tv)
    TextView norides_tv;

    public RidesHistoryFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides_history, container, false);
        ButterKnife.bind(this, view);

        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", getContext().getString(R.string.wait), true, true);
        App.getNetworkService().getRidesHistory(new Callback<List<RideHistoryForJson>>() {
            @Override
            public void success(List<RideHistoryForJson> historyRides, Response response) {
                if (historyRides == null || historyRides.isEmpty()) {
                    norides_tv.setVisibility(View.VISIBLE);
                    pd.dismiss();
                    return;
                }

                for (RideForJson rideHistory : historyRides) {
                    rideHistory.setDbId(rideHistory.getId().intValue());
                }
                Collections.sort(historyRides, new RideComparatorByDateAndTime());
                myRidesList.setAdapter(new RidesHistoryAdapter(historyRides, (MainAct) getActivity()));
                myRidesList.setHasFixedSize(true);
                myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));

                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                norides_tv.setVisibility(View.VISIBLE);
                pd.dismiss();
                Util.toast(R.string.frag_rideshistory_errorGetRides);
                Log.e("getRidesHistory", error.getMessage());
            }
        });

        return view;
    }
}
