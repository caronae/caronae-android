package br.ufrj.caronae;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.query.Select;

public class MainActivity extends AppCompatActivity implements PerfilFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Usuario usuario = new Usuario();
        usuario.nome = "Leonardo";
        usuario.perfil = "Aluno";
        usuario.curso = "Bacharelado em Ciência da Computação";
        usuario.unidade = "CCMN";
        usuario.zona = "Norte";
        usuario.bairro = "Jardim Guanabara";
        usuario.save();

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
