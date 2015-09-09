package br.ufrj.caronae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class InicioAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //new Delete().from(Usuario.class).execute();

        if (App.inst().isUsuarioLogado())
            startActivity(new Intent(this, PrincipalAct.class));
        else
            startActivity(new Intent(this, LoginAct.class));

        finish();
    }
}
