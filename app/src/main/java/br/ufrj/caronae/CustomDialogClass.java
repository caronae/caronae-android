package br.ufrj.caronae;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.ufrj.caronae.acts.MyProfileAct;
import br.ufrj.caronae.acts.RideOfferAct;
import br.ufrj.caronae.frags.MyProfileShowFrag;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    private Activity activity;
    private Fragment fragment;
    private TextView title, message, positive_bt, negative_bt;
    private String currentFrag;
    private ImageView separator;
    LinearLayout buttons;

    public CustomDialogClass(Activity activity, String currentFrag, Fragment fragment) {
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
        setContentView(R.layout.custom_dialog);
        buttons = (LinearLayout) findViewById(R.id.buttons);
        title = (TextView) findViewById(R.id.title);
        message = (TextView) findViewById(R.id.message);
        positive_bt = (TextView) findViewById(R.id.positive_bt);
        negative_bt = (TextView) findViewById(R.id.negative_bt);
        separator = (ImageView) findViewById(R.id.separator);
        positive_bt.setOnClickListener(this);
        negative_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.positive_bt:
                dismiss();
                break;

            case R.id.negative_bt:
                if(currentFrag.equals("MyProfileEdit"))
                {
                    MyProfileAct profileAct;
                    profileAct = (MyProfileAct) activity;
                    profileAct.negativeDialogAction();
                    dismiss();
                }
                else if(currentFrag.equals("MyProfileShow"))
                {
                    MyProfileShowFrag myProfileShowFrag;
                    myProfileShowFrag = (MyProfileShowFrag) fragment;
                    myProfileShowFrag.setActionExit();
                    dismiss();
                }
                else if(currentFrag.equals("RideOfferAct"))
                {
                    RideOfferAct rideOfferAct;
                    rideOfferAct = (RideOfferAct) activity;
                    rideOfferAct.joinAction();
                    dismiss();
                }
                break;

            default:

                break;
        }
    }

    public void setTitleText(String titleText)
    {
        this.title.setText(titleText);
    }

    public void setMessageText(String messageText)
    {
        this.message.setText(messageText);
    }

    public void setPButtonText(String pButtonText)
    {
        this.positive_bt.setText(pButtonText);
    }

    public void setNButtonText(String nButtonText)
    {
        this.negative_bt.setText(nButtonText);
    }

    public void setMessageVisibility(int visibility)
    {
        message.setVisibility(visibility);
    }

    public void enableOnePositiveOption()
    {
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) positive_bt.getLayoutParams();
        this.negative_bt.setVisibility(View.GONE);
        separator.setVisibility(View.GONE);
        llp.weight = 2;
        this.positive_bt.setLayoutParams(llp);
    }

    public void setNegativeButtonColor(int color)
    {
        this.negative_bt.setTextColor(color);
    }
}