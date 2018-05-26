package br.ufrj.caronae.acts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.asyncs.LogOut;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeAct extends AppCompatActivity {
    @BindView(R.id.welcome_title)
    TextView welcometv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        if(!App.isUserLoggedIn())
        {
            startActivity(new Intent(WelcomeAct.this, LoginAct.class));
            WelcomeAct.this.finish();
        }
        else
        {
            String[] namePart = App.getUser().getName().split(" ");
            String welcometxt = "Olá, "+ namePart[0] +"!";
            welcometv.setText(welcometxt);
        }
    }

    @OnClick(R.id.continue_bt)
    public void goToProfile()
    {
        Intent profileAct = new Intent(this, MyProfileAct.class);
        profileAct.putExtra("firstLogin", true);
        startActivity(profileAct);
        overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @Override
    public void onBackPressed()
    {
        new LogOut(this).execute();
        startActivity(new Intent(this, LoginAct.class));
        finish();
        SharedPref.NAV_INDICATOR = "AllRides";
    }
}
