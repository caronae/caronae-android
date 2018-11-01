package br.ufrj.caronae.acts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import br.ufrj.caronae.customizedviews.CustomDialogClass;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.frags.FalaeFrag;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FalaeAct extends AppCompatActivity {

    @BindView(R.id.send_bt)
    TextView send_bt;
    @BindView(R.id.activity_back)
    TextView title;

    FalaeFrag frag;

    public String message;
    public String subject;
    public String fromWhere = "";

    private RideForJson rideOffer;
    private String from, user2, status;

    boolean fromAnother, showPhone;
    private int idRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falae);
        ButterKnife.bind(this);
        boolean fromProfile;
        showPhone = getIntent().getBooleanExtra("showPhone", false);
        try {
            fromWhere = getIntent().getStringExtra("fromWhere");
            status = getIntent().getStringExtra("status");
        }catch(Exception e){}
        fromProfile = getIntent().getBooleanExtra("fromProfile", false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        idRide = getIntent().getExtras().getInt("id");
        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = FalaeFrag.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flContent, fragment).commit();
        frag = (FalaeFrag) fragment;
        if(fromProfile)
        {
            String driverName;
            title.setText(getResources().getString(R.string.back));
            user2 = getIntent().getExtras().getString("user");
            from = getIntent().getExtras().getString("from");
            fromAnother = getIntent().getBooleanExtra("fromAnother", false);
            rideOffer = getIntent().getExtras().getParcelable("ride");
            driverName = getIntent().getStringExtra("driver");
            ((FalaeFrag)fragment).reason_txt = getResources().getString(R.string.report);
            ((FalaeFrag)fragment).subject_txt = "Denúncia sobre usuário " + driverName;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(title.getText().equals("Menu")) {
            backToMenu();
        }
        else
        {
            backToProfile();
        }
    }

    @OnClick(R.id.back_bt)
    public void backTouch()
    {
        if(title.getText().equals("Menu")) {
            backToMenu();
        }
        else
        {
            backToProfile();
        }

    }

    public void backToMenu()
    {
        finish();
        Intent mainAct = new Intent(this, MainAct.class);
        SharedPref.NAV_INDICATOR = "Menu";
        startActivity(mainAct);
        this.overridePendingTransition(R.anim.anim_left_slide_in,R.anim.anim_right_slide_out);
    }

    private void backToProfile()
    {
        finish();
        Intent profileAct = new Intent(this, ProfileAct.class);
        profileAct.putExtra("showPhone", showPhone);
        profileAct.putExtra("status", status);
        profileAct.putExtra("user", user2);
        profileAct.putExtra("from", from);
        profileAct.putExtra("fromAnother", true);
        profileAct.putExtra("ride", rideOffer);
        profileAct.putExtra("fromWhere", fromWhere);
        profileAct.putExtra("id",  idRide);
        startActivity(profileAct);
        this.overridePendingTransition(R.anim.anim_left_slide_in,R.anim.anim_right_slide_out);
    }

    @OnClick(R.id.send_bt)
    public void sendBt()
    {
        Activity act = this;
        message = frag.getMessage();
        subject = frag.getSubject();
        if (message.isEmpty()) {
            CustomDialogClass cdc = new CustomDialogClass(this, "", null);
            cdc.show();
            cdc.setTitleText("Ops!");
            cdc.setMessageText("Parece que você esqueceu de preencher sua mensagem.");
            cdc.setPButtonText(getResources().getString(R.string.ok_uppercase));
            cdc.enableOnePositiveOption();
            return;
        }
        else {
            message = message
                    + "\n\n--------------------------------\n"
                    + "Device: " + android.os.Build.MODEL + " (Android " + android.os.Build.VERSION.RELEASE + ")\n"
                    + "Versão do app: " + Util.getAppVersionName();
        }
        final ProgressDialog pd = ProgressDialog.show(FalaeAct.this, "", getString(R.string.wait), true, true);
        CaronaeAPI.service().falaeSendMessage(new FalaeMsgForJson(subject, message))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            pd.dismiss();
                            CustomDialogClass cdc = new CustomDialogClass(act, "Falae", null);
                            cdc.show();
                            cdc.setTitleText("Mensagem enviada!");
                            cdc.setMessageText("Obrigado por nos mandar uma mensagem. Nossa equipe irá entrar em contato em breve.");
                            cdc.setPButtonText(getResources().getString(R.string.ok_uppercase));
                            cdc.enableOnePositiveOption();
                            SharedPref.NAV_INDICATOR = "Menu";
                        } else {
                            Util.treatResponseFromServer(response);
                            pd.dismiss();
                            CustomDialogClass cdc = new CustomDialogClass(act, "", null);
                            cdc.show();
                            cdc.setTitleText("Mensagem não enviada");
                            cdc.setMessageText("Ocorreu um erro enviando sua mensagem. Verifique sua conexão e tente novamente.");
                            cdc.setPButtonText(getResources().getString(R.string.ok_uppercase));
                            cdc.enableOnePositiveOption();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pd.dismiss();
                        CustomDialogClass cdc = new CustomDialogClass(act, "", null);
                        cdc.show();
                        cdc.setTitleText("Mensagem não enviada");
                        cdc.setMessageText("Ocorreu um erro enviando sua mensagem. Verifique sua conexão e tente novamente.");
                        cdc.setPButtonText(getResources().getString(R.string.ok_uppercase));
                        cdc.enableOnePositiveOption();
                    }
                });
    }
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
