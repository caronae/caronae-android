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
import br.ufrj.caronae.models.Zone;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ZonesFrag extends Fragment {

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;

    Activity activity;

    public ZonesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, view);
        activity = getActivity();
        PlaceAct placeAct = (PlaceAct)activity;
        placeAct.setOtherVisibility(View.GONE);
        placeAct.setFinishButtonVisibility(View.GONE);
        PlacesForJson places = SharedPref.getPlace();
        List<Zone> zones = places.getZones();
        if(zones != null && zones.size() != 0)
        {
            Collections.sort(zones, new Comparator<Zone>() {
                public int compare(Zone z1, Zone z2) {
                    return z1.getName().compareTo(z2.getName());
                }
            });
            CustomPlaceBar cPB;
            Fragment fragment = (Fragment) this;
            boolean other = activity.getIntent().getExtras().getBoolean("otherP", false);
            boolean all = activity.getIntent().getExtras().getBoolean("allP", false);
            boolean backFromOthers = activity.getIntent().getExtras().getBoolean("getBack", false);
            if(all)
            {
                cPB = new CustomPlaceBar(activity, getContext(), fragment, false, "Todos os Bairros", "#606060", "zone", false);
                mainLayout.addView(cPB);
            }
            for (int i = 0; i < zones.size(); i++) {
                cPB = new CustomPlaceBar(activity, getContext(), fragment, false, zones.get(i).getName(), zones.get(i).getColor(), "zone", false);
                mainLayout.addView(cPB);
            }
            if(other) {
                if(backFromOthers) {
                    cPB = new CustomPlaceBar(activity, getContext(), fragment, false, "Outros", "#919191", "willback", false);
                }
                else
                {
                    cPB = new CustomPlaceBar(activity, getContext(), fragment, false, "Outros", "#919191", "zone", false);
                }
                mainLayout.addView(cPB);
            }
        }
        return view;
    }

    public void changeToNeighborhoods(String zone)
    {
        PlaceAct act = (PlaceAct)activity;
        act.setBackText("Zona");
        act.hideKeyboard();
        Fragment fragment = null;
        FragmentManager fragmentManager;
        Class fragmentClass;
        Bundle bundle = new Bundle();
        bundle.putString("zone", zone);
        fragmentClass = NeighborhoodsFrag.class;
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
