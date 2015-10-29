package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByTime;
import br.ufrj.caronae.models.RideOfferForJson;
import br.ufrj.caronae.models.RideSearchFiltersForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RideSearchFrag extends Fragment {
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.location_et)
    EditText location_et;
    @Bind(R.id.date_et)
    TextView date_et;
    @Bind(R.id.time_et)
    TextView time_et;
    @Bind(R.id.center_et)
    TextView center_et;
    @Bind(R.id.lay)
    RelativeLayout lay;
    @Bind(R.id.anotherSearch_bt)
    Button anotherSearch_bt;
    @Bind(R.id.fab)
    FloatingActionButton fab;

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

        adapter = new RideOfferAdapter(new ArrayList<RideOfferForJson>(), getActivity());
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        String lastRideSearchFilters = App.getPref(App.LAST_RIDE_SEARCH_FILTERS_PREF_KEY);
        if (!lastRideSearchFilters.equals(App.MISSING_PREF)) {
            loadLastFilters(lastRideSearchFilters);
        }

        return view;
    }

    private void loadLastFilters(String lastRideSearchFilters) {
        RideSearchFiltersForJson rideSearchFilters = new Gson().fromJson(lastRideSearchFilters, RideSearchFiltersForJson.class);

        location_et.setText(rideSearchFilters.getLocation());
        date_et.setText(rideSearchFilters.getDate());
        time_et.setText(rideSearchFilters.getTime());
        center_et.setText(rideSearchFilters.getCenter());
        boolean go = rideSearchFilters.isGo();
        radioGroup.check(go ? R.id.go_rb : R.id.back_rb);
    }

    @OnClick(R.id.location_et)
    public void locationEt() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                CharSequence[] selectedZones = getSelectedValues();
                if (selectedZones != null) {
                    if (selectedZones.length == 1) {
                        locationEt2(selectedZones[0].toString());
                    } else {
                        String zone = "";
                        for (int i = 0; i < selectedZones.length; i++) {
                            zone += selectedZones[i];
                            if (i + 1 != selectedZones.length) {
                                zone += ", ";
                            }
                        }
                        location_et.setText(zone);
                    }
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.multiChoiceItems(App.getZones(), 0)
                .title("Escolha a(s) zona(s)")
                .positiveAction("OK")
                .negativeAction("Cancelar");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    public void locationEt2(final String zone) {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                CharSequence[] selectedNeighborhoods = getSelectedValues();
                if (selectedNeighborhoods != null) {
                    String neighborhoods = "";
                    for (int i = 0; i < selectedNeighborhoods.length; i++) {
                        neighborhoods += selectedNeighborhoods[i];
                        if (i + 1 != selectedNeighborhoods.length) {
                            neighborhoods += ", ";
                        }
                    }
                    location_et.setText(neighborhoods);
                } else {
                    location_et.setText(zone);
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.multiChoiceItems(App.getNeighborhoods(zone), 0)
                .title("Escolha o(s) bairro(s)")
                .positiveAction("OK")
                .negativeAction("Cancelar");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.date_et)
    public void dateEt() {
        Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker_Light) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                date_et.setText(dialog.getFormattedDate(new SimpleDateFormat("dd/MM/yyyy", Locale.US)));
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

    @OnClick(R.id.fab)
    public void fab() {
        ((MainAct) getActivity()).showRideOfferFrag();
    }

    @OnClick(R.id.anotherSearch_bt)
    public void anotherSearchBt() {
        App.expandOrCollapse(lay, true);
        anotherSearch_bt.setVisibility(View.GONE);
    }

    @OnClick(R.id.search_bt)
    public void searchBt() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Aguarde", true, true);

        String location = location_et.getText().toString();
        if (location.isEmpty()) {
            App.toast("Escolha um local");
            pd.dismiss();
            return;
        }
        String date = date_et.getText().toString();
        if (date.isEmpty()) {
            date_et.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date()));
            date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
        }
        String time = time_et.getText().toString();
        String center = center_et.getText().toString();
        boolean go = radioGroup.getCheckedRadioButtonId() == R.id.go_rb;
        RideSearchFiltersForJson rideSearchFilters = new RideSearchFiltersForJson(location, date, time, center, go);

        String lastRideSearchFilters = new Gson().toJson(rideSearchFilters);
        App.putPref(App.LAST_RIDE_SEARCH_FILTERS_PREF_KEY, lastRideSearchFilters);

        App.getNetworkService().getRideOffers(rideSearchFilters, new Callback<List<RideOfferForJson>>() {
            @Override
            public void success(List<RideOfferForJson> rideOffers, Response response) {
                if (rideOffers != null) {
                    App.expandOrCollapse(lay, false);
                    anotherSearch_bt.setVisibility(View.VISIBLE);
                    Collections.sort(rideOffers, new RideOfferComparatorByTime());
                    adapter.makeList(rideOffers);
                } else {
                    App.toast("Nenhuma carona encontrada");
                    adapter.makeList(new ArrayList<RideOfferForJson>());
                }
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                App.toast("Erro ao obter caronas");
                Log.e("getRideOffers", error.getMessage());
            }
        });
    }
}
