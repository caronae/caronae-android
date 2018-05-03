package br.ufrj.caronae.acts;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.ufrj.caronae.CustomDialogClass;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.frags.ZonesFrag;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceAct extends AppCompatActivity {

    RelativeLayout back_bt;
    TextView back_tv, title_tv;
    RelativeLayout progressBar, ok_bt, othersLay;
    EditText otherOption;
    String backText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Activity a = this;
        back_bt = (RelativeLayout)findViewById(R.id.back_bt);
        back_tv = (TextView) findViewById(R.id.activity_back);
        title_tv = (TextView) findViewById(R.id.header_text);
        othersLay = (RelativeLayout) findViewById(R.id.other_lay);
        ok_bt = (RelativeLayout) findViewById(R.id.activity_ok);
        progressBar = (RelativeLayout) findViewById(R.id.progress_bar);
        otherOption = (EditText) findViewById(R.id.others);
        try {
            backText = getIntent().getExtras().getString("backText");
        }catch (Exception e){}
        back_tv.setText(backText);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFrag(back_tv.getText().toString());
            }
        });
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!otherOption.getText().toString().isEmpty()) {
                    SharedPref.LOCATION_INFO = otherOption.getText().toString();
                    finish();
                }
            }
        });
        if(!SharedPref.checkExistence(SharedPref.PLACE_KEY))
        {
            if(Util.isNetworkAvailable(getBaseContext())) {
                CaronaeAPI.service(getApplicationContext()).getPlaces()
                        .enqueue(new Callback<PlacesForJson>() {
                            @Override
                            public void onResponse(Call<PlacesForJson> call, Response<PlacesForJson> response) {
                                if (response.isSuccessful()) {
                                    SharedPref.setPlace(response.body());
                                    progressBar.setVisibility(View.GONE);
                                    goToZone();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    CustomDialogClass cdc = new CustomDialogClass(a, "PlaceAct", null);
                                    cdc.show();
                                    cdc.setTitleText("Não foi possível carregar as localidades");
                                    cdc.setMessageText("Por favor, tente novamente. Esgotou-se o tempo limite da solicitação...");
                                    cdc.setPButtonText("OK");
                                    cdc.enableOnePositiveOption();
                                }
                            }

                            @Override
                            public void onFailure(Call<PlacesForJson> call, Throwable t) {
                                progressBar.setVisibility(View.GONE);
                                CustomDialogClass cdc = new CustomDialogClass(a, "PlaceAct", null);
                                cdc.show();
                                cdc.setTitleText("Não foi possível carregar as localidades");
                                cdc.setMessageText("Por favor, tente novamente. Esgotou-se o tempo limite da solicitação...");
                                cdc.setPButtonText("OK");
                                cdc.enableOnePositiveOption();
                            }
                        });
            }
            else
            {
                progressBar.setVisibility(View.GONE);
                CustomDialogClass cdc = new CustomDialogClass(a, "PlaceAct", null);
                cdc.show();
                cdc.setTitleText("Não foi possível carregar as localidades");
                cdc.setMessageText("Por favor, tente novamente. Sem conexão com a internet...");
                cdc.setPButtonText("OK");
                cdc.enableOnePositiveOption();
            }
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            goToZone();
        }
    }

    public void goToZone()
    {
        Fragment fragment;
        fragment = new ZonesFrag();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    public void backToZone()
    {
        Fragment fragment = null;
        FragmentManager fragmentManager;
        Class fragmentClass;
        fragmentClass = ZonesFrag.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack();
        transaction.setCustomAnimations(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
        setBackText(backText);
        setTitle("Zona");
    }

    public void changeFrag(String title)
    {
        if(title.equals(backText))
        {
            hideKeyboard();
            finish();
            overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
        }
        else if(title.equals("Zona")) {
            hideKeyboard();
            backToZone();
        }
    }

    public void setTitle(String text)
    {
        title_tv.setText(text);
    }

    public void setBackText(String text)
    {
        back_tv.setText(text);
    }

    public void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed()
    {
        if(back_tv.getText().toString().equals(backText))
        {
            hideKeyboard();
            finish();
        }
        else if(back_tv.getText().toString().equals("Zona"))
        {
            hideKeyboard();
            backToZone();
        }
        overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
    }

    public void setOtherVisibility(int v)
    {
        othersLay.setVisibility(v);
        otherOption.setVisibility(v);
        otherOption.requestFocus();
        ok_bt.setVisibility(v);
    }
}
