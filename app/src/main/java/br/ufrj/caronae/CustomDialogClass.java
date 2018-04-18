package br.ufrj.caronae;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public Dialog d;
    private TextView title, message, positive_bt, negative_bt;

    public CustomDialogClass(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        title = (TextView) findViewById(R.id.title);
        message = (TextView) findViewById(R.id.message);
        positive_bt = (TextView) findViewById(R.id.positive_bt);
        negative_bt = (TextView) findViewById(R.id.negative_bt);
        positive_bt.setOnClickListener(this);
        negative_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.positive_bt:

                break;

            case R.id.negative_bt:
                dismiss();
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
}