package br.ufrj.caronae.customizedviews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


import br.ufrj.caronae.R;

public class ExpandPhotoDialogClass extends Dialog {

    private Activity activity;
    private Context context;
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
        this.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        image = findViewById(R.id.expanded_image);
    }

    public void setImage(String url)
    {
        Picasso.with(context).load(url).into(image);
    }
}