package br.ufrj.caronae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginAct extends AppCompatActivity {

    @Bind(R.id.token_et)
    EditText token_et;

    @Bind(R.id.enviar_bt)
    Button enviar_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.enviar_bt)
    public void enviarBt() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://private-5b9ed6-caronae.apiary-mock.com")
                .build();
        ApiaryService service = restAdapter.create(ApiaryService.class);

        service.enviarToken(token_et.getText().toString(), new Callback<Usuario>() {
            @Override
            public void success(Usuario usuario, Response response) {
                usuario.save();
                chamarPrincipalAct();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(App.LOGTAG, retrofitError.getMessage());
            }
        });
    }

    public void chamarPrincipalAct() {
        startActivity(new Intent(this, PrincipalAct.class));
        finish();
    }
}
