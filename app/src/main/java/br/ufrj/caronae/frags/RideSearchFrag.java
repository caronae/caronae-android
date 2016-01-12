package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.google.gson.Gson;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByTime;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideSearchFiltersForJson;
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

        adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getActivity());
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        String lastRideSearchFilters = SharedPref.getLastRideSearchFiltersPref();
        if (!lastRideSearchFilters.equals(SharedPref.MISSING_PREF)) {
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
                String selectedZone = getSelectedValue().toString();
                locationEt2(selectedZone);
                location_et.setText(selectedZone);
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(Util.getZones(), 0)
                .title(getContext().getString(R.string.frag_rideSearch_pickZones))
                .positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));
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
                        if (selectedNeighborhoods[i].equals("Todos")) {
                            super.onPositiveActionClicked(fragment);
                            return;
                        }
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

        @SuppressWarnings("ConstantConditions")
        ArrayList<String> neighborhoods = new ArrayList<>(Arrays.asList(Util.getNeighborhoods(zone)));
        neighborhoods.add(0, "Todos");
        String[] neighborhoodsArray = new String[neighborhoods.size()];
        neighborhoods.toArray(neighborhoodsArray);
        builder.multiChoiceItems(neighborhoodsArray, 0)
                .title(getContext().getString(R.string.frag_rideSearch_pickNeighborhood))
                .positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));
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

        builder.positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.time_et)
    public void timeEt() {
        Dialog.Builder builder = new TimePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light, 24, 0) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                time_et.setText(dialog.getFormattedTime(new SimpleDateFormat("HH:mm", Locale.US)));
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.center_et)
    public void centerEt() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                center_et.setText(getSelectedValue());
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(Util.getCenters(), 0)
                .title(getContext().getString(R.string.frag_rideSearch_hintPickCenter))
                .positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.fab)
    public void fab() {
        ((MainAct) getActivity()).showRideOfferFrag();
    }

    @OnClick(R.id.anotherSearch_bt)
    public void anotherSearchBt() {
        Util.expandOrCollapse(lay, true);
        anotherSearch_bt.setVisibility(View.GONE);
    }

    @OnClick(R.id.search_bt)
    public void searchBt() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", getContext().getString(R.string.wait), true, true);

        String location = location_et.getText().toString();
        if (location.isEmpty()) {
            //noinspection ConstantConditions
            location_et.setText(Util.getNeighborhoods(Util.getZones()[0])[0]);
            location = location_et.getText().toString();
        }
        String date = date_et.getText().toString();
        if (date.isEmpty()) {
            date_et.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date()));
            date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
        }
        String time = time_et.getText().toString();
        if (time.isEmpty()) {
            time_et.setText(new SimpleDateFormat("HH:mm", Locale.US).format(new Date()));
            time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        }
        String center = center_et.getText().toString();
        boolean go = radioGroup.getCheckedRadioButtonId() == R.id.go_rb;
        RideSearchFiltersForJson rideSearchFilters = new RideSearchFiltersForJson(location, date, time, center, go);

        String lastRideSearchFilters = new Gson().toJson(rideSearchFilters);
        SharedPref.saveLastRideSearchFiltersPref(lastRideSearchFilters);

        App.getNetworkService().listFiltered(rideSearchFilters, new Callback<List<RideForJson>>() {
            @Override
            public void success(List<RideForJson> rideOffers, Response response) {
                if (rideOffers != null && !rideOffers.isEmpty()) {
                    Util.expandOrCollapse(lay, false);
                    anotherSearch_bt.setVisibility(View.VISIBLE);
                    Collections.sort(rideOffers, new RideOfferComparatorByTime());
                    for (RideForJson rideOffer : rideOffers) {
                        rideOffer.setDbId(rideOffer.getId().intValue());
                    }
                    adapter.makeList(rideOffers);
                } else {
                    Util.toast(R.string.frag_rideSearch_noRideFound);
                    adapter.makeList(new ArrayList<RideForJson>());
                }
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                Util.toast(R.string.frag_rideSearch_errorListFiltered);
                Log.e("listFiltered", error.getMessage());
            }
        });
    }
}
