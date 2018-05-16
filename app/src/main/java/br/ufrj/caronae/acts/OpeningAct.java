package br.ufrj.caronae.acts;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;

public class OpeningAct extends AppCompatActivity {

    public static final int SPLASH_SCREEN_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_opening);
        } else {
            setContentView(R.layout.activity_opening);

            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        if(App.isUserLoggedIn())
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(OpeningAct.this, MainAct.class));
                    OpeningAct.this.finish();
                }
            }, SPLASH_SCREEN_DURATION);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(OpeningAct.this, LoginAct.class));
                    OpeningAct.this.finish();
                }
            }, SPLASH_SCREEN_DURATION);
        }
    }
}
