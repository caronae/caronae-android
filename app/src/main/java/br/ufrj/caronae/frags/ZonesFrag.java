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
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.ufrj.caronae.CustomPlaceBar;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.models.Zone;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ZonesFrag extends Fragment {

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;
    @BindView(R.id.loading_tv)
    TextView loading_tv;
    Activity activity;

    public ZonesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, view);
        activity = getActivity();
        List<Zone> zones = Util.zones;
        if(zones != null && zones.size() != 0) {
            Collections.sort(zones, new Comparator<Zone>() {
                public int compare(Zone z1, Zone z2) {
                    return z1.getName().compareTo(z2.getName());
                }
            });
            CustomPlaceBar cPB;
            Fragment fragment = (Fragment) this;
            for (int i = 0; i < zones.size(); i++) {
                cPB = new CustomPlaceBar(activity, getContext(), fragment, false, zones.get(i).getName(), zones.get(i).getColor());
                mainLayout.addView(cPB);
            }
            cPB = new CustomPlaceBar(activity, getContext(), fragment, false, "Outra","#919191" );
            mainLayout.addView(cPB);
            loading_tv.setVisibility(View.GONE);
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
