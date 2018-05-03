package br.ufrj.caronae;

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

import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.frags.ZonesFrag;


public class CustomPlaceBar extends LinearLayout {

    private ImageView bar_iv;
    private TextView bar_tv;

    public CustomPlaceBar(Activity activity, Context context, Fragment frag, boolean secondPlace, String text, String color, String code)
    {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.custom_placebar, this);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        RelativeLayout arrow = (RelativeLayout) findViewById(R.id.lay_3);
        bar_iv = (ImageView) findViewById(R.id.bar_iv);
        bar_tv = (TextView) findViewById(R.id.bar_tv);
        setText(text);
        setBarColor(color);
        setTextColor(color);
        if(secondPlace) {
            arrow.setVisibility(View.INVISIBLE);
        }
        PlaceAct act = (PlaceAct) activity;
        mainLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code.equals("zone")) {
                    ZonesFrag fragment = (ZonesFrag) frag;
                    if (bar_tv.getText().toString().equals("Outra")) {
                        act.setTitle(bar_tv.getText().toString() + " região");
                        fragment.changeToNeighborhoods(bar_tv.getText().toString());
                    }
                    else if(bar_tv.getText().toString().equals("Todos os Bairros")) {
                        SharedPref.LOCATION_INFO = "Todos os Bairros";
                        act.finish();
                        act.overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    }
                    else {
                        act.setTitle(bar_tv.getText().toString());
                        fragment.changeToNeighborhoods(bar_tv.getText().toString());
                    }
                }
                else if(code.equals("neighborhood"))
                {
                    SharedPref.LOCATION_INFO = bar_tv.getText().toString();
                    act.finish();
                    act.overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                }
                else if(code.equals("willback"))
                {
                    SharedPref.LOCATION_INFO = "Outros";
                    act.finish();
                    act.overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
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
}
