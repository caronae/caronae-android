package br.ufrj.caronae.acts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.modelsforjson.TokenForJson;
import br.ufrj.caronae.models.modelsforjson.UserWithRidesForJson;
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
        App.getNetworkService().login(new TokenForJson(token), new Callback<UserWithRidesForJson>() {
            @Override
            public void success(UserWithRidesForJson userWithRides, Response response) {
                pd.dismiss();

                if (userWithRides == null || userWithRides.getUser() == null) {
                    App.toast("Chave inválida");
                    return;
                }

                App.saveUser(userWithRides.getUser());
                App.saveToken(token);
                App.putPref(App.NOTIFICATIONS_ON_PREF_KEY, "true");

                new SaveRidesAsync(userWithRides).execute();

                String gcmToken = App.getPref(App.GCM_TOKEN_PREF_KEY);
                if (!gcmToken.equals(App.MISSING_PREF)) {
                    App.getNetworkService().saveGcmToken(new TokenForJson(gcmToken), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            Log.i("saveGcmToken", "gcm token sent to server");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("saveGcmToken", error.getMessage());
                        }
                    });
                }

                startActivity(new Intent(LoginAct.this, MainAct.class));
                LoginAct.this.finish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                pd.dismiss();
                App.toast("Erro ao fazer login");
                Log.e("login", retrofitError.getMessage());
            }
        });
    }

    @OnClick(R.id.logo)
    public void signUp() {
        startActivity(new Intent(LoginAct.this, SignUpAct.class));
    }

    private class SaveRidesAsync extends AsyncTask<Void, Void, Void> {
        private final UserWithRidesForJson userWithRides;

        public SaveRidesAsync(UserWithRidesForJson userWithRides) {
            this.userWithRides = userWithRides;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            for (Ride ride : userWithRides.getRides()) {
                new Ride(ride).save();
            }

            return null;
        }
    }
}
