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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByTime;
import br.ufrj.caronae.models.RideOffer;
import br.ufrj.caronae.models.RideSearchFilters;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RideSearchFrag extends Fragment {
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.zone_et)
    EditText zone_et;
    @Bind(R.id.neighborhood_et)
    EditText neighborhood_et;
    @Bind(R.id.date_et)
    TextView date_et;
    @Bind(R.id.lay)
    RelativeLayout lay;
    @Bind(R.id.anotherSearch_bt)
    Button anotherSearch_bt;

    @Bind(R.id.rvRides)
    RecyclerView rvRides;

    private RideOfferAdapter adapter;

    public RideSearchFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_search, container, false);
        ButterKnife.bind(this, view);

        adapter = new RideOfferAdapter(new ArrayList<RideOffer>());
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        String lastRideSearchFilters = App.getPref("lastRideSearchFilters");
        if (!lastRideSearchFilters.equals(App.MISSING_PREF)) {
            loadLastFilters(lastRideSearchFilters);
        } else {
            date_et.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(Calendar.getInstance().getTime()));
        }

        return view;
    }

    private void loadLastFilters(String lastRideSearchFilters) {
        RideSearchFilters rideSearchFilters = new Gson().fromJson(lastRideSearchFilters, RideSearchFilters.class);

        zone_et.setText(rideSearchFilters.getZone());
        neighborhood_et.setText(rideSearchFilters.getNeighborhood());
        date_et.setText(rideSearchFilters.getDate());
        boolean go = rideSearchFilters.isGo();
        radioGroup.check(go ? R.id.go_rb : R.id.back_rb);
    }

    @OnClick(R.id.date_et)
    public void dateEt() {
        Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker_Light) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                date_et.setText(dialog.getFormattedDate(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())));
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.positiveAction("OK")
                .negativeAction("Cancelar");

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.anotherSearch_bt)
    public void anotherSearchBt() {
        App.expandOrCollapse(lay, true);
        anotherSearch_bt.setVisibility(View.GONE);
    }

    @OnClick(R.id.search_bt)
    public void searchBt() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Aguarde", true, true);

        String zone = zone_et.getText().toString();
        String neighborhood = neighborhood_et.getText().toString();
        String date = date_et.getText().toString();
        boolean go = radioGroup.getCheckedRadioButtonId() == R.id.go_rb;
        RideSearchFilters rideSearchFilters = new RideSearchFilters(zone, neighborhood, date, go);

        String lastRideSearchFilters = new Gson().toJson(rideSearchFilters);
        App.putPref("lastRideSearchFilters", lastRideSearchFilters);

        App.getNetworkService().getRideOffers(rideSearchFilters, new Callback<List<RideOffer>>() {
            @Override
            public void success(List<RideOffer> rideOffer, Response response) {
                if (rideOffer != null) {
                    App.expandOrCollapse(lay, false);
                    anotherSearch_bt.setVisibility(View.VISIBLE);
                    Collections.sort(rideOffer, new RideOfferComparatorByTime());
                    adapter.makeList(rideOffer);
                } else {
                    Toast.makeText(App.inst(), "Nenhuma carona encontrada", Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getRideOffers", error.getMessage());
                pd.dismiss();
            }
        });
    }
}
