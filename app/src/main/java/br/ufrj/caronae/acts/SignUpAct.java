package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.User;
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
    @Bind(R.id.checkBox)
    public CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                nome_et.setHint(b ? "Identificação UFRJ" : "Nome");
            }
        });
    }

    @OnClick(R.id.button)
    public void button() {
        if (checkBox.isChecked())
            App.getNetworkService().signUpIntranet(nome_et.getText().toString(), token_et.getText().toString(), new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    Util.toast(user.getName() + " cadastrado");
                }

                @Override
                public void failure(RetrofitError error) {
                    Util.toast("Erro ao cadastrar");
                    Log.e("signUp", error.getMessage());
                }
            });
        else
            App.getNetworkService().signUp(nome_et.getText().toString(), token_et.getText().toString(), new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    Util.toast(user.getName() + " cadastrado");
                }

                @Override
                public void failure(RetrofitError error) {
                    Util.toast("Erro ao cadastrar");
                    Log.e("signUp", error.getMessage());
                }
            });
    }

}
