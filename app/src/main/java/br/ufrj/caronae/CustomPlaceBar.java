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

    private Activity activity;
    private LinearLayout mainLayout;
    private RelativeLayout arrow;
    private ImageView bar_iv;
    private TextView bar_tv;
    private Fragment frag;
    private boolean secondPlace;

    public CustomPlaceBar(Activity activity, Context context, Fragment frag, boolean secondPlace, String text, String color)
    {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.custom_placebar, this);
        this.frag = frag;
        this.secondPlace = secondPlace;
        this.activity = activity;
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        arrow = (RelativeLayout) findViewById(R.id.lay_3);
        bar_iv = (ImageView) findViewById(R.id.bar_iv);
        bar_tv = (TextView) findViewById(R.id.bar_tv);
        setText(text);
        setBarColor(color);
        setTextColor(color);
        if(secondPlace) {
            arrow.setVisibility(View.INVISIBLE);
        }
        else
        {
            mainLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZonesFrag fragment = (ZonesFrag) frag;
                    PlaceAct act = (PlaceAct)activity;
                    if(!bar_tv.getText().toString().equals("Outra")) {
                        act.setTitle(bar_tv.getText().toString());
                    }
                    else
                    {
                        act.setTitle(bar_tv.getText().toString() + " região");
                    }
                        fragment.changeToNeighborhoods(bar_tv.getText().toString());
                }
            });
        }
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
