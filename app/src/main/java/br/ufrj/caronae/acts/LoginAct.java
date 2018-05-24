package br.ufrj.caronae.acts;

import android.app.Activity;
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

import br.ufrj.caronae.Constants;
import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.modelsforjson.LoginForJson;
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

    @BindView(R.id.send_bt)
    Button loginButton;

    @BindView(R.id.left_back_v3)
    RelativeLayout lBackV3;
    @BindView(R.id.right_back_v3)
    RelativeLayout rBackV3;
    @BindView(R.id.login_bt_v3)
    RelativeLayout loginBtV3;

    @BindView(R.id.loading_login)
    ProgressBar onLoading;

    boolean startLink;
    private String id, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(!Constants.BUILD_TYPE.equals("prod"))
        {
            lBackV3.setVisibility(View.GONE);
            rBackV3.setVisibility(View.GONE);
            loginBtV3.setVisibility(View.GONE);
            idUfrj_et.setVisibility(View.VISIBLE);
            token_et.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        }
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
        startLink = getIntent().getBooleanExtra("startLink", false);
        if(startLink)
        {
            loginBtV3.setClickable(false);
            loginBtV3.setFocusable(false);
            onLoading.setVisibility(View.VISIBLE);
            id = getIntent().getStringExtra("id");
            token = getIntent().getStringExtra("token");
            startLogin();
        }
    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        loginButton.setEnabled(false);
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.wait), true, true);
        final Activity act = this;
        String tokenHolder = token_et.getText().toString();
        final String idUfrj = idUfrj_et.getText().toString();
        final String token = Util.fixBlankSpaces(tokenHolder).toUpperCase();

            Call<UserForJson> loginCall = CaronaeAPI.service(getApplicationContext()).login(new LoginForJson(token, idUfrj));
            loginCall.enqueue(new Callback<UserForJson>() {
                @Override
                public void onResponse(Call<UserForJson> call, Response<UserForJson> response) {
                    // response.isSuccessful() is true if the response code is 2xx
                    if (response.isSuccessful()) {
                        UserForJson user = response.body();

                        if (user == null || user.getUser() == null) {
                            Util.toast(R.string.act_login_invalidLogin);
                            return;
                        }
                        pd.dismiss();
                        SharedPref.saveUser(user.getUser());
                        SharedPref.saveUserToken(token);
                        SharedPref.saveUserIdUfrj(idUfrj);
                        SharedPref.saveNotifPref("true");
                        
                        if (user.getUser().getEmail() == null || user.getUser().getEmail().isEmpty() || user.getUser().getPhoneNumber() == null || user.getUser().getPhoneNumber().isEmpty() || user.getUser().getLocation() == null || user.getUser().getLocation().isEmpty()) {
                            Intent firstLogin = new Intent(act, WelcomeAct.class);
                            startActivity(firstLogin);
                            LoginAct.this.finish();
                        }
                        else {
                            Intent mainAct = new Intent(act, MainAct.class);
                            startActivity(mainAct);
                            LoginAct.this.finish();
                        }
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
                public void onFailure(Call<UserForJson> call, Throwable t) {
                    // handle execution failures like no internet connectivity
                    Log.e("Login", "Failure: " + t.getMessage());
                    Util.toast(R.string.act_login_loginFail);
                    pd.dismiss();
                    loginButton.setEnabled(true);
                }
            });
    }

    private void startLogin()
    {
        Call<UserForJson> loginCall = CaronaeAPI.service(getApplicationContext()).getUser(id);
        loginCall.enqueue(new Callback<UserForJson>() {
            @Override
            public void onResponse(Call<UserForJson> call, Response<UserForJson> response) {
                if (response.isSuccessful()) {
                    UserForJson user = response.body();
                    Util.debug(user.getUser().getName());
                } else {

                }
            }

            @Override
            public void onFailure(Call<UserForJson> call, Throwable t) {

            }
        });
        Call<String> getToken = CaronaeAPI.service(getApplicationContext()).getToken(token);
        getToken.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String user = response.body();
                    Util.debug(user);
                } else {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    @OnClick(R.id.login_bt_v3)
    public void loginV3()
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.API_BASE_URL + "login?type=app_jwt")));
    }

}
