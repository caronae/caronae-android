package br.ufrj.caronae.frags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.ufrj.caronae.customizedviews.CustomPlaceBar;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.models.Campi;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CampiFrag extends Fragment {

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;

    Activity activity;

    public CampiFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, view);
        activity = getActivity();
        PlacesForJson places = SharedPref.getPlace();
        List<Campi> campi = places.getCampi();
        ((PlaceAct)activity).setFinishButtonVisibility(View.GONE);
        String selection = ((PlaceAct) activity).fragType;
        boolean enableAll = ((PlaceAct)activity).enableAll;
        if(campi != null && campi.size() != 0)
        {
            Collections.sort(campi, new Comparator<Campi>() {
                public int compare(Campi c1, Campi c2) {
                    return c1.getName().compareTo(c2.getName());
                }
            });
            CustomPlaceBar cPB;
            Fragment fragment = (Fragment) this;
            if(enableAll)
            {
                cPB = new CustomPlaceBar(activity, getContext(), fragment, false, "Todos os Campi", "#606060", "center", false);
                mainLayout.addView(cPB);
            }
            for (int i = 0; i < campi.size(); i++) {
                cPB = new CustomPlaceBar(activity, getContext(), fragment, false, campi.get(i).getName(), campi.get(i).getColor(), selection, false);
                mainLayout.addView(cPB);
            }
        }
        return view;
    }

    public void changeToCentersHubs(String campi)
    {
        PlaceAct act = (PlaceAct)activity;
        act.setBackText("");
        act.hideKeyboard();
        act.setTitle(campi);
        Fragment fragment = null;
        FragmentManager fragmentManager;
        Class fragmentClass;
        Bundle bundle = new Bundle();
        bundle.putString("campi", campi);
        fragmentClass = CentersHubsFrag.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragment.setArguments(bundle);
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack();
        transaction.setCustomAnimations(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
    }
}
