package br.ufrj.caronae.frags;

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
import br.ufrj.caronae.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionsMenuFrag extends Fragment {

    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.profile_tv)
    TextView profile_tv;
    @BindView(R.id.user_pic)
    ImageView user_pic;

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
}
