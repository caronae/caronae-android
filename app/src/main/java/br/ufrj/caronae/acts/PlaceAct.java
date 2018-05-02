package br.ufrj.caronae.acts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Activity a = this;
        back_bt = (RelativeLayout)findViewById(R.id.back_bt);
        back_tv = (TextView) findViewById(R.id.activity_back);
        title_tv = (TextView) findViewById(R.id.header_text);
        progressBar = (RelativeLayout) findViewById(R.id.progress_bar);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFrag(back_tv.getText().toString());
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
                                    changeToZone();
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
            changeToZone();
        }
    }

    public void changeToZone()
    {
        Fragment fragment;
        fragment = new ZonesFrag();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    public void changeFrag(String title)
    {
        switch (title)
        {
            case "Editar Perfil":
                hideKeyboard();
                Intent myProfileAct = new Intent(this, MyProfileAct.class);
                startActivity(myProfileAct);
                overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                break;
            case "Zona":
                hideKeyboard();
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
                setBackText("Editar Perfil");
                setTitle("Zona");
                break;
            default:
                break;
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

    public void actReturn()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
    }
}
