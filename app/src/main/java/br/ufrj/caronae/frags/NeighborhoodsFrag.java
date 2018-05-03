package br.ufrj.caronae.frags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import br.ufrj.caronae.CustomPlaceBar;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.models.Zone;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NeighborhoodsFrag extends Fragment {

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;

    public NeighborhoodsFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, view);
        String zoneName = getArguments().getString("zone");
        Activity activity = getActivity();
        PlaceAct placeAct = (PlaceAct)activity;
        placeAct.setOtherVisibility(View.GONE);
        List<Zone> zones = SharedPref.getPlace().getZones();
        Zone selectedZone = null;
        if(zones != null && zones.size() != 0)
        {
            for(int i = 0; i < zones.size(); i++)
            {
                if(zones.get(i).getName().equals(zoneName))
                {
                  selectedZone = zones.get(i);
                }
            }
            if(selectedZone != null) {
                CustomPlaceBar cPB;
                Fragment fragment = (Fragment) this;
                for (int i = 0; i < selectedZone.getNeighborhoods().size(); i++) {
                    cPB = new CustomPlaceBar(activity, getContext(), fragment, true, selectedZone.getNeighborhoods().get(i), selectedZone.getColor(), "neighborhood");
                    mainLayout.addView(cPB);
                }
            }
            else if(zoneName.equals("Outra"))
            {
                placeAct.setOtherVisibility(View.VISIBLE);
            }
        }
        return view;
    }


}
