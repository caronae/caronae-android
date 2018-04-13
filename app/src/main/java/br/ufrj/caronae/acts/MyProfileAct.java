package br.ufrj.caronae.acts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.frags.FalaeFrag;
import br.ufrj.caronae.frags.MyProfileShowFrag;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyProfileAct extends AppCompatActivity {
    private Class fragmentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        Fragment fragment = null;
        fragmentClass = MyProfileShowFrag.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        backToMenu();
    }

    @OnClick(R.id.back_bt)
    public void backTouch()
    {
        backToMenu();
    }

    private void backToMenu()
    {
        finish();
        Intent mainAct = new Intent(this, MainAct.class);
        SharedPref.NAV_INDICATOR = "Menu";
        startActivity(mainAct);
    }
}
