package br.ufrj.caronae.frags;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.acts.MenuOptionsAct;
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
        if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty())
            Picasso.with(getContext()).load(user.getProfilePicUrl())
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(user_pic);
    }

    @OnClick(R.id.myprofile_btn)
    public void openMyProfile() {

    }

    @OnClick(R.id.history_btn)
    public void openHistory() {
        Intent historyAct = new Intent(getActivity(), MenuOptionsAct.class);
        historyAct.putExtra("fragId", 1);
        startActivity(historyAct);
    }

    @OnClick(R.id.faq_btn)
    public void openFAQ() {
        Intent faqAct = new Intent(getActivity(), MenuOptionsAct.class);
        faqAct.putExtra("fragId", 2);
        startActivity(faqAct);
    }

    @OnClick(R.id.falae_btn)
    public void openFalae() {
        Intent falaeAct = new Intent(getActivity(), MenuOptionsAct.class);
        falaeAct.putExtra("fragId", 3);
        startActivity(falaeAct);
    }

    @OnClick(R.id.termsofuse_btn)
    public void openTermsOfUse() {
        Intent termsOfUseAct = new Intent(getActivity(), MenuOptionsAct.class);
        termsOfUseAct.putExtra("fragId", 4);
        startActivity(termsOfUseAct);
    }

    @OnClick(R.id.about_btn)
    public void openAbout() {
        Intent aboutAct = new Intent(getActivity(), MenuOptionsAct.class);
        aboutAct.putExtra("fragId", 5);
        startActivity(aboutAct);
    }
}
