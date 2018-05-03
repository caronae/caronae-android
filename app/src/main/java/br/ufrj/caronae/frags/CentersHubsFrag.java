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
import br.ufrj.caronae.models.Campi;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CentersHubsFrag extends Fragment {

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;

    public CentersHubsFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, view);
        String campiName = getArguments().getString("campi");
        Activity activity = getActivity();
        PlaceAct placeAct = (PlaceAct)activity;
        placeAct.setOtherVisibility(View.GONE);
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
                CustomPlaceBar cPB;
                Fragment fragment = (Fragment) this;
                if(selection.equals("center")) {
                    for (int i = 0; i < selectedCampi.getCenters().size(); i++) {
                        cPB = new CustomPlaceBar(activity, getContext(), fragment, true, selectedCampi.getCenters().get(i), selectedCampi.getColor(), "center");
                        mainLayout.addView(cPB);
                    }
                }
                else
                {
                    for (int i = 0; i < selectedCampi.getHubs().size(); i++) {
                        cPB = new CustomPlaceBar(activity, getContext(), fragment, true, selectedCampi.getHubs().get(i), selectedCampi.getColor(), "center");
                        mainLayout.addView(cPB);
                    }
                }
            }
        }
        return view;
    }


}
