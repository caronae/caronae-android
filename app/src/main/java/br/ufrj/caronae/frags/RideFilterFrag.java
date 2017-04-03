package br.ufrj.caronae.frags;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.StartAct;
import br.ufrj.caronae.models.modelsforjson.RideFiltersForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RideFilterFrag extends Fragment {

    @Bind(R.id.location_et)
    EditText location_et;
    @Bind(R.id.center_et)
    EditText center_et;
    @Bind(R.id.search_bt)
    Button search_bt;

    private String neighborhoods;

    private String location = "";
    private String center = "";
    private String zone = "";
    private String resumeLocation = "";


    public RideFilterFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ride_filter, container, false);
        ButterKnife.bind(this, view);

        String lastFilters = SharedPref.getRideFiltersPref();
        if (!lastFilters.equals(SharedPref.MISSING_PREF)) {
            loadLastFilters(lastFilters);
        }

        return view;
    }

    private void loadLastFilters(String lastFilters) {
        RideFiltersForJson rideFilters = new Gson().fromJson(lastFilters, RideFiltersForJson.class);

        neighborhoods = rideFilters.getLocation();
        location_et.setText(rideFilters.getResumeLocation());
        center_et.setText(rideFilters.getCenter());
    }

    @OnClick(R.id.search_bt)
    public void search(){
        if (center.equals("Todos os Centros")){
            center = "";
        }
        RideFiltersForJson rideFilters = new RideFiltersForJson(location, center, zone, resumeLocation);
        String lastRideFilters = new Gson().toJson(rideFilters);
        SharedPref.saveLastFiltersPref(lastRideFilters);
        SharedPref.saveFilterPref(lastRideFilters);
        MainAct.updateFilterCard(getContext(), lastRideFilters);
        Intent intent = new Intent(getActivity(), StartAct.class);
        startActivity(intent);
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
                    location = neighborhood;
                    resumeLocation = neighborhood;
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
        this.zone = zone;
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
                        if (selectedNeighborhoods[i].equals(Util.getNeighborhoods("")[0])
                                || selectedNeighborhoods.length == Util.getNeighborhoods(zone).length) {

                            super.onPositiveActionClicked(fragment);
                            return;
                        }
                        neighborhoods += selectedNeighborhoods[i];
//                        if (i == 2) {
//                            resumedField = neighborhoods + " + " + (selectedNeighborhoods.length - 3);
//                        }
                        if (i + 1 != selectedNeighborhoods.length) {
                            neighborhoods += ", ";
                        }
                    }

                    if (selectedNeighborhoods.length > 3) {
                        resumeLocation = selectedNeighborhoods[0] + "... " + selectedNeighborhoods[selectedNeighborhoods.length - 1];
                        location_et.setText(resumeLocation);
                    } else {
                        location_et.setText(neighborhoods);
                    }
                    location = neighborhoods;
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

    @OnClick(R.id.center_et)
    public void centerEt() {
        final ArrayList<String> selectedItems = new ArrayList();

        String[] selectedCenters = center_et.getText().toString().split(", ");
        boolean[] ifCentersAreSelected = new boolean[Util.getCenters().length];
        for (int centers = 0; centers < Util.getCenters().length; centers++) {
            ifCentersAreSelected[centers] = false;
            for (int selecteds = 0; selecteds < selectedCenters.length; selecteds++) {
                if (Util.getCenters()[centers].equals(selectedCenters[selecteds])) {
                    ifCentersAreSelected[centers] = true;
                    selectedItems.add(Util.getCenters()[centers]);
                }
            }
        }

        AlertDialog builder = new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.frag_rideSearch_hintPickCenter))
                .setMultiChoiceItems(Util.getCenters(), ifCentersAreSelected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(Util.getCenters()[which]);
                        } else if (selectedItems.contains(Util.getCenters()[which])) {
                            // Else, if the item is already in the array, remove it
                            for (int item = 0; item < selectedItems.size(); item++) {
                                if (Util.getCenters()[which].equals(selectedItems.get(item)))
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
                            if (selectedItems.get(selectedValues).equals(Util.getCenters()[0])
                                    || selectedItems.size() == Util.getCenters().length - 1) {
                                selectedItems.clear();
                                selectedItems.add(Util.getCenters()[0]);
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
}
