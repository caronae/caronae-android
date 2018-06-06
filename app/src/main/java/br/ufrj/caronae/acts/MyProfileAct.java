package br.ufrj.caronae.acts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;

import br.ufrj.caronae.customizedviews.CustomDialogClass;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.frags.MyProfileEditFrag;
import br.ufrj.caronae.frags.MyProfileShowFrag;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyProfileAct extends AppCompatActivity {

    private Class fragmentClass;
    private Fragment fragment;
    private CallbackManager callbackManager;

    @BindView(R.id.edit_bt)
    public TextView edit_bt;
    @BindView(R.id.back_bt)
    RelativeLayout back;
    @BindView(R.id.cancel_bt)
    RelativeLayout cancel;
    @BindView(R.id.progress_bar)
    public ProgressBar progressBar;
    boolean firstLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        firstLogin = false;
        try {
            firstLogin = getIntent().getBooleanExtra("firstLogin", false);
        } catch (Exception e){}
        if(firstLogin)
        {
            SharedPref.NAV_INDICATOR = "Menu";
            edit_bt.setText(R.string.save);
            back.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            fragmentClass = MyProfileEditFrag.class;
        }
        else
        {
            fragmentClass = MyProfileShowFrag.class;
        }
        fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flContent, fragment).commit();
    }

    @OnClick(R.id.edit_bt)
    public void onTouchEditButton()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(back.getVisibility() == View.VISIBLE)
        {
            edit_bt.setText(R.string.save);
            fragment = null;
            fragmentClass = MyProfileEditFrag.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragmentManager.popBackStack();
            transaction.setCustomAnimations(R.anim.anim_down_slide_in, R.anim.anim_up_slide_out);
            transaction.replace(R.id.flContent, fragment).commit();
            back.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
        }
        else
        {
            MyProfileEditFrag frag = (MyProfileEditFrag)fragment;
            frag.saveProfileBtn();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!firstLogin) {
            if (fragmentClass.equals(MyProfileShowFrag.class)) {
                backToMenu();
            } else {
                showAlert();
            }
        }else{
            Intent firstLogin = new Intent(this, WelcomeAct.class);
            startActivity(firstLogin);
            overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
            MyProfileAct.this.finish();
        }
    }

    @OnClick(R.id.back_bt)
    public void backMenuTouch()
    {
        backToMenu();
    }

    @OnClick(R.id.cancel_bt)
    public void backCancelTouch()
    {
        showAlert();
    }

    private void backToMenu()
    {
        finish();
        Intent mainAct = new Intent(this, MainAct.class);
        SharedPref.NAV_INDICATOR = "Menu";
        startActivity(mainAct);
        this.overridePendingTransition(R.anim.anim_left_slide_in,R.anim.anim_right_slide_out);
    }

    public CallbackManager getFbCallbackManager() {
        if (callbackManager == null)
            callbackManager = CallbackManager.Factory.create();

        return callbackManager;
    }

    private void showAlert()
    {
        CustomDialogClass customDialogClass;
        customDialogClass= new CustomDialogClass(this, "MyProfileEdit", null);
        customDialogClass.show();
    }

    public void negativeDialogAction()
    {
        View view = MyProfileAct.this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        fragment = null;
        fragmentClass = MyProfileShowFrag.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
        if(progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        edit_bt.setText(R.string.edit);
        edit_bt.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
    }

    public void onSuccessSave()
    {
        edit_bt.setText(R.string.edit);
        firstLogin = false;
        fragment = null;
        fragmentClass = MyProfileShowFrag.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack();
        transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
        back.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
    }
}
