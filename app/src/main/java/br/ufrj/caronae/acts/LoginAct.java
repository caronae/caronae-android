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

import br.ufrj.caronae.Constants;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.InvalidCredentialsException;
import br.ufrj.caronae.httpapis.LoginService;
import br.ufrj.caronae.httpapis.ServiceCallback;
import br.ufrj.caronae.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginAct extends AppCompatActivity {
    @BindView(R.id.token_et)
    EditText token_et;
    @BindView(R.id.idUfrj_et)
    EditText idUFRJ_et;
    @BindView(R.id.login_manually)
    TextView loginManually;
    @BindView(R.id.send_bt)
    Button loginButton;
    @BindView(R.id.background_left)
    RelativeLayout backgroundLeft;
    @BindView(R.id.background_right)
    RelativeLayout backgroundRight;
    @BindView(R.id.institution_login_button)
    RelativeLayout institutionLoginButton;
    @BindView(R.id.loading_login)
    ProgressBar onLoading;

    private boolean loginWithInstitutionEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (!Constants.BUILD_TYPE.equals("prod")) {
            loginManually.setVisibility(View.VISIBLE);
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

        if (getIntent().getBooleanExtra("startLink", false)) {
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
    public void startLegacyLogin() {
        loginButton.setEnabled(false);
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.wait), true, false);
        String tokenHolder = token_et.getText().toString();
        final String idUFRJ = idUFRJ_et.getText().toString();
        final String token = Util.fixBlankSpaces(tokenHolder).toUpperCase();

        LoginService.service().signInLegacy(idUFRJ, token, new ServiceCallback<User>() {
            @Override
            public void success(User user) {
                pd.dismiss();
                logIn(user);
            }

            @Override
            public void fail(Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                pd.dismiss();
                loginButton.setEnabled(true);

                if (t instanceof InvalidCredentialsException) {
                    Util.toast(R.string.loginactivity_invalid_login);
                    return;
                }

                Util.toast(R.string.loginactivity_login_fail);
            }
        });
    }

    //PROD Login
    private void startLogin(String id, String token) {
        LoginService.service().signIn(id, token, new ServiceCallback<User>() {
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

    @OnClick(R.id.login_manually)
    public void toggleLoginMode() {
        if (loginWithInstitutionEnabled) {
            backgroundLeft.setVisibility(View.GONE);
            backgroundRight.setVisibility(View.GONE);
            institutionLoginButton.setVisibility(View.GONE);
            idUFRJ_et.setVisibility(View.VISIBLE);
            token_et.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);

            loginManually.setText(R.string.login_with_institution);
            loginWithInstitutionEnabled = false;
        } else {
            backgroundLeft.setVisibility(View.VISIBLE);
            backgroundRight.setVisibility(View.VISIBLE);
            institutionLoginButton.setVisibility(View.VISIBLE);
            idUFRJ_et.setVisibility(View.GONE);
            token_et.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);

            loginManually.setText(R.string.login_manually);
            loginWithInstitutionEnabled = true;
        }
    }

    @OnClick(R.id.institution_login_button)
    public void openExternalLogin() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.API_BASE_URL + "login?type=app_jwt")));
    }

    private void logIn(User user) {
        Class nextActivity;
        if (user.hasIncompleteProfile()) {
            nextActivity = WelcomeAct.class;
        } else {
            nextActivity = MainAct.class;
        }

        startActivity(new Intent(this, nextActivity));
        LoginAct.this.finish();
    }
}
