package br.ufrj.caronae.customizedviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.frags.CampiFrag;
import br.ufrj.caronae.frags.CentersHubsFrag;
import br.ufrj.caronae.frags.NeighborhoodsFrag;
import br.ufrj.caronae.frags.ZonesFrag;


public class CustomPlaceBar extends LinearLayout {

    private ImageView bar_iv, checked_iv;
    private TextView bar_tv;
    private boolean checked;

    public CustomPlaceBar(Activity activity, Context context, Fragment frag, boolean setArrowsInvisible, String text, String color, String code, boolean selectable)
    {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.custom_placebar, this);
        LinearLayout mainLayout = findViewById(R.id.main_layout);
        RelativeLayout arrow = findViewById(R.id.lay_3);
        bar_iv = findViewById(R.id.bar_iv);
        checked_iv = findViewById(R.id.checked);
        bar_tv = findViewById(R.id.bar_tv);

        setText(text);
        setBarColor(color);
        setTextColor(color);
        if(setArrowsInvisible)
        {
            arrow.setVisibility(View.INVISIBLE);
        }
        PlaceAct act = (PlaceAct) activity;
        mainLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectable) {
                    SharedPref.NAV_INDICATOR = "MyRides";
                    switch (code)
                    {
                        case "zone":
                            ZonesFrag fragment = (ZonesFrag) frag;
                            if (bar_tv.getText().toString().equals("Outros")) {
                                act.setTitle("Outra região");
                                fragment.changeToNeighborhoods(bar_tv.getText().toString());
                            } else if (bar_tv.getText().toString().equals("Todos os Bairros")) {
                                SharedPref.LOCATION_INFO = "Todos os Bairros";
                                act.finish();
                                act.overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                            } else {
                                act.setTitle(bar_tv.getText().toString());
                                fragment.changeToNeighborhoods(bar_tv.getText().toString());
                            }
                            break;
                        case "neighborhood":
                            SharedPref.LOCATION_INFO = bar_tv.getText().toString();
                            act.finish();
                            act.overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                            break;
                        case "willback":
                            SharedPref.LOCATION_INFO = "Outros";
                            act.finish();
                            act.overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                            break;
                        case "center":
                            if (bar_tv.getText().toString().equals("Cidade Universitária") || bar_tv.getText().toString().equals("Macaé")) {
                                ((CampiFrag) frag).changeToCentersHubs(bar_tv.getText().toString());
                            } else {
                                SharedPref.CAMPI_INFO = bar_tv.getText().toString();
                                act.finish();
                                act.overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                            }
                            break;
                        case "hub":
                            ((CampiFrag) frag).changeToCentersHubs(bar_tv.getText().toString());
                            break;
                    }
                }
                else
                {
                    if(checked){
                        checked_iv.setVisibility(View.GONE);
                        checked = false;
                    }
                    else{
                        checked_iv.setVisibility(View.VISIBLE);
                        checked = true;
                    }
                    if(code.equals("center")) {
                        act.setFinishText(((CentersHubsFrag)frag).optionsSelected());
                    }
                    else{
                        act.setFinishText(((NeighborhoodsFrag)frag).optionsSelected());
                    }
                }
            }
        });
    }

    public void setText(String text)
    {
        bar_tv.setText(text);
    }

    public void setTextColor(String color)
    {
        bar_tv.setTextColor(Color.parseColor(color));
    }

    public void setBarColor(String color)
    {
        bar_iv.setBackgroundColor(Color.parseColor(color));
    }

    public boolean isChecked()
    {
        return checked;
    }

    public String getText()
    {
        return bar_tv.getText().toString();
    }
}
