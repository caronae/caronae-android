package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.ufrj.caronae.App;
import br.ufrj.caronae.BuildConfig;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FalaeFrag extends Fragment {

    @Bind(R.id.user_pic_iv)
    ImageView user_pic_iv;
    @Bind(R.id.name_tv)
    TextView name_tv;
    @Bind(R.id.profile_tv)
    TextView profile_tv;
    @Bind(R.id.course_tv)
    TextView course_tv;
    @Bind(R.id.subject_et)
    EditText subject_et;
    @Bind(R.id.message_et)
    EditText message_et;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;

    public FalaeFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_falae, container, false);
        ButterKnife.bind(this, view);

        User user = App.getUser();
        name_tv.setText(user.getName());
        profile_tv.setText(user.getProfile());
        course_tv.setText(user.getCourse());
        String profilePicUrl = user.getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty())
            Picasso.with(getContext()).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(user_pic_iv);

        return view;
    }

    @OnClick(R.id.send_bt)
    public void sendBt() {
        String message = message_et.getText().toString();
        if (message.isEmpty()) {
            Util.toast(getActivity().getString(R.string.frag_falae_msgblank));
            return;
        }
        message = message.concat("\n\nPlataforma: Android\nVersão do App: " + BuildConfig.VERSION_NAME);

        int checkedId = radioGroup.getCheckedRadioButtonId();
        String subject = "";
        switch (checkedId) {
            case R.id.suggestion_rb:
                subject = getActivity().getString(R.string.frag_falae_suggestionRb);
                break;
            case R.id.question_rb:
                subject = getActivity().getString(R.string.frag_falae_questionRb);
                break;
            case R.id.report_rb:
                subject = getActivity().getString(R.string.frag_falae_reportRb);
                break;
        }
        subject = subject.concat(subject_et.getText().toString());

        final ProgressDialog pd = ProgressDialog.show(getContext(), "", getString(R.string.wait), true, true);
        App.getNetworkService(getContext()).falaeSendMessage(new FalaeMsgForJson(subject, message))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            pd.dismiss();
                            Util.toast(getActivity().getString(R.string.frag_falae_thanksSent));
                            subject_et.setText("");
                            message_et.setText("");
                            Log.i("falaeSendMessage", "falae message sent succesfully");
                        } else {
                            pd.dismiss();
                            Util.toast(getActivity().getString(R.string.frag_falae_errorSent));
                            Log.e("falaeSendMessage", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pd.dismiss();
                        Util.toast(getActivity().getString(R.string.frag_falae_errorSent));
                        Log.e("falaeSendMessage", t.getMessage());
                    }
                });
    }
}
