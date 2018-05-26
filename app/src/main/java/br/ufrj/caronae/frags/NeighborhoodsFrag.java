package br.ufrj.caronae.frags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import br.ufrj.caronae.customizedviews.CustomPlaceBar;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.models.Zone;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NeighborhoodsFrag extends Fragment {

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;

    CustomPlaceBar[] cPB;

    String zoneName;

    public NeighborhoodsFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, view);
        zoneName = getArguments().getString("zone");
        SharedPref.LOCATION_INFO = zoneName;
        Activity activity = getActivity();
        PlaceAct placeAct = (PlaceAct)activity;
        placeAct.setOtherVisibility(View.GONE);
        boolean selectable = placeAct.selectable;
        if(selectable)
        {
            placeAct.setFinishButtonVisibility(View.VISIBLE);
        }
        else
        {
            placeAct.setFinishButtonVisibility(View.GONE);
        }
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
                cPB = new CustomPlaceBar[selectedZone.getNeighborhoods().size()];
                Fragment fragment = (Fragment) this;
                for (int i = 0; i < selectedZone.getNeighborhoods().size(); i++) {
                    cPB[i] = new CustomPlaceBar(activity, getContext(), fragment, true, selectedZone.getNeighborhoods().get(i), selectedZone.getColor(), "neighborhood", selectable);
                    mainLayout.addView(cPB[i]);
                }
            }
            else if(zoneName.equals("Outros"))
            {
                placeAct.setOtherVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    public int optionsSelected()
    {
        int isChecked = 0;
        String selectedOptions = "";
        for(int i = 0; i < cPB.length; i++)
        {
            if(cPB[i].isChecked())
            {
                isChecked++;
                selectedOptions = selectedOptions.concat(cPB[i].getText() + ", ");
            }
        }
        if(isChecked == cPB.length || isChecked == 0)
        {
            SharedPref.LOCATION_INFO = zoneName;
        }
        else
        {
            selectedOptions = selectedOptions.substring(0, selectedOptions.length()-2);
            SharedPref.LOCATION_INFO = selectedOptions.substring(0, selectedOptions.length());
        }
        return isChecked;
    }
}
