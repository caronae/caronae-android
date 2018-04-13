package br.ufrj.caronae.acts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.frags.FalaeFrag;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FalaeAct extends AppCompatActivity {

    private Class fragmentClass;

    @BindView(R.id.send_bt)
    TextView send_bt;

    FalaeFrag frag;

    public String message;
    public String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Fragment fragment = null;
        fragmentClass = FalaeFrag.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_falae);
        ButterKnife.bind(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
        frag = (FalaeFrag) fragment;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        backToMenu();
    }

    @OnClick(R.id.back_bt)
    public void backTouch()
    {
        backToMenu();
    }

    private void backToMenu()
    {
        finish();
        Intent mainAct = new Intent(this, MainAct.class);
        SharedPref.NAV_INDICATOR = "Menu";
        startActivity(mainAct);
    }

    @OnClick(R.id.send_bt)
    public void sendBt()
    {
        message = frag.getMessage();
        subject = frag.getSubject();
        if (message.isEmpty()) {
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(FalaeAct.this);
            builder.setCancelable(false);
            builder.setTitle("Ops!");
            builder.setMessage("Parece que você esqueceu de preencher sua mensagem.");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.CENTER;
            dialog.show();
            return;
        }
        else {
            message = message
                    + "\n\n--------------------------------\n"
                    + "Device: " + android.os.Build.MODEL + " (Android " + android.os.Build.VERSION.RELEASE + ")\n"
                    + "Versão do app: " + Util.getAppVersionName(this);
        }
        final ProgressDialog pd = ProgressDialog.show(FalaeAct.this, "", getString(R.string.wait), true, true);
        CaronaeAPI.service(getBaseContext()).falaeSendMessage(new FalaeMsgForJson(subject, message))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            pd.dismiss();
                            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(FalaeAct.this);
                            builder.setCancelable(false);
                            builder.setTitle("Mensagem enviada!");
                            builder.setMessage("Obrigado por nos mandar uma mensagem. Nossa equipe irá entrar em contato em breve.");
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPref.NAV_INDICATOR = "Menu";
                                    backToMenu();
                                }
                            });
                            android.support.v7.app.AlertDialog dialog = builder.create();
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                            wmlp.gravity = Gravity.CENTER;
                            dialog.show();
                        } else {
                            Util.treatResponseFromServer(response);
                            pd.dismiss();
                            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(FalaeAct.this);
                            builder.setCancelable(false);
                            builder.setTitle("Mensagem não enviada");
                            builder.setMessage("Ocorreu um erro enviando sua mensagem. Verifique sua conexão e tente novamente.");
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            android.support.v7.app.AlertDialog dialog = builder.create();
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                            wmlp.gravity = Gravity.CENTER;
                            dialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pd.dismiss();
                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(FalaeAct.this);
                        builder.setCancelable(false);
                        builder.setTitle("Mensagem não enviada");
                        builder.setMessage("Ocorreu um erro enviando sua mensagem. Verifique sua conexão e tente novamente.");
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        android.support.v7.app.AlertDialog dialog = builder.create();
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                        wmlp.gravity = Gravity.CENTER;
                        dialog.show();
                    }
                });
    }
}
