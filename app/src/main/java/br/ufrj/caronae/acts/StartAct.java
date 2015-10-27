package br.ufrj.caronae.acts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.ufrj.caronae.App;

public class StartAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (App.isUserLoggedIn())
            startActivity(new Intent(this, MainAct.class));
        else
            startActivity(new Intent(this, OpeningAct.class));

        finish();
    }
}
