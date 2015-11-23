package br.ufrj.caronae.frags;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.LoginAct;
import br.ufrj.caronae.asyncs.LogOut;
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
    @Bind(R.id.location_et)
    EditText location_et;
    @Bind(R.id.carOwner_sw)
    SwitchCompat carOwner_sw;
    @Bind(R.id.notif_sw)
    SwitchCompat notif_sw;
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
            location_et.setText(user.getLocation());
            carOwner_sw.setChecked(user.isCarOwner());
            carModel_et.setText(user.getCarModel());
            carColor_et.setText(user.getCarColor());
            carPlate_et.setText(user.getCarPlate());
            String date = user.getCreatedAt().split(" ")[0];
            try {
                Date date2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            createdAt_tv.setText("Usuário desde " + date);

            String notifOn = App.getPref(App.NOTIFICATIONS_ON_PREF_KEY);
            notif_sw.setChecked(notifOn.equals("true"));
        }

        carOwnerSw();

        return view;
    }

    @OnClick(R.id.logout_iv)
    public void logoutIv() {
        new LogOut().execute();
        startActivity(new Intent(getContext(), LoginAct.class));
        getActivity().finish();
    }

    @OnClick(R.id.carOwner_sw)
    public void carOwnerSw() {
        car_lay.setVisibility(carOwner_sw.isChecked() ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.notif_sw)
    public void notif_sw() {
        App.putPref(App.NOTIFICATIONS_ON_PREF_KEY, notif_sw.isChecked() ? "true" : "false");
    }

    @OnClick(R.id.location_et)
    public void locationEt() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                locationEt2(getSelectedValue().toString());
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(Util.getZones(), 0)
                .title("Escolha sua zona")
                .positiveAction("OK")
                .negativeAction("Cancelar");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    public void locationEt2(String zone) {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                location_et.setText(getSelectedValue());
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(Util.getNeighborhoods(zone), 0)
                .title("Escolha seu bairro")
                .positiveAction("OK")
                .negativeAction("Cancelar");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.profile_tv)
    public void profileTv() {
        final EditText input = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String text = input.getText().toString();
                        profile_tv.setText(text.isEmpty() ? "Perfil padrão" : text);
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
                        String text = input.getText().toString();
                        course_tv.setText(text.isEmpty() ? "Curso padrão" : text);
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
                    Util.toast("Perfil atualizado");
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("updateUser", error.getMessage());
                    Util.toast("Erro ao atualizar usuário");
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
        editedUser.setLocation(location_et.getText().toString());
        editedUser.setCarOwner(carOwner_sw.isChecked());
        editedUser.setCarModel(carModel_et.getText().toString());
        editedUser.setCarColor(carColor_et.getText().toString());
        editedUser.setCarPlate(carPlate_et.getText().toString());
    }
}
