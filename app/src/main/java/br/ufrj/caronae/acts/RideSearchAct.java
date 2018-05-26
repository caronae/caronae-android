package br.ufrj.caronae.acts;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.frags.RideSearchFrag;
import br.ufrj.caronae.frags.SearchRidesListFrag;

public class RideSearchAct extends AppCompatActivity {

    RelativeLayout backV, cancelV;
    ImageView searchBt;
    TextView titleTv;
    FrameLayout content;

    public String isGoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_search);
        backV = findViewById(R.id.back_bt);
        cancelV = findViewById(R.id.cancel_bt);
        searchBt = findViewById(R.id.search_bt);
        titleTv = findViewById(R.id.header_text);
        content = findViewById(R.id.flContent);
        isGoing = getIntent().getExtras().getString("isGoing");
        setBackBt();
        setSearchIv();
        changeToList(false);
    }

    private void setBackBt()
    {
        Activity act = this;
        backV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.NAV_INDICATOR = "AllRides";
                Intent mainAct = new Intent(act, MainAct.class);
                startActivity(mainAct);
                overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
            }
        });
        cancelV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToList(true);
            }
        });
    }

    private void setSearchIv()
    {
        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBt.setVisibility(View.GONE);
                backV.setVisibility(View.GONE);
                cancelV.setVisibility(View.VISIBLE);
                titleTv.setText("Buscar carona");
                Fragment fragment = null;
                Class fragmentClass;
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromSearch", true);
                fragmentClass = RideSearchFrag.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_down_slide_in, R.anim.anim_up_slide_out);
                transaction.replace(R.id.flContent, fragment).commit();
            }
        });
    }

    public void changeToList(boolean upToDown)
    {
        searchBt.setVisibility(View.VISIBLE);
        cancelV.setVisibility(View.GONE);
        backV.setVisibility(View.VISIBLE);
        titleTv.setText("Pesquisa");
        Fragment fragment = null;
        Class fragmentClass;
        Bundle bundle = new Bundle();
        bundle.putString("isGoing", isGoing);
        fragmentClass = SearchRidesListFrag.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(upToDown) {
            transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
        }
        transaction.replace(R.id.flContent, fragment).commit();
    }
}
