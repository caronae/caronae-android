package br.ufrj.caronae.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.List;

import javax.security.auth.callback.Callback;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
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

    public MyRidesFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);
        ButterKnife.bind(this, view);

        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        setHasOptionsMenu(true);
        MainAct.showMainItems();

        refreshRideList();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_my_rides, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    private void refreshRideList()
    {
        CaronaeAPI.service(getContext()).getMyRides(Integer.toString(App.getUser().getDbId()))
            .enqueue(new retrofit2.Callback<MyRidesForJson>() {
                @Override
                public void onResponse(Call<MyRidesForJson> call, Response<MyRidesForJson> response) {
                    if (response.isSuccessful()) {
                        MyRidesForJson data = response.body();
                        List<RideForJson> activeRides = data.getActiveRides();
                        List<RideForJson> offeredRides = data.getOfferedRides();
                        List<RideForJson> pendingRides = data.getPendingRides();

                        setMyRides(activeRides, offeredRides, pendingRides);
                    } else {
                        Util.treatResponseFromServer(response);
                        Util.debug(response.message());
                    }
                }
                @Override
                public void onFailure(Call<MyRidesForJson> call, Throwable t) {
                        Util.debug(t.getMessage());
                }
            });
    }

    private void setMyRides(List<RideForJson> activeRides, List<RideForJson> offeredRides, List<RideForJson> pendingRides)
    {

    }
}
