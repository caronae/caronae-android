package br.ufrj.caronae.acts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.TokenForJson;
import br.ufrj.caronae.models.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginAct extends AppCompatActivity {
    @Bind(R.id.token_et)
    EditText token_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        token_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendBt();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        final ProgressDialog pd = ProgressDialog.show(this, "", "Aguarde", true, true);
        final String token = token_et.getText().toString();
        App.getNetworkService().sendToken(new TokenForJson(token), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                pd.dismiss();

                if (user == null) {
                    Toast.makeText(App.inst(), "Nenhum usúario encontrado com esse token", Toast.LENGTH_SHORT).show();
                    return;
                }

                App.saveUser(user);

                App.saveToken(token);

                startActivity(new Intent(LoginAct.this, MainAct.class));
                LoginAct.this.finish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                pd.dismiss();
                Toast.makeText(App.inst(), "Erro", Toast.LENGTH_SHORT).show();
                Log.e("sendToken", retrofitError.getMessage());
            }
        });
    }
}
