package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignUpAct extends AppCompatActivity {

    @Bind(R.id.nome_et)
    public EditText nome_et;
    @Bind(R.id.token_et)
    public EditText token_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button)
    public void button() {
        App.getNetworkService().signUp(nome_et.getText().toString(), token_et.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                App.toast(App.getResponseBody(s));
            }

            @Override
            public void failure(RetrofitError error) {
                App.toast("Erro ao cadastrar");
                Log.e("signUp", error.getMessage());
            }
        });
    }

}
