package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpAct extends AppCompatActivity {

    @BindView(R.id.nome_et)
    public EditText nome_et;
    @BindView(R.id.token_et)
    public EditText token_et;
    @BindView(R.id.checkBox)
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
        if (checkBox.isChecked()) {
            CaronaeAPI.service(getApplicationContext()).signUpIntranet(nome_et.getText().toString(), token_et.getText().toString())
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                User user = response.body();
                                Util.toast(user.getName() + " cadastrado");
                            } else {
                                Util.toast("Erro ao cadastrar");
                                Log.e("signUp", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e("signUp", t.getMessage());
                        }
                    });
        } else {
            CaronaeAPI.service(getApplicationContext()).signUp(nome_et.getText().toString(), token_et.getText().toString())
            .enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        User user = response.body();
                        Util.toast(user.getName() + " cadastrado");
                    } else {
                        Util.toast("Erro ao cadastrar");
                        Log.e("signUp", response.message());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("signUp", t.getMessage());
                }
            });
        }

    }
}
