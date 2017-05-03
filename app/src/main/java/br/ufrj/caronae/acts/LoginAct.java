package br.ufrj.caronae.acts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.rey.material.widget.Button;

import java.io.IOException;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAct extends AppCompatActivity {
    @Bind(R.id.token_et)
    EditText token_et;
    @Bind(R.id.idUfrj_et)
    EditText idUfrj_et;
    @Bind(R.id.send_bt)
    Button loginButton;

    public boolean ASYNC_IS_RUNNING = false;


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
        loginButton.setEnabled(false);
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.wait), true, true);

        String tokenHolder = token_et.getText().toString();
        final String idUfrj = idUfrj_et.getText().toString();
        final String token = Util.fixBlankSpace(tokenHolder).toUpperCase();

            Call<UserWithRidesForJson> loginCall = App.getNetworkService(getApplicationContext()).login(new LoginForJson(token.toUpperCase(), idUfrj));
            loginCall.enqueue(new Callback<UserWithRidesForJson>() {
                @Override
                public void onResponse(Call<UserWithRidesForJson> call, Response<UserWithRidesForJson> response) {
                    // response.isSuccessful() is true if the response code is 2xx
                    if (response.isSuccessful()) {
                        UserWithRidesForJson userWithRides = response.body();

                        if (userWithRides == null || userWithRides.getUser() == null) {
                            Util.toast(R.string.act_login_invalidLogin);
                            return;
                        }

                        SharedPref.saveUser(userWithRides.getUser());
                        SharedPref.saveUserToken(token);
                        SharedPref.saveNotifPref("true");

                        if (ASYNC_IS_RUNNING == false) {
                            ASYNC_IS_RUNNING = true;
                            new SaveRidesAsync(userWithRides).execute();
                        }

                        String gcmToken = SharedPref.getUserGcmToken();
                        if (!gcmToken.equals(SharedPref.MISSING_PREF)) {
                            Call<ResponseBody> saveGcmtokenCall = App.getNetworkService(getApplicationContext()).saveGcmToken(new TokenForJson(gcmToken));
                            saveGcmtokenCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Log.i("saveGcmToken", "gcm token sent to server");
                                    } else {
                                        Log.e("saveGcmToken", "Code: " + response.code() +
                                                "Message: " +
                                                response.message() == null ? "Null" : response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("saveGcmToken", "Failure");
                                }
                            });
                        }
                        startActivity(new Intent(LoginAct.this, MainAct.class));
                        LoginAct.this.finish();
                    } else {
                        // Server Errors
                        pd.dismiss();
                        loginButton.setEnabled(true);
                        int statusCode = response.code();
                        ResponseBody errorBody = response.errorBody();
                        if (statusCode == 401) {
                            Util.toast(R.string.act_login_invalidLogin);
                        } else try {
                            if (errorBody.string().equals("timeout")) {
                                Util.toast(R.string.no_conexion);
                            } else {
                                Util.toast(R.string.act_login_loginFail);
                                try {
                                    Log.e("Login", "Code: " + statusCode
                                            + "\n Body: " + errorBody.string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserWithRidesForJson> call, Throwable t) {
                    // handle execution failures like no internet connectivity
                    Log.e("Login", "Failure: " + t.getMessage());
                    Util.toast(R.string.act_login_loginFail);
                    pd.dismiss();
                    loginButton.setEnabled(true);
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
                    if (Ride.findById(Ride.class, ride.getDbId()) == null) {
                        new Ride(ride).save();
                    }
            }

            return null;
        }
    }
}
