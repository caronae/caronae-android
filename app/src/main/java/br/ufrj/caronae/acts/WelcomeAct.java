package br.ufrj.caronae.acts;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeAct extends AppCompatActivity {

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
    }

    @OnClick(R.id.continue_bt)
    public void goToProfile()
    {
        Intent profileAct = new Intent(this, MyProfileAct.class);
        profileAct.putExtra("firstLogin", true);
        startActivity(profileAct);
        overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }
}
