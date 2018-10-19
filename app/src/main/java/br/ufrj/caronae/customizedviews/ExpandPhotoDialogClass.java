package br.ufrj.caronae.customizedviews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;


import br.ufrj.caronae.R;

public class ExpandPhotoDialogClass extends Dialog implements android.view.View.OnClickListener{

    private Activity activity;
    private Context context;
    private RelativeLayout dismiss, lay;
    private ImageView image;

    public ExpandPhotoDialogClass(Activity activity, Context ctx) {
        super(activity);
        this.activity = activity;
        this.context = ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.expand_photo_dialog);
        this.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.BLACK));
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        lay = findViewById(R.id.lay);
        dismiss = findViewById(R.id.dismiss);
        image = findViewById(R.id.expanded_image);
        lay.setOnClickListener(this);
        dismiss.setOnClickListener(this);
        image.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dismiss:
                dismiss();
                break;
            default:
                if(dismiss.getVisibility() == View.VISIBLE)
                {
                    dismiss.setVisibility(View.GONE);
                }
                else
                {
                    dismiss.setVisibility(View.VISIBLE);
                }
        }
    }

    public void setImage(String url)
    {
        Picasso.with(context).load(url).into(image);
    }
}