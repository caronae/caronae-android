package br.ufrj.caronae.frags;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.models.modelsforjson.RideFiltersForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RideFilterFrag extends Fragment {

    @BindView(R.id.location_et)
    EditText location_et;
    @BindView(R.id.center_et)
    EditText center_et;
    @BindView(R.id.search_bt)
    Button search_bt;

    private String resumeLocation = "";
    private String neighborhoods = "";
    private String location = "";
    private String center = "";
    private String campi = "";
    private String zone = "";


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
    public void search() {
        Fragment fragment;
        Class fragmentClass;
        RideFiltersForJson rideFilters;
        String lastRideFilters;
        if(center_et.getText().toString().isEmpty() && location_et.getText().toString().isEmpty())
        {
            fragment = null;
            fragmentClass = AllRidesFrag.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
            transaction.replace(R.id.flContent, fragment).commit();
        }
        else
        {
            if (center.equals("Cidade Universitária")) {
                center = "";
            }
            if (campi.equals(Util.getCampi()[0]) || center_et.getText().toString().isEmpty()) {
                campi = "Todos os Campi";
            }
            rideFilters = new RideFiltersForJson(location_et.getText().toString(), center, campi, zone, resumeLocation);
            lastRideFilters = new Gson().toJson(rideFilters);
            SharedPref.saveLastFiltersPref(lastRideFilters);
            SharedPref.saveFilterPref(lastRideFilters);
            MainAct act = (MainAct) getActivity();
            fragment = null;
            fragmentClass = AllRidesFrag.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
            transaction.replace(R.id.flContent, fragment).commit();
            act.updateFilterCard(getContext(), lastRideFilters);
            act.startFilterCard();
        }
    }

    @Override
    public void onStart()
    {
        Util.debug(SharedPref.LOCATION_INFO);
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        super.onStart();
    }

    @Override
    public void onResume()
    {
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        super.onResume();
    }

    @OnClick(R.id.location_et)
    public void locationEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Filtrar");
        intent.putExtra("allP", true);
        intent.putExtra("otherP", true);
        intent.putExtra("getBack", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.center_et)
    public void centerEt() {

    }

}
