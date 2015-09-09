package br.ufrj.caronae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class InicioAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Usuario usuario = new Usuario();
        usuario.setNome("Leonardo");
        usuario.setPerfil("Aluno");
        usuario.setCurso("Bacharelado em Ciência da Computação");
        usuario.setUnidade("CCMN");
        usuario.setZona("Norte");
        usuario.setBairro("Jardim Guanabara");
        usuario.save();

        if (App.inst().isUsuarioLogado())
            startActivity(new Intent(this, PrincipalAct.class));
        else
            startActivity(new Intent(this, LoginAct.class));

        finish();
    }
}
