package br.ufrj.caronae.frags;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.ufrj.caronae.App;
import br.ufrj.caronae.data.ImageSaver;
import br.ufrj.caronae.R;
import br.ufrj.caronae.customizedviews.RoundedTransformation;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.acts.FalaeAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.acts.MenuOptionsAct;
import br.ufrj.caronae.acts.MyProfileAct;
import br.ufrj.caronae.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionsMenuFrag extends Fragment {

    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.profile_tv)
    TextView profile_tv;
    @BindView(R.id.user_pic)
    ImageView user_pic;
    @BindView(R.id.myprofile_btn)
    TextView myProfile_btn;
    @BindView(R.id.history_btn)
    TextView history_btn;
    @BindView(R.id.faq_btn)
    TextView faq_btn;
    @BindView(R.id.falae_btn)
    TextView falae_btn;
    @BindView(R.id.termsofuse_btn)
    TextView termsOfUse_btn;
    @BindView(R.id.about_btn)
    TextView about_btn;

    public OptionsMenuFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options_menu, container, false);
        ButterKnife.bind(this, view);
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        ((MainAct)getActivity()).showMainItems();

        User user = App.getUser();
        if (user != null) {
            fillUserFields(user);
        }
        return view;
    }

    private void fillUserFields(User user) {
        name_tv.setText(user.getName());
        String info;
        info = user.getProfile() + " | " + user.getCourse();
        profile_tv.setText(info);
        if(SharedPref.getSavedPic())
        {
                Bitmap bmp = new ImageSaver(getContext()).
                        setFileName("myProfile.png").
                        setDirectoryName("images").
                        load();
                user_pic.setImageBitmap(bmp);
        }
        else {
            if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty())
            {
                Picasso.with(getContext()).load(user.getProfilePicUrl())
                        .placeholder(R.drawable.user_pic)
                        .error(R.drawable.user_pic)
                        .transform(new RoundedTransformation())
                        .into(user_pic);
            }
        }
    }

    @OnClick(R.id.user_pic)
    public void clickProfileImage() {
        Intent myProfileAct = new Intent(getActivity(), MyProfileAct.class);
        startActivity(myProfileAct);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.myprofile_btn)
    public void openMyProfile() {
        Intent myProfileAct = new Intent(getActivity(), MyProfileAct.class);
        startActivity(myProfileAct);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.history_btn)
    public void openHistory() {
        Intent historyAct = new Intent(getActivity(), MenuOptionsAct.class);
        historyAct.putExtra("fragId", 1);
        startActivity(historyAct);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.faq_btn)
    public void openFAQ() {
        Intent faqAct = new Intent(getActivity(), MenuOptionsAct.class);
        faqAct.putExtra("fragId", 2);
        startActivity(faqAct);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.falae_btn)
    public void openFalae() {
        Intent falaeAct = new Intent(getActivity(), FalaeAct.class);
        startActivity(falaeAct);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.termsofuse_btn)
    public void openTermsOfUse() {
        Intent termsOfUseAct = new Intent(getActivity(), MenuOptionsAct.class);
        termsOfUseAct.putExtra("fragId", 4);
        startActivity(termsOfUseAct);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.about_btn)
    public void openAbout() {
        Intent aboutAct = new Intent(getActivity(), MenuOptionsAct.class);
        aboutAct.putExtra("fragId", 5);
        startActivity(aboutAct);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }
}
