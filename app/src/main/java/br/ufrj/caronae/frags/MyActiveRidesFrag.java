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

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.asyncs.CheckSubGcmTopic;
import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.MyActiveRidesAdapter;
import br.ufrj.caronae.models.modelsforjson.RideWithUsersForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyActiveRidesFrag extends Fragment {

    @Bind(R.id.myRidesList)
    RecyclerView myRidesList;
    @Bind(R.id.norides_tv)
    TextView norides_tv;

    public MyActiveRidesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_active_rides, container, false);
        ButterKnife.bind(this, view);

        final ProgressDialog pd = ProgressDialog.show(getContext(), "", getActivity().getString(R.string.wait), true, true);
        App.getNetworkService().getMyActiveRides(new Callback<List<RideWithUsersForJson>>() {
            @Override
            public void success(List<RideWithUsersForJson> rideWithUsersList, Response response) {
                if (rideWithUsersList == null || rideWithUsersList.isEmpty()) {
                    pd.dismiss();

                    norides_tv.setVisibility(View.VISIBLE);
                    return;
                }

                //subscribe to ride id topic
                if (!SharedPref.getUserGcmToken().equals(SharedPref.MISSING_PREF)) {
                    Log.i("getMyActiveRides", "i have gcm token");
                    for (RideWithUsersForJson rideWithUsers : rideWithUsersList) {
                        int rideId = rideWithUsers.getRide().getId().intValue();
                        new CheckSubGcmTopic().execute(rideId+"");
                    }
                } else {
                    Log.i("getMyActiveRides", "i DO NOT have gcm token");
                }

                myRidesList.setAdapter(new MyActiveRidesAdapter(rideWithUsersList, (MainAct) getActivity()));
                myRidesList.setHasFixedSize(true);
                myRidesList.setLayoutManager(new LinearLayoutManager(getActivity()));

                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();

                norides_tv.setVisibility(View.VISIBLE);
                Util.toast(getContext().getString(R.string.frag_myactiverides_errorGetActiveRides));

                Log.e("getMyActiveRides", error.getMessage());
            }
        });

        return view;
    }
}
