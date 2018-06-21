package br.ufrj.caronae.acts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.frags.AboutFrag;
import br.ufrj.caronae.frags.FAQFrag;
import br.ufrj.caronae.frags.RidesHistoryFrag;
import br.ufrj.caronae.frags.TermsOfUseFrag;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuOptionsAct extends AppCompatActivity {

    private Class fragmentClass;

    TextView header_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_options);
        ButterKnife.bind(this);
        Fragment fragment = null;
        Bundle b = getIntent().getExtras();
        int fragId = b.getInt("fragId");
        fragmentClass = setFrag(fragId);
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
        header_tv = (TextView)findViewById(R.id.header_text);
        switch (fragId) {
            case 1:
                header_tv.setText(R.string.historic);
                break;
            case 2:
                header_tv.setText(R.string.faq_uppercase);
                break;
            case 4:
                header_tv.setText(R.string.fragment_termsofuse_title);
                break;
            case 5:
                header_tv.setText(R.string.about);
                break;
        }
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

    private Class setFrag(int fragId)
    {
        switch (fragId)
        {
            case 1:
                return RidesHistoryFrag.class;
            case 2:
                return FAQFrag.class;
            case 4:
                return TermsOfUseFrag.class;
            case 5:
                return AboutFrag.class;
            default:
                Log.e("Error:", " Null fragId");
                return null;
        }
    }

    private void backToMenu()
    {
        finish();
        Intent mainAct = new Intent(this, MainAct.class);
        SharedPref.NAV_INDICATOR = "Menu";
        startActivity(mainAct);
        this.overridePendingTransition(R.anim.anim_left_slide_in,R.anim.anim_right_slide_out);
    }
}
