package br.ufrj.caronae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StartAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //new Delete().from(User.class).execute();

        if (App.inst().isUserLoggedIn())
            startActivity(new Intent(this, MainAct.class));
        else
            startActivity(new Intent(this, LoginAct.class));

        finish();
    }
}
