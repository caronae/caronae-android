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

import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.PlaceAct;
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

    public RideFilterFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ride_filter, container, false);
        ButterKnife.bind(this, view);
        loadLastFilters();
        return view;
    }

    private void loadLastFilters() {
        String l, c;
        c = !SharedPref.getCenterFilter().equals(SharedPref.MISSING_PREF) ? SharedPref.getCenterFilter() : "Todos os Campi";
        l = !SharedPref.getLocationFilter().equals(SharedPref.MISSING_PREF) ? SharedPref.getLocationFilter() : "Todos os Bairros";
        center_et.setText(c);
        location_et.setText(l);
    }

    @OnClick(R.id.search_bt)
    public void search() {
        String location, center;
        Fragment fragment;
        Class fragmentClass;
        center = center_et.getText().toString();
        location = location_et.getText().toString();
        SharedPref.setCenterFilter(center);
        SharedPref.setLocationFilter(location);
        if(center_et.getText().toString().isEmpty() && location_et.getText().toString().isEmpty() || center_et.getText().toString().equals("Todos os Campi") && location_et.getText().toString().equals("Todos os Bairros"))
        {
            SharedPref.NAV_INDICATOR = "AllRides";
            SharedPref.setFilterPref(false);
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
            SharedPref.NAV_INDICATOR = "AllRides";
            ((MainAct) getActivity()).verifyItem();
        }
        else
        {
            MainAct act = (MainAct) getActivity();
            SharedPref.setFilterPref(true);
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
            act.updateFilterCard(getContext());
            act.startFilterCard();
            SharedPref.NAV_INDICATOR = "AllRides";
            act.verifyItem();
        }
        SharedPref.lastAllRidesUpdate = null;
    }

    @Override
    public void onStart()
    {
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        if(!SharedPref.CAMPI_INFO.isEmpty() && !SharedPref.CAMPI_INFO.equals(""))
        {
            center_et.setText(SharedPref.CAMPI_INFO);
            SharedPref.CAMPI_INFO = "";
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
        if(!SharedPref.CAMPI_INFO.isEmpty() && !SharedPref.CAMPI_INFO.equals(""))
        {
            center_et.setText(SharedPref.CAMPI_INFO);
            SharedPref.CAMPI_INFO = "";
        }
        super.onResume();
    }

    @OnClick(R.id.location_et)
    public void locationEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Filtrar");
        intent.putExtra("selection", "neigh");
        intent.putExtra("allP", true);
        intent.putExtra("otherP", true);
        intent.putExtra("getBack", true);
        intent.putExtra("selectable", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.center_et)
    public void centerEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Filtrar");
        intent.putExtra("allP", true);
        intent.putExtra("selection", "center");
        intent.putExtra("otherP", false);
        intent.putExtra("getBack", false);
        intent.putExtra("selectable", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }
}
