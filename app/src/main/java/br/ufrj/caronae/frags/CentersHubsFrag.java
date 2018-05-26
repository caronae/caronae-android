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
import br.ufrj.caronae.models.Campi;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CentersHubsFrag extends Fragment {

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;

    CustomPlaceBar[] cPB;

    String campiName;

    public CentersHubsFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, view);
        campiName = getArguments().getString("campi");
        SharedPref.CAMPI_INFO = campiName;
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
        String selection = placeAct.fragType;
        List<Campi> campi = SharedPref.getPlace().getCampi();
        Campi selectedCampi = null;
        if(campi != null && campi.size() != 0)
        {
            for(int i = 0; i < campi.size(); i++)
            {
                if(campi.get(i).getName().equals(campiName))
                {
                  selectedCampi = campi.get(i);
                }
            }
            if(selectedCampi != null) {
                Fragment fragment = (Fragment) this;
                if(selection.equals("center")) {
                    cPB = new CustomPlaceBar[selectedCampi.getCenters().size()];
                    for (int i = 0; i < selectedCampi.getCenters().size(); i++) {
                        cPB[i] = new CustomPlaceBar(activity, getContext(), fragment, true, selectedCampi.getCenters().get(i), selectedCampi.getColor(), "center", selectable);
                        mainLayout.addView(cPB[i]);
                    }
                }
                else
                {
                    cPB = new CustomPlaceBar[selectedCampi.getHubs().size()];
                    for (int i = 0; i < selectedCampi.getHubs().size(); i++) {
                        cPB[i] = new CustomPlaceBar(activity, getContext(), fragment, true, selectedCampi.getHubs().get(i), selectedCampi.getColor(), "center", selectable);
                        mainLayout.addView(cPB[i]);
                    }
                }
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
            SharedPref.CAMPI_INFO = campiName;
        }
        else
        {
            selectedOptions = selectedOptions.substring(0, selectedOptions.length()-2);
            SharedPref.CAMPI_INFO = selectedOptions.substring(0, selectedOptions.length());
        }
        return isChecked;
    }
}
