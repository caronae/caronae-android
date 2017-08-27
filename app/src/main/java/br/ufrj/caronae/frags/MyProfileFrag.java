package br.ufrj.caronae.frags;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import com.rey.material.app.Dialog;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public MyProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, view);

        callbackManager = ((MainAct) getActivity()).getFbCallbackManager();

        carOwner_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                carOwnerSw();
            }
        });

        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("face", "onSuccess = " + loginResult.toString());

                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    final String faceId = profile.getId();
                    App.getNetworkService(getContext()).saveFaceId(new IdForJson(faceId))
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Log.i("saveFaceId", "face id saved");
                                        User user = App.getUser();
                                        user.setFaceId(faceId);
                                        SharedPref.saveUser(user);
                                    } else {
                                        Util.treatResponseFromServer(response);
                                        Util.toast(R.string.frag_myprofile_errorSaveFaceId);
                                        Log.e("saveFaceId", response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Util.toast(R.string.frag_myprofile_errorSaveFaceId);
                                    Log.e("saveFaceId", t.getMessage());
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

        setFieldValidatorsListeners();

        User user = App.getUser();
        if (user != null) {
            fillUserFields(user);

            App.getNetworkService(getContext()).getRidesHistoryCount(user.getDbId() + "")
                    .enqueue(new Callback<HistoryRideCountForJson>() {
                        @Override
                        public void onResponse(Call<HistoryRideCountForJson> call, Response<HistoryRideCountForJson> response) {

                            if (response.isSuccessful()) {
                                HistoryRideCountForJson historyRideCountForJson = response.body();
                                ridesOffered_tv.setText(String.valueOf(historyRideCountForJson.getOfferedCount()));
                                ridesTaken_tv.setText(String.valueOf(historyRideCountForJson.getTakenCount()));
                            } else {
                                Util.treatResponseFromServer(response);
                                Util.toast(R.string.act_profile_errorCountRidesHistory);
                                Log.e("getRidesHistoryCount", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<HistoryRideCountForJson> call, Throwable t) {
                            Util.toast(R.string.act_profile_errorCountRidesHistory);
                            Log.e("getRidesHistoryCount", t.getMessage());
                        }
                    });
        }

        return view;
    }

    private void setFieldValidatorsListeners() {
        phoneNumber_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String phone = phoneNumber_et.getText().toString();
                    if (!validatePhone(phone))
                        phoneNumber_et.setError(getString(R.string.frag_myprofile_invalidPhone));
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
                        phoneNumber_et.setError(getString(R.string.frag_myprofile_invalidPhone));
                }
            }
        });

        email_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String phone = email_et.getText().toString();
                    if (!validateMail(phone))
                        email_et.setError(getString(R.string.frag_myprofile_invalidMail));
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
                        email_et.setError(getString(R.string.frag_myprofile_invalidMail));
                }
            }
        });

        carPlate_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String plate = carPlate_et.getText().toString();
                    if (!validatePlate(plate))
                        carPlate_et.setError(getString(R.string.frag_myprofile_invalidPlate));
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
                        carPlate_et.setError(getString(R.string.frag_myprofile_invalidPlate));
                }
            }
        });
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
        if (phone.length() == 12 || phone.length() == 11) {
            for (int i = 0; i < phone.length(); i++) {
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
                    .transform(new RoundedTransformation())
                    .into(user_pic);
    }

    @OnClick(R.id.user_pic)
    public void userPic() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                final User user = App.getUser();

                if (getSelectedValue().toString().equals(getString(R.string.frag_myprofile_facePicChoice))) {
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        String faceId = profile.getId();
                        String profilePicUrl = "https://graph.facebook.com/" + faceId + "/picture?type=large";
//                        if (user.getProfilePicUrl() == null || !user.getProfilePicUrl().equals(profilePicUrl)) {
                            user.setProfilePicUrl(profilePicUrl);

                            Picasso.with(getContext()).load(profilePicUrl)
                                    .error(R.drawable.auth_bg)
                                    .transform(new RoundedTransformation())
                                    .into(user_pic);

                            saveProfilePicUrl(profilePicUrl);
//                        }
                    } else {
                        Util.toast(R.string.frag_myprofile_facePickChoiceNotOnFace);
                    }
                } else {
                    App.getNetworkService(getContext()).getIntranetPhotoUrl()
                            .enqueue(new Callback<UrlForJson>() {
                                @Override
                                public void onResponse(Call<UrlForJson> call, Response<UrlForJson> response) {
                                    if (response.isSuccessful()) {
                                        UrlForJson urlForJson = response.body();
                                        if (urlForJson == null)
                                            return;

                                        String profilePicUrl = urlForJson.getUrl();
                                        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                            user.setProfilePicUrl(profilePicUrl);
                                            Picasso.with(getContext()).load(profilePicUrl)
                                                    .error(R.drawable.user_pic)
                                                    .transform(new RoundedTransformation())
                                                    .into(user_pic);

                                            saveProfilePicUrl(profilePicUrl);
                                        }
                                    } else {
                                        Util.treatResponseFromServer(response);
                                        Util.toast(R.string.frag_myprofile_errorGetIntranetPhoto);
                                        Log.e("getIntranetPhotoUrl", response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<UrlForJson> call, Throwable t) {
                                    Util.toast(R.string.frag_myprofile_errorGetIntranetPhoto);
                                    Log.e("getIntranetPhotoUrl", t.getMessage());
                                }
                            });

                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

//        builder.items(new String[]{getString(R.string.frag_myprofile_SigaPicChoice), getString(R.string.frag_myprofile_facePicChoice)}, 0)
        builder.items(new String[]{getString(R.string.frag_myprofile_facePicChoice)}, 0)
//                .title(getString(R.string.frag_myprofile_picChoice))
                .title("Usar foto do Facebook?")
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.logout_iv)
    public void logoutIv() {
        new LogOut(getContext()).execute();
        startActivity(new Intent(getContext(), LoginAct.class));
        getActivity().finish();
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
                String selectedZone = getSelectedValue().toString();
                if (selectedZone.equals("Outros")) {
                    showOtherNeighborhoodDialog();
                } else {
                    locationEt2(selectedZone);
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(Util.getZones(), 0)
                .title("Escolha sua zona")
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    public void showOtherNeighborhoodDialog() {
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                android.widget.EditText neighborhood_et = (android.widget.EditText) fragment.getDialog().findViewById(R.id.neighborhood_et);
                String neighborhood = neighborhood_et.getText().toString();
                if (!neighborhood.isEmpty()) {
                    location_et.setText(neighborhood);
                }

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.title(getActivity().getString(R.string.frag_ridesearch_typeNeighborhood))
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.cancel))
                .contentView(R.layout.other_neighborhood);

        DialogFragment fragment2 = DialogFragment.newInstance(builder);
        fragment2.show(getActivity().getSupportFragmentManager(), null);
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
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.save_profile_btn)
    public void saveProfileBtn() {
        if (App.getUser() == null)
            return;

        final User editedUser = new User();
        prepEditedUser(editedUser);

        if (!App.getUser().sameFieldsState(editedUser)) {
            int validation = fieldsValidated();
            if (validation == 0) {
                App.getNetworkService(getContext()).updateUser(editedUser)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    User user = App.getUser();
                                    if (user == null)
                                        return;
                                    user.setUser(editedUser);
                                    SharedPref.saveUser(user);
                                    Util.toast(R.string.frag_myprofile_updated);
                                } else {
                                    Util.treatResponseFromServer(response);
                                    Log.e("updateUser", response.message());
                                    Util.toast(R.string.frag_myprofile_errorUpdated);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e("updateUser", t.getMessage());
                                Util.toast(R.string.frag_myprofile_errorUpdated);
                            }
                        });
            } else if (validation == 1) {
                Util.toast(getString(R.string.frag_myprofile_invalidFieldsTelephone));
            } else if (validation == 2) {
                Util.toast(getString(R.string.frag_myprofile_invalidFieldsEmail));
            } else if (validation == 3) {
                Util.toast(getString(R.string.frag_myprofile_invalidFieldsPlate));
            }
        } else {
            Util.toast(getString(R.string.frag_myprofile_SameFieldsState));
        }
    }

    private int fieldsValidated() {
        String phone = phoneNumber_et.getText().toString();
        String mail = email_et.getText().toString();
        boolean result = validatePhone(phone) && validateMail(mail);
        if (!validatePhone(phone))
            return 1;
        if (!validateMail(mail))
            return 2;
        if (carOwner_sw.isChecked()) {
            String plate = carPlate_et.getText().toString();
            if (!validatePlate(plate))
                return 3;
        }

        return 0;
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

    public void saveProfilePicUrl(String profilePicUrl) {
        App.getNetworkService(getContext()).saveProfilePicUrl(new UrlForJson(profilePicUrl))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.i("saveProfilePicUrl", "profile pic url saved");
                            SharedPref.saveUser(App.getUser());
                        } else { //need to save it later
                            Util.treatResponseFromServer(response);
                            Log.e("saveProfilePicUrl", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { //need to save it later
                        Log.e("saveProfilePicUrl", t.getMessage());
                    }
                });

    }
}
