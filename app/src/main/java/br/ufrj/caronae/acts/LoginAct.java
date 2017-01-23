package br.ufrj.caronae.acts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.modelsforjson.LoginForJson;
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
    @Bind(R.id.idUfrj_et)
    EditText idUfrj_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Did that to avoid keyboard overlap EditText when clicked 2 times bug
        token_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String actualText = token_et.getText().toString();
                if (actualText.equals("")){
                    token_et.setPadding(Util.convertDpToPixel(getApplicationContext(), 26)
                            , token_et.getPaddingTop(), token_et.getPaddingRight(), token_et.getPaddingBottom());
                } else {
                    token_et.setPadding(Util.convertDpToPixel(getApplicationContext(), 56)
                            , token_et.getPaddingTop(), token_et.getPaddingRight(), token_et.getPaddingBottom());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.wait), true, true);

        String tokenHolder = token_et.getText().toString();
        final String idUfrj = idUfrj_et.getText().toString();
        final String token = Util.fixBlankSpace(tokenHolder);

        // Busca usuário no servidor, token deve ser com carcteres maiusculos
        App.getNetworkService(getApplicationContext()).login(new LoginForJson(token.toUpperCase(), idUfrj), new Callback<UserWithRidesForJson>() {
            @Override
            public void success(UserWithRidesForJson userWithRides, Response response) {
                if (userWithRides == null || userWithRides.getUser() == null) {
                    Util.toast(R.string.act_login_invalidLogin);
                    return;
                }

                SharedPref.saveUser(userWithRides.getUser());
                SharedPref.saveUserToken(token);
                SharedPref.saveNotifPref("true");

                new SaveRidesAsync(userWithRides).execute();

                String gcmToken = SharedPref.getUserGcmToken();
                if (!gcmToken.equals(SharedPref.MISSING_PREF)) {
                    App.getNetworkService(getApplicationContext()).saveGcmToken(new TokenForJson(gcmToken), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            Log.i("saveGcmToken", "gcm token sent to server");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            try {
                                Log.e("saveGcmToken", error.getMessage());
                            } catch (Exception e) {//sometimes RetrofitError is null
                                Log.e("saveGcmToken", e.getMessage());
                            }
                        }
                    });
                }

                pd.dismiss();

                startActivity(new Intent(LoginAct.this, MainAct.class));
                LoginAct.this.finish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                pd.dismiss();

                try {
                    if (retrofitError.getResponse().getStatus() == 403)
                        Util.toast(R.string.act_login_invalidLogin);
                    else
                        Util.toast(R.string.act_login_loginFail);

                    Log.e("login", retrofitError.getMessage());
                } catch (Exception e) {//sometimes RetrofitError is null
                    Log.e("signUp", e.getMessage());
                }
            }
        });
    }

    @OnClick(R.id.getToken_tv)
    public void getTokenBt() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(App.getHost() + "chave")));
    }

    //@OnClick(R.id.logo)
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
                ride.setTime(Util.formatTime(ride.getTime()));
                String format = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                int c = ride.getDate().compareTo(format);
                if (c >= 0)
                    new Ride(ride).save();
            }

            return null;
        }
    }
}
