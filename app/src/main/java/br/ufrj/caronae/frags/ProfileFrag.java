package br.ufrj.caronae.frags;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.models.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileFrag extends Fragment {

    @Bind(R.id.name_tv)
    TextView name_tv;
    @Bind(R.id.profile_tv)
    TextView profile_tv;
    @Bind(R.id.course_tv)
    TextView course_tv;
    @Bind(R.id.createdAt_tv)
    TextView createdAt_tv;
    @Bind(R.id.email_et)
    EditText email_et;
    @Bind(R.id.phoneNumber_et)
    EditText phoneNumber_et;
    @Bind(R.id.carOwner_sw)
    SwitchCompat carOwner_sw;
    @Bind(R.id.carModel_et)
    EditText carModel_et;
    @Bind(R.id.carColor_et)
    EditText carColor_et;
    @Bind(R.id.carPlate_et)
    EditText carPlate_et;
    @Bind(R.id.car_lay)
    RelativeLayout car_lay;

    public ProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        User user = App.getUser();
        if (user != null) {
            name_tv.setText(user.getName());
            profile_tv.setText(user.getProfile());
            course_tv.setText(user.getCourse());
            phoneNumber_et.setText(user.getPhoneNumber());
            email_et.setText(user.getEmail());
            carOwner_sw.setChecked(user.isCarOwner());
            carModel_et.setText(user.getCarModel());
            carColor_et.setText(user.getCarColor());
            carPlate_et.setText(user.getCarPlate());
            String date = user.getCreatedAt().split(" ")[0];
            try {
                Date date2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
                date = new SimpleDateFormat("dd/MM/yy", Locale.US).format(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            createdAt_tv.setText("Usuário desde " + date);
        }

        carOwnerSw();

        return view;
    }

    @OnClick(R.id.carOwner_sw)
    public void carOwnerSw() {
        car_lay.setVisibility(carOwner_sw.isChecked() ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.profile_tv)
    public void profileTv() {
        final EditText input = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        profile_tv.setText(input.getText());
                    }
                }).show();
    }

    @OnClick(R.id.course_tv)
    public void courseTv() {
        final EditText input = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        course_tv.setText(input.getText());
                    }
                }).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (App.getUser() == null)
            return;

        final User editedUser = new User();
        prepEditedUser(editedUser);

        if (!App.getUser().sameFieldsState(editedUser)) {
            App.getNetworkService().updateUser(editedUser, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    User user = App.getUser();
                    if (user == null)
                        return;
                    user.setUser(editedUser);
                    App.saveUser(user);
                    App.toast("Perfil atualizado");
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("updateUser", error.getMessage());
                    App.toast("Erro ao atualizar usuário");
                }
            });
        }
    }

    private void prepEditedUser(User editedUser) {
        editedUser.setName(name_tv.getText().toString());
        editedUser.setProfile(profile_tv.getText().toString());
        editedUser.setCourse(course_tv.getText().toString());
        editedUser.setPhoneNumber(phoneNumber_et.getText().toString());
        editedUser.setEmail(email_et.getText().toString());
        editedUser.setCarOwner(carOwner_sw.isChecked());
        editedUser.setCarModel(carModel_et.getText().toString());
        editedUser.setCarColor(carColor_et.getText().toString());
        editedUser.setCarPlate(carPlate_et.getText().toString());
    }
}
