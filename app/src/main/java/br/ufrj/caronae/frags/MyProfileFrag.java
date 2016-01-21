package br.ufrj.caronae.frags;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.LoginAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.asyncs.LogOut;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.HistoryRideCountForJson;
import br.ufrj.caronae.models.modelsforjson.IdForJson;
import br.ufrj.caronae.models.modelsforjson.UrlForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyProfileFrag extends Fragment {

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
    android.widget.EditText location_et;
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
    @Bind(R.id.login_button)
    LoginButton loginButton;
    @Bind(R.id.user_pic)
    ImageView user_pic;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.ridesOffered_tv)
    TextView ridesOffered_tv;
    @Bind(R.id.ridesTaken_tv)
    TextView ridesTaken_tv;

    private CallbackManager callbackManager;
    private boolean logOut = false;

    public MyProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, view);

        callbackManager = ((MainAct) getActivity()).getFbCallbackManager();

        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("face", "onSuccess = " + loginResult.toString());

                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    String faceId = profile.getId();
                    App.getNetworkService().saveFaceId(new IdForJson(faceId), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            Log.i("saveFaceId", "face id saved");
                        }

                        @Override
                        public void failure(RetrofitError error) {//need to save id later
                            Util.toast(R.string.frag_myprofile_errorSaveFaceId);

                            Log.e("saveFaceId", error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancel() {
                Log.i("face", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Util.toast(R.string.frag_myprofile_errorFaceLogin);
                Log.e("face", "onError = " + exception.toString());
            }
        });

        phoneNumber_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String phone = phoneNumber_et.getText().toString();
                    if (!validatePhone(phone))
                        phoneNumber_et.setError(getActivity().getString(R.string.frag_myprofile_invalidPhone));
                }

                return false;
            }

        });
        phoneNumber_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phoneNumber_et.setError(null);
                } else {
                    String phone = phoneNumber_et.getText().toString();
                    if (!validatePhone(phone))
                        phoneNumber_et.setError(getActivity().getString(R.string.frag_myprofile_invalidPhone));
                }
            }
        });

        email_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String phone = email_et.getText().toString();
                    if (!validateMail(phone))
                        email_et.setError(getActivity().getString(R.string.frag_myprofile_invalidMail));
                }

                return false;
            }

        });
        email_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    email_et.setError(null);
                } else {
                    String phone = email_et.getText().toString();
                    if (!validateMail(phone))
                        email_et.setError(getActivity().getString(R.string.frag_myprofile_invalidMail));
                }
            }
        });

        carPlate_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String plate = carPlate_et.getText().toString();
                    if (!validatePlate(plate))
                        carPlate_et.setError(getActivity().getString(R.string.frag_myprofile_invalidPlate));
                }

                return false;
            }

        });
        carPlate_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    carPlate_et.setError(null);
                } else {
                    String plate = carPlate_et.getText().toString();
                    if (!validatePlate(plate))
                        carPlate_et.setError(getActivity().getString(R.string.frag_myprofile_invalidPlate));
                }
            }
        });

        User user = App.getUser();
        if (user != null) {
            fillUserFields(user);

            App.getNetworkService().getRidesHistoryCount(user.getDbId() + "", new Callback<HistoryRideCountForJson>() {
                @Override
                public void success(HistoryRideCountForJson historyRideCountForJson, Response response) {
                    ridesOffered_tv.setText(String.valueOf(historyRideCountForJson.getOfferedCount()));
                    ridesTaken_tv.setText(String.valueOf(historyRideCountForJson.getTakenCount()));
                }

                @Override
                public void failure(RetrofitError error) {
                    Util.toast(R.string.act_profile_errorCountRidesHistory);
                }
            });
        }

        return view;
    }

    private boolean validatePlate(String plate) {
        return plate.length() == 7 &&
                Character.isLetter(plate.charAt(0)) &&
                Character.isLetter(plate.charAt(1)) &&
                Character.isLetter(plate.charAt(2)) &&
                Character.isDigit(plate.charAt(3)) &&
                Character.isDigit(plate.charAt(4)) &&
                Character.isDigit(plate.charAt(5)) &&
                Character.isDigit(plate.charAt(6));
    }

    private boolean validateMail(String mail) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(mail);
        return m.matches();
    }

    private boolean validatePhone(String phone) {
        if (phone.length() == 12) {
            for (int i = 0; i < phone.length() && i < 12; i++) {
                if (!Character.isDigit(phone.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private void fillUserFields(User user) {
        car_lay.setVisibility(user.isCarOwner() ? View.VISIBLE : View.GONE);
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
        date = Util.formatBadDateWithYear(date);
        createdAt_tv.setText(date);

        String notifOn = SharedPref.getNotifPref();
        notif_sw.setChecked(notifOn.equals("true"));

        if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty())
            Picasso.with(getContext()).load(user.getProfilePicUrl())
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation(0))
                    .into(user_pic);
    }

    @OnClick(R.id.user_pic)
    public void userPic() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                User user = App.getUser();
                if (getSelectedValue().toString().equals("Foto do Facebook")) {
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        String faceId = profile.getId();
                        String profilePicUrl = "http://graph.facebook.com/" + faceId + "/picture?type=large";
                        if (user.getProfilePicUrl() == null || !user.getProfilePicUrl().equals(profilePicUrl)) {
                            user.setProfilePicUrl(profilePicUrl);
                            Picasso.with(getContext()).load(profilePicUrl)
                                    .error(R.drawable.user_pic)
                                    .transform(new RoundedTransformation(0))
                                    .into(user_pic);
                            App.getNetworkService().saveProfilePicUrl(new UrlForJson(profilePicUrl), new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    Log.i("saveProfilePicUrl", "profile pic url saved");
                                    SharedPref.saveUser(App.getUser());
                                }

                                @Override
                                public void failure(RetrofitError error) {//need to save it later
                                    Log.e("saveProfilePicUrl", error.getMessage());
                                }
                            });
                        }
                    } else {
                        Util.toast(R.string.frag_myprofile_facePickChoiceNotOnFace);
                    }
                } else {
                    String profilePicUrl = "";
                    if (user.getProfilePicUrl() == null || !user.getProfilePicUrl().equals(profilePicUrl)) {
                        user.setProfilePicUrl(profilePicUrl);
                        Picasso.with(getContext()).load(R.drawable.user_pic)
                                .error(R.drawable.user_pic)
                                .into(user_pic);
                        App.getNetworkService().saveProfilePicUrl(new UrlForJson(profilePicUrl), new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                Log.i("saveProfilePicUrl", "profile pic url saved");
                                SharedPref.saveUser(App.getUser());
                            }

                            @Override
                            public void failure(RetrofitError error) {//need to save it later
                                Log.e("saveProfilePicUrl", error.getMessage());
                            }
                        });
                    }
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(new String[]{getContext().getString(R.string.frag_myprofile_SigaPicChoice), getContext().getString(R.string.frag_myprofile_facePicChoice)}, 0)
                .title(getContext().getString(R.string.frag_myprofile_picChoice))
                .positiveAction(getContext().getString(R.string.ok))
                .negativeAction(getContext().getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.logout_iv)
    public void logoutIv() {
        new LogOut().execute();
        startActivity(new Intent(getContext(), LoginAct.class));
        getActivity().finish();
        logOut = true;
    }

    @OnClick(R.id.carOwner_sw)
    public void carOwnerSw() {
        car_lay.setVisibility(carOwner_sw.isChecked() ? View.VISIBLE : View.GONE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, scrollView.getBottom());
            }
        });
    }

    @OnClick(R.id.notif_sw)
    public void notif_sw() {
        SharedPref.saveNotifPref(notif_sw.isChecked() ? "true" : "false");
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
        final android.widget.EditText input = new android.widget.EditText(getActivity());
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
        final android.widget.EditText input = new android.widget.EditText(getActivity());
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
        if (logOut || App.getUser() == null)
            return;

        final User editedUser = new User();
        prepEditedUser(editedUser);

        if (!App.getUser().sameFieldsState(editedUser) && fieldsValidated()) {
            App.getNetworkService().updateUser(editedUser, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    User user = App.getUser();
                    if (user == null)
                        return;
                    user.setUser(editedUser);
                    SharedPref.saveUser(user);
                    Util.toast(R.string.frag_myprofile_updated);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("updateUser", error.getMessage());
                    Util.toast(R.string.frag_myprofile_errorUpdated);
                }
            });
        }
    }

    private boolean fieldsValidated() {
        String phone = phoneNumber_et.getText().toString();
        String mail = email_et.getText().toString();
        boolean result = validatePhone(phone) && validateMail(mail);
        if (!result) {
            return false;
        }
        if (carOwner_sw.isChecked()) {
            String plate = carPlate_et.getText().toString();
            return validatePlate(plate);
        }

        return true;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
