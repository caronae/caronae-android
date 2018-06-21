package br.ufrj.caronae.customizedviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.acts.RideDetailAct;
import br.ufrj.caronae.frags.MyProfileShowFrag;

public class CustomPhoneDialogClass extends Dialog implements View.OnClickListener {

    private Activity activity;
    private Fragment fragment;
    private String currentFrag, userPhone = "Ligar para ";
    private LinearLayout cancel_bt,  copy_bt, add_bt, call_bt;
    private TextView phone_tv;

    public CustomPhoneDialogClass(Activity activity, String currentFrag, Fragment fragment, String userPhone) {
        super(activity);
        this.activity = activity;
        this.fragment = fragment;
        this.currentFrag = currentFrag;
        this.userPhone += userPhone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_phone_dialog);
        this.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        add_bt = findViewById(R.id.add_option);
        cancel_bt = findViewById(R.id.cancel_option);
        copy_bt = findViewById(R.id.copy_option);
        call_bt = findViewById(R.id.call_option);
        phone_tv = findViewById(R.id.user_phone);
        phone_tv.setText(userPhone);
        add_bt.setOnClickListener(this);
        cancel_bt.setOnClickListener(this);
        copy_bt.setOnClickListener(this);
        call_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.call_option:
                dismiss();
                switch (currentFrag)
                {
                    case "MyProfileShow":
                        ((MyProfileShowFrag)fragment).callUserPhone();
                        break;
                    case "ProfileAct":
                        ((ProfileAct)activity).callUserPhone();
                        break;
                    case "RideDetailAct":
                        ((RideDetailAct)activity).callUserPhone();
                        break;
                }
                break;
            case R.id.add_option:
                dismiss();
                switch (currentFrag)
                {
                    case "MyProfileShow":
                        ((MyProfileShowFrag)fragment).addUserPhone();
                        break;
                    case "ProfileAct":
                        ((ProfileAct)activity).addUserPhone();
                        break;
                    case "RideDetailAct":
                        ((RideDetailAct)activity).addUserPhone();
                        break;
                }
                break;
            case R.id.copy_option:
                dismiss();
                switch (currentFrag)
                {
                    case "MyProfileShow":
                        ((MyProfileShowFrag)fragment).copyUserPhone();
                        break;
                    case "ProfileAct":
                        ((ProfileAct)activity).copyUserPhone();
                        break;
                    case "RideDetailAct":
                        ((RideDetailAct)activity).copyUserPhone();
                        break;
                }
                break;

            case R.id.cancel_option:
                dismiss();
                break;
        }
    }
}