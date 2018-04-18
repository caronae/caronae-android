package br.ufrj.caronae.acts;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;

import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.asyncs.LogOut;
import br.ufrj.caronae.frags.FalaeFrag;
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
    TextView edit_bt;
    @BindView(R.id.back_bt)
    RelativeLayout back;
    @BindView(R.id.cancel_bt)
    RelativeLayout cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        fragment = null;
        fragmentClass = MyProfileShowFrag.class;
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
            edit_bt.setText(R.string.edit_bt);
            MyProfileEditFrag frag = (MyProfileEditFrag)fragment;
            frag.saveProfileBtn();
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

    @Override
    public void onBackPressed()
    {
        if(fragmentClass.equals(MyProfileShowFrag.class)) {
            backToMenu();
        }
        else
        {
            showAlert();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileAct.this);
        builder.setTitle(R.string.cancel_profile_edit)
                .setCancelable(false)
                .setPositiveButton(R.string.frag_logout_title, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        View view = MyProfileAct.this.getCurrentFocus();
                        if (view != null) {
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
                        edit_bt.setText(R.string.edit_bt);
                        back.setVisibility(View.VISIBLE);
                        cancel.setVisibility(View.GONE);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(getResources().getColor(R.color.darkblue2));
        nbutton.setText(R.string.continue_edit);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(getResources().getColor(R.color.red));
        pbutton.setText(R.string.discard);
    }
}
