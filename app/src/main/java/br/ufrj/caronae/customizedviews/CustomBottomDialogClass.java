package br.ufrj.caronae.customizedviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;

import android.widget.LinearLayout;

import br.ufrj.caronae.R;
import br.ufrj.caronae.frags.MyProfileEditFrag;

public class CustomBottomDialogClass extends Dialog implements View.OnClickListener {

    private Activity activity;
    private Fragment fragment;
    private String currentFrag;
    private LinearLayout cancel_bt, remove_bt, facebook_bt, phone_bt;

    public CustomBottomDialogClass(Activity activity, String currentFrag, Fragment fragment) {
        super(activity);
        this.activity = activity;
        this.fragment = fragment;
        this.currentFrag = currentFrag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_bottom_dialog);
        this.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        facebook_bt = findViewById(R.id.facebook_option);
        cancel_bt = findViewById(R.id.cancel_option);
        remove_bt = findViewById(R.id.remove_option);
        phone_bt = findViewById(R.id.phone_option);
        facebook_bt.setOnClickListener(this);
        cancel_bt.setOnClickListener(this);
        remove_bt.setOnClickListener(this);
        phone_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.phone_option:
                dismiss();
                switch (currentFrag)
                {
                    case "MyProfileEdit":
                        ((MyProfileEditFrag)fragment).changeToMediaAct();
                        break;
                    default:

                        break;
                }
                break;
            case R.id.facebook_option:
                dismiss();
                switch (currentFrag)
                {
                    case "MyProfileEdit":
                        ((MyProfileEditFrag)fragment).useFacebookPhoto();
                        break;
                    default:

                        break;
                }
                break;
            case R.id.remove_option:
                dismiss();
                switch (currentFrag)
                {
                    case "MyProfileEdit":
                        ((MyProfileEditFrag)fragment).removePhoto();
                        break;
                    default:

                        break;
                }
                break;

            case R.id.cancel_option:
                dismiss();
                break;

            default:

                break;
        }
    }
}