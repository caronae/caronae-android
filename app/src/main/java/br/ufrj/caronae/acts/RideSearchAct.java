package br.ufrj.caronae.acts;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.frags.RideSearchFrag;
import br.ufrj.caronae.frags.SearchRidesListFrag;

public class RideSearchAct extends AppCompatActivity {

    RelativeLayout backV;
    ImageView searchBt;

    String isGoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_search);
        backV = findViewById(R.id.back_bt);
        searchBt = findViewById(R.id.search_bt);
        isGoing = getIntent().getExtras().getString("isGoing");
        setBackBt();
        setSearchIv();
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
        transaction.replace(R.id.flContent, fragment).commit();
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
    }

    private void setSearchIv()
    {
        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                Class fragmentClass;
                Bundle bundle = new Bundle();
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
}
