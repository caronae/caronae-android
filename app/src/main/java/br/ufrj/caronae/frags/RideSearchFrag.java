package br.ufrj.caronae.frags;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.squareup.otto.Subscribe;

import java.text.ParseException;
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
import br.ufrj.caronae.adapters.RideOfferAdapter;
import br.ufrj.caronae.comparators.RideOfferComparatorByDateAndTime;
import br.ufrj.caronae.frags.DialogFragment.SelectorDialogFrag;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideSearchFiltersForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    @Bind(R.id.campi_et)
    TextView campi_et;
    @Bind(R.id.lay)
    RelativeLayout lay;
    @Bind(R.id.anotherSearch_bt)
    Button anotherSearch_bt;

    @Bind(R.id.rvRides)
    RecyclerView rvRides;

    private RideOfferAdapter adapter;

    private String neighborhoods;

    public RideSearchFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_search, container, false);
        ButterKnife.bind(this, view);

        adapter = new RideOfferAdapter(new ArrayList<RideForJson>(), getActivity(), getActivity().getFragmentManager());
        rvRides.setAdapter(adapter);
        rvRides.setHasFixedSize(true);
        rvRides.setLayoutManager(new LinearLayoutManager(getActivity()));

        String lastRideSearchFilters = SharedPref.getLastRideSearchFiltersPref();
        if (!lastRideSearchFilters.equals(SharedPref.MISSING_PREF)) {
            loadLastFilters(lastRideSearchFilters);
        }

        App.getBus().register(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.getBus().unregister(this);
    }

    private void loadLastFilters(String lastRideSearchFilters) {
        RideSearchFiltersForJson rideSearchFilters = new Gson().fromJson(lastRideSearchFilters, RideSearchFiltersForJson.class);

        location_et.setText(rideSearchFilters.getLocationResumedField());
        neighborhoods = rideSearchFilters.getLocation();
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
                location_et.setText(selectedZone);
                if (selectedZone.equals("Outros")) {
                    showOtherNeighborhoodDialog();
                } else {
                    locationEt2(selectedZone);
                }
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

    public void showOtherNeighborhoodDialog() {
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                EditText neighborhood_et = (EditText) fragment.getDialog().findViewById(R.id.neighborhood_et);
                String neighborhood = neighborhood_et.getText().toString();
                if (!neighborhood.isEmpty()) {
                    location_et.setText(neighborhood);
                }

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.title(getActivity().getString(R.string.frag_ridesearch_typeNeighborhood))
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.cancel))
                .contentView(R.layout.other_neighborhood);

        DialogFragment fragment2 = DialogFragment.newInstance(builder);
        fragment2.show(getActivity().getSupportFragmentManager(), null);
    }

    public void locationEt2(final String zone) {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                CharSequence[] selectedNeighborhoods = null;
                try {
                    selectedNeighborhoods = getSelectedValues();
                } catch (Exception e) {
                    //do nothing
                }
                if (selectedNeighborhoods != null) {
                    String resumedField = "";
                    neighborhoods = "";
                    for (int i = 0; i < selectedNeighborhoods.length; i++) {
                        if (selectedNeighborhoods[i].equals(Util.getNeighborhoods("")[0])) {
                            super.onPositiveActionClicked(fragment);
                            return;
                        }
                        neighborhoods += selectedNeighborhoods[i];
                        if (i == 2) {
                            resumedField = neighborhoods + " + " + (selectedNeighborhoods.length - 3);
                        }
                        if (i + 1 != selectedNeighborhoods.length) {
                            neighborhoods += ", ";
                        }
                    }

                    if (selectedNeighborhoods.length > 3) {
                        location_et.setText(resumedField);
                    } else {
                        location_et.setText(neighborhoods);
                    }

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
        neighborhoods.add(0, Util.getNeighborhoods("")[0]);
        String[] neighborhoodsArray = new String[neighborhoods.size()];
        neighborhoods.toArray(neighborhoodsArray);
        builder.multiChoiceItems(neighborhoodsArray, -1)
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

    @OnClick(R.id.campi_et)
    public void campiEt() {
        final ArrayList<String> selectedItems = new ArrayList();
        String[] campis = Util.getCampi();
        String[] selectedCampi = campi_et.getText().toString().split(",");
        boolean[] ifCampiAreSelected = new boolean[campis.length];
        for (int campi = 0; campi < campis.length; campi++) {
            ifCampiAreSelected[campi] = false;
            for (int selected = 0; selected < selectedCampi.length; selected++) {
                if (campis[campi].equals(selectedCampi[selected])) {
                    ifCampiAreSelected[campi] = true;
                    selectedItems.add(campis[campi]);
                }
            }
        }

        showListFragment(campis, ifCampiAreSelected, "Campi");
//        AlertDialog builder = new AlertDialog.Builder(getContext())
//                .setTitle(getContext().getString(R.string.frag_rideSearch_hintPickCampi))
//                .setMultiChoiceItems(Util.getCampi(), ifCampiAreSelected, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        if (isChecked) {
//                            // If the user checked the item, add it to the selected items
//                            selectedItems.add(Util.getCampi()[which]);
//                        } else if (selectedItems.contains(Util.getCampi()[which])) {
//                            // Else, if the item is already in the array, remove it
//                            for (int item = 0; item < selectedItems.size(); item++) {
//                                if (Util.getCampi()[which].equals(selectedItems.get(item)))
//                                    selectedItems.remove(item);
//                            }
//                        }
//                    }
//                })
//                .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String campi = "";
//                        for (int selectedValues = 0; selectedValues < selectedItems.size(); selectedValues++) {
//                            if (selectedItems.get(selectedValues).equals(Util.getCampi()[0])
//                                    || selectedItems.size() == Util.getCampi().length - 1) {
//                                selectedItems.clear();
//                                selectedItems.add(Util.getCampi()[0]);
//                                campi = selectedItems.get(0) + ", ";
//                                break;
//                            }
//                            campi = campi + selectedItems.get(selectedValues) + ", ";
//                        }
//
//                        if (!campi.equals("")) {
//                            campi = campi.substring(0, campi.length() - 2);
//                        }
//                        if (campi.equals(Util.getCampi()[2]))
//                            center_et.setVisibility(View.GONE);
//                        else
//                            center_et.setVisibility(View.VISIBLE);
//                        campi_et.setText(campi);
//                    }
//                })
//                .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .create();
//
//        builder.show();
    }

    private void showListFragment(String[] campis, boolean[] ifCampiAreSelected, String title) {
        SelectorDialogFrag dialog = new SelectorDialogFrag();
        int[] colorId = new int[campis.length];
        for (int color = 0; color < colorId.length; color++){
            Log.e("COR", "campus: " + campis[color]);
            colorId[color] = Util.getColorbyCampi(campis[color]);
            Log.e("COR", "cor: " + colorId[color]);
        }
        dialog.newInstance(campis, colorId, ifCampiAreSelected);
        dialog.show(getActivity().getFragmentManager(), title);

    }

    @OnClick(R.id.center_et)
    public void centerEt() {
        final ArrayList<String> selectedItems = new ArrayList();

        String[] selectedCenters = center_et.getText().toString().split(", ");
        boolean[] ifCentersAreSelected = new boolean[Util.getFundaoCenters().length];
        for (int centers = 0; centers < Util.getFundaoCenters().length; centers++) {
            ifCentersAreSelected[centers] = false;
            for (int selecteds = 0; selecteds < selectedCenters.length; selecteds++) {
                if (Util.getFundaoCenters()[centers].equals(selectedCenters[selecteds])) {
                    ifCentersAreSelected[centers] = true;
                    selectedItems.add(Util.getFundaoCenters()[centers]);
                }
            }
        }

        AlertDialog builder = new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.frag_rideSearch_hintPickCenter))
                .setMultiChoiceItems(Util.getFundaoCenters(), ifCentersAreSelected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(Util.getFundaoCenters()[which]);
                        } else if (selectedItems.contains(Util.getFundaoCenters()[which])) {
                            // Else, if the item is already in the array, remove it
                            for (int item = 0; item < selectedItems.size(); item++) {
                                if (Util.getFundaoCenters()[which].equals(selectedItems.get(item)))
                                    selectedItems.remove(item);
                            }
                        }
                    }
                })
                .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String centers = "";
                        for (int selectedValues = 0; selectedValues < selectedItems.size(); selectedValues++) {
                            if (selectedItems.get(selectedValues).equals(Util.getFundaoCenters()[0])
                                    || selectedItems.size() == Util.getFundaoCenters().length - 1) {
                                selectedItems.clear();
                                selectedItems.add(Util.getFundaoCenters()[0]);
                                centers = selectedItems.get(0) + ", ";
                                break;
                            }
                            centers = centers + selectedItems.get(selectedValues) + ", ";
                        }

                        if (!centers.equals("")) {
                            centers = centers.substring(0, centers.length() - 2);
                        }
                        center_et.setText(centers);
                    }
                })
                .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        builder.show();
    }

    @OnClick(R.id.anotherSearch_bt)
    public void anotherSearchBt() {
        Util.expandOrCollapse(lay, true);
        anotherSearch_bt.setVisibility(View.GONE);
    }

    @OnClick(R.id.search_bt)
    public void searchBt() {

        String location = location_et.getText().toString();
        if (location.isEmpty()) {
            Util.toast(getString(R.string.frag_rideSearch_locationEmpty));
            return;
        } else {
            if (location.contains("+")) {
                location = neighborhoods;
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        String etDateString = date_et.getText().toString();
        Date todayDate = new Date();
        String todayString = simpleDateFormat.format(todayDate);
        try {
            todayDate = simpleDateFormat.parse(todayString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (etDateString.isEmpty()) {
            date_et.setText(todayString);
            etDateString = todayString;
        } else {
            try {
                Date etDate = simpleDateFormat.parse(etDateString);
                if (etDate.before(todayDate)) {
                    Util.toast(getString(R.string.frag_rideoffersearch_pastdate));
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String time = time_et.getText().toString();
        if (time.isEmpty()) {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm", Locale.US);
            String format = simpleDateFormat1.format(new Date());
            time_et.setText(format);
            time = format;
        }
        String center = center_et.getText().toString();
        if (center.equals(Util.getFundaoCenters()[0]))
            center = "";
        String campus = campi_et.getText().toString();
        if (campus.equals(Util.getCampi()[0]))
            campus = "";
        boolean go = radioGroup.getCheckedRadioButtonId() == R.id.go_rb;
        RideSearchFiltersForJson rideSearchFilters = new RideSearchFiltersForJson(location, etDateString, time, center, campus, go, location_et.getText().toString());

        String lastRideSearchFilters = new Gson().toJson(rideSearchFilters);
        SharedPref.saveLastRideSearchFiltersPref(lastRideSearchFilters);

        rideSearchFilters.setDate(Util.formatBadDateWithYear(etDateString));

        Log.e("INPUT", "location: " + location);
        Log.e("INPUT", "data: " + etDateString);
        Log.e("INPUT", "hora: " + time);
        Log.e("INPUT", "center: " + center);
        Log.e("INPUT", "campus: " + campus);
        Log.e("INPUT", "locationResumeField: " + location_et.getText().toString());


        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", getContext().getString(R.string.wait), true, true);
        App.getNetworkService(getContext()).listFiltered(rideSearchFilters)
                .enqueue(new Callback<List<RideForJson>>() {
                    @Override
                    public void onResponse(Call<List<RideForJson>> call, Response<List<RideForJson>> response) {
                        if (response.isSuccessful()) {
                            List<RideForJson> rideOffers = response.body();
                            if (rideOffers != null && !rideOffers.isEmpty()) {
                                Util.expandOrCollapse(lay, false);
                                anotherSearch_bt.setVisibility(View.VISIBLE);
                                Collections.sort(rideOffers, new RideOfferComparatorByDateAndTime());
                                for (RideForJson rideOffer : rideOffers) {
                                    rideOffer.setDbId(rideOffer.getId().intValue());
                                }
                                adapter.makeList(rideOffers);
                            } else {
                                Util.treatResponseFromServer(response);
                                Util.toast(R.string.frag_rideSearch_noRideFound);
                                adapter.makeList(new ArrayList<RideForJson>());
                            }
                            pd.dismiss();
                        } else {
                            pd.dismiss();
                            Util.toast(R.string.frag_rideSearch_errorListFiltered);
                            Log.e("listFiltered", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RideForJson>> call, Throwable t) {
                        pd.dismiss();
                        Util.toast(R.string.frag_rideSearch_errorListFiltered);
                        Log.e("listFiltered", t.getMessage());
                    }
                });

    }

    @Subscribe
    public void removeRideFromList(RideRequestSent ride) {
        adapter.remove(ride.getDbId());
        Log.i("removeRideFromList,srch", "remove called");
    }

    private List<Integer> getHeaderPositionsOnList(List<RideForJson> rides){
        List<Integer> headersPositions = new ArrayList<>();
        headersPositions.add(0);
        for (int rideIndex = 1; rideIndex < rides.size(); rideIndex++){
            if (Util.getDayFromDate(rides.get(rideIndex).getDate()) > Util.getDayFromDate(rides.get(rideIndex - 1).getDate())){
                headersPositions.add(rideIndex);
            }
        }
        return headersPositions;
    }
}
