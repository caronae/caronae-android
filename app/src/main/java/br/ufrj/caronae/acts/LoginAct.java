package br.ufrj.caronae.acts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import br.ufrj.caronae.App;
import br.ufrj.caronae.Constants;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.httpapis.LoginService;
import br.ufrj.caronae.httpapis.ServiceCallback;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.LoginForJson;
import br.ufrj.caronae.models.modelsforjson.PlacesForJson;
import br.ufrj.caronae.models.modelsforjson.UserForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAct extends AppCompatActivity {
    @BindView(R.id.token_et)
    EditText token_et;
    @BindView(R.id.idUfrj_et)
    EditText idUfrj_et;
    @BindView(R.id.key)
    TextView getKeyLink;
    @BindView(R.id.send_bt)
    Button loginButton;
    @BindView(R.id.left_back_v3)
    RelativeLayout backgroundLeft;
    @BindView(R.id.right_back_v3)
    RelativeLayout backgroundRight;
    @BindView(R.id.institution_login_button)
    RelativeLayout institutionLoginButton;

    @BindView(R.id.loading_login)
    ProgressBar onLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(Constants.BUILD_TYPE.equals("prod"))
        {
            backgroundLeft.setVisibility(View.GONE);
            backgroundRight.setVisibility(View.GONE);
            institutionLoginButton.setVisibility(View.GONE);
            idUfrj_et.setVisibility(View.VISIBLE);
            token_et.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            getKeyLink.setVisibility(View.VISIBLE);
        }
        else
        {
            backgroundLeft.setVisibility(View.GONE);
            backgroundRight.setVisibility(View.GONE);
            institutionLoginButton.setVisibility(View.GONE);
            idUfrj_et.setVisibility(View.VISIBLE);
            token_et.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        }

        token_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    startLegacyLogin();
                    handled = true;
                }
                return handled;
            }
        });

        if(getIntent().getBooleanExtra("startLink", false))
        {
            institutionLoginButton.setClickable(false);
            institutionLoginButton.setFocusable(false);
            onLoading.setVisibility(View.VISIBLE);

            String id = getIntent().getStringExtra("id");
            String token = getIntent().getStringExtra("token");
            startLogin(id, token);
        }
    }

    //DEV Login
    @OnClick(R.id.send_bt)
    public void startLegacyLogin()
    {
        loginButton.setEnabled(false);
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.wait), true, true);
        String tokenHolder = token_et.getText().toString();
        final String idUfrj = idUfrj_et.getText().toString();
        final String token = Util.fixBlankSpaces(tokenHolder).toUpperCase();

        Call<UserForJson> loginCall = CaronaeAPI.service(getApplicationContext()).login(new LoginForJson(token, idUfrj));
            loginCall.enqueue(new Callback<UserForJson>() {
                @Override
                public void onResponse(Call<UserForJson> call, Response<UserForJson> response) {
                    // response.isSuccessful() is true if the response code is 2xx
                    if (response.isSuccessful()) {
                        final UserForJson user = response.body();
                        if (user == null || user.getUser() == null)
                        {
                            Util.toast(R.string.loginactivity_invalid_login);
                            return;
                        }
                        SharedPref.saveUser(user.getUser());
                        SharedPref.saveUserToken(token);
                        SharedPref.saveUserIdUfrj(idUfrj);
                        SharedPref.saveNotifPref("true");
                        if(SharedPref.checkExistence(SharedPref.PLACE_KEY))
                        {
                            pd.dismiss();
                            logIn(user.getUser());
                        }
                        else
                        {
                            CaronaeAPI.service(App.getInst()).getPlaces()
                                .enqueue(new Callback<PlacesForJson>() {
                                    @Override
                                    public void onResponse(Call<PlacesForJson> call, Response<PlacesForJson> response) {
                                        if (response.isSuccessful()) {
                                            pd.dismiss();
                                            PlacesForJson places = response.body();
                                            SharedPref.setPlace(places);
                                            logIn(user.getUser());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<PlacesForJson> call, Throwable t) {
                                        Log.e("ERROR: ", t.getMessage());
                                        pd.dismiss();
                                        loginButton.setEnabled(true);
                                        int statusCode = response.code();
                                        ResponseBody errorBody = response.errorBody();
                                        if (statusCode == 401) {
                                            Util.toast(R.string.loginactivity_invalid_login);
                                        } else try {
                                            if (errorBody.string().equals("timeout")) {
                                                Util.toast(R.string.no_conexion);
                                            } else {
                                                Util.toast(R.string.loginactivity_login_fail);
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
                                });
                        }
                    } else {
                        // Server Errors
                        pd.dismiss();
                        loginButton.setEnabled(true);
                        int statusCode = response.code();
                        ResponseBody errorBody = response.errorBody();
                        if (statusCode == 401) {
                            Util.toast(R.string.loginactivity_invalid_login);
                        } else try {
                            if (errorBody.string().equals("timeout")) {
                                Util.toast(R.string.no_conexion);
                            } else {
                                Util.toast(R.string.loginactivity_login_fail);
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
                public void onFailure(Call<UserForJson> call, Throwable t) {
                    // handle execution failures like no internet connectivity
                    Log.e("Login", "Failure: " + t.getMessage());
                    Util.toast(R.string.loginactivity_login_fail);
                    pd.dismiss();
                    loginButton.setEnabled(true);
                }
            });
    }

    //PROD Login
    private void startLogin(String id, String token)
    {
        LoginService loginService = new LoginService();
        loginService.signIn(id, token, new ServiceCallback<User>()
        {
            @Override
            public void success(User user) {
                logIn(user);
            }

            @Override
            public void fail(Throwable t) {
                Log.e("Login", "Erro ao fazer login");
                Util.toast(R.string.loginactivity_login_fail);

                institutionLoginButton.setClickable(true);
                institutionLoginButton.setFocusable(true);
                onLoading.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.institution_login_button)
    public void openExternalLogin()
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.API_BASE_URL + "login?type=app_jwt")));
    }

    @OnClick(R.id.key)
    public void getTokenBt() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CaronaeAPI.BASE_URL + "login")));
    }

    private void logIn(User user)
    {
        if (user.hasIncompleteProfile()) {
            Intent firstLogin = new Intent(this, WelcomeAct.class);
            startActivity(firstLogin);
            LoginAct.this.finish();
        } else {
            Intent mainAct = new Intent(this, MainAct.class);
            startActivity(mainAct);
            LoginAct.this.finish();
        }
    }
}
