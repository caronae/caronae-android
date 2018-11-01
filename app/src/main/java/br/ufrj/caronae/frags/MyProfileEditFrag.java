package br.ufrj.caronae.frags;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.Mask;
import com.redmadrobot.inputmask.model.CaretString;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

import br.ufrj.caronae.App;
import br.ufrj.caronae.acts.MediaAct;
import br.ufrj.caronae.customizedviews.CustomDialogClass;
import br.ufrj.caronae.customizedviews.CustomBottomDialogClass;
import br.ufrj.caronae.data.ImageSaver;
import br.ufrj.caronae.R;
import br.ufrj.caronae.customizedviews.RoundedTransformation;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MyProfileAct;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileEditFrag extends Fragment {

    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.profile_tv)
    TextView profile_tv;
    @BindView(R.id.createdAt_tv)
    TextView createdAt_tv;
    @BindView(R.id.changePhotoText)
    TextView changePhoto;
    @BindView(R.id.ridesOffered_tv)
    TextView ridesOffered_tv;
    @BindView(R.id.ridesTaken_tv)
    TextView ridesTaken_tv;

    @BindView(R.id.email_et)
    EditText email_et;
    @BindView(R.id.phoneNumber_et)
    EditText phoneNumber_et;
    @BindView(R.id.location_et)
    EditText location_et;
    @BindView(R.id.carModel_et)
    EditText carModel_et;
    @BindView(R.id.carColor_et)
    EditText carColor_et;
    @BindView(R.id.carPlate_et)
    EditText carPlate_et;

    @BindView(R.id.carOwner_sw)
    SwitchCompat carOwner_sw;

    @BindView(R.id.car_lay)
    RelativeLayout car_lay;
    @BindView(R.id.login_button)
    LoginButton loginButton;
    @BindView(R.id.user_pic)
    ImageView user_pic;

    private CallbackManager callbackManager;
    private String course, profile, profileUrlPic, faceId;

    public MyProfileEditFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile_edit, container, false);
        ButterKnife.bind(this, view);

        callbackManager = ((MyProfileAct) getActivity()).getFbCallbackManager();

        carOwner_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                carOwnerSw();
            }
        });
        InputFilter[] editFilters = carPlate_et.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        carPlate_et.setFilters(newFilters);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("face", "onSuccess = " + loginResult.toString());
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    faceId = profile.getId();
                    User user = App.getUser();
                    user.setFaceId(faceId);
                    CaronaeAPI.service().updateUser(Integer.toString(App.getUser().getDbId()), user)
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Util.debug("SUCESS");
                                        SharedPref.saveUser(user);
                                    } else {
                                        Util.treatResponseFromServer(response);
                                        Util.toast(R.string.facebook_saveid_error);
                                        Util.debug("saveFaceId" + response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Util.toast(R.string.facebook_saveid_error);
                                    Util.debug("saveFaceId" + t.getMessage());
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
                Util.toast(R.string.facebook_login_error);
                Log.e("face", "onError = " + exception.toString());
            }
        });

        setFieldValidatorsListeners();

        User user = App.getUser();
        if (user != null) {
            fillUserFields(user);

            CaronaeAPI.service().getRidesHistory(Integer.toString(user.getDbId()))
                    .enqueue(new Callback<RideHistoryForJson>() {
                        @Override
                        public void onResponse(Call<RideHistoryForJson> call, Response<RideHistoryForJson> response) {

                            if (response.isSuccessful()) {
                                RideHistoryForJson historyRide = response.body();
                                ridesOffered_tv.setText(String.valueOf(historyRide.getRidesHistoryOfferedCount()));
                                ridesTaken_tv.setText(String.valueOf(historyRide.getRidesHistoryTakenCount()));
                                SharedPref.setRidesTaken(String.valueOf(historyRide.getRidesHistoryTakenCount()));
                                SharedPref.setRidesOffered(String.valueOf(historyRide.getRidesHistoryOfferedCount()));
                            } else {
                                if (!SharedPref.getRidesOffered().isEmpty() && !SharedPref.getRidesOffered().equals("missing")) {
                                    ridesOffered_tv.setText(SharedPref.getRidesOffered());
                                    ridesTaken_tv.setText(SharedPref.getRidesTaken());
                                }
                                Util.treatResponseFromServer(response);
                                Log.e("getRidesHistoryCount", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<RideHistoryForJson> call, Throwable t) {
                            if (!SharedPref.getRidesOffered().isEmpty() && !SharedPref.getRidesOffered().equals("missing")) {
                                ridesOffered_tv.setText(SharedPref.getRidesOffered());
                                ridesTaken_tv.setText(SharedPref.getRidesTaken());
                            }
                            Log.e("getRidesHistoryCount", t.getMessage());
                        }
                    });
        }
        setETFormat();
        return view;
    }

    @Override
    public void onStart() {
        if (!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals("")) {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        if (!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals("")) {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        super.onResume();
    }

    private void setFieldValidatorsListeners() {
        phoneNumber_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String phone = phoneNumber_et.getText().toString();
                    if (!validatePhone(phone))
                        phoneNumber_et.setError(getString(R.string.fragment_myprofile_invalid_phone));
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
                        phoneNumber_et.setError(getString(R.string.fragment_myprofile_invalid_phone));
                }
            }
        });

        email_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String phone = email_et.getText().toString();
                    if (!validateMail(phone))
                        email_et.setError(getString(R.string.fragment_myprofile_invalid_email));
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
                        email_et.setError(getString(R.string.fragment_myprofile_invalid_email));
                }
            }
        });

        carPlate_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    if(carPlate_et.getText() != null && !carPlate_et.getText().toString().isEmpty()) {
                        if (!validatePlate(carPlate_et.getText().toString()))
                            carPlate_et.setError(getString(R.string.fragment_myprofile_invalid_plate));
                    }
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
                    if(carPlate_et.getText() != null && !carPlate_et.getText().toString().isEmpty()) {
                        if (!validatePlate(carPlate_et.getText().toString()))
                            carPlate_et.setError(getString(R.string.fragment_myprofile_invalid_plate));
                    }
                }
            }
        });
    }

    public boolean validatePlate(String plate) {
        String brazilianPlateRegex = "^[A-Z]{3}[0-9]{4}$";
        String mercosulPlateRegex = "^(?=(?:.*[0-9]){3})(?=(?:.*[A-Z]){4})[A-Z0-9]{7}$";

        return Pattern.compile(brazilianPlateRegex).matcher(plate).matches() || Pattern.compile(mercosulPlateRegex).matcher(plate).matches();
    }

    private boolean validateMail(String mail) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(mail);
        return m.matches();
    }

    private boolean validatePhone(String phone) {
        if (phone.length() == 16) {
            //(0XX) XXXXX-XXXX
            return true;
        }
        return false;
    }

    private void fillUserFields(User user) {
        car_lay.setVisibility(user.isCarOwner() ? View.VISIBLE : View.GONE);
        name_tv.setText(user.getName());
        profile = user.getProfile();
        course = user.getCourse();
        String info;
        info = profile + " | " + course;
        profileUrlPic = user.getProfilePicUrl();
        profile_tv.setText(info);
        if (!TextUtils.isEmpty(user.getPhoneNumber())) {
            phoneNumber_et.setText(getFormatedNumber(user.getPhoneNumber()));
        }
        email_et.setText(user.getEmail());
        location_et.setText(user.getLocation());
        carOwner_sw.setChecked(user.isCarOwner());
        carModel_et.setText(user.getCarModel());
        carColor_et.setText(user.getCarColor());
        if (user.getCarPlate() != null && !TextUtils.isEmpty(user.getCarPlate())) {
            String bPR = "^[A-Z]{3}-[0-9]{4}$";
            String plate = Pattern.compile(bPR).matcher(user.getCarPlate()).matches() ? user.getCarPlate().substring(0,3) + user.getCarPlate().substring(4) : user.getCarPlate();
            carPlate_et.setText(plate);
        }
        String date = user.getCreatedAt().split(" ")[0];
        date = Util.formatBadDateWithYear(date).substring(3);
        createdAt_tv.setText(date);
        if (SharedPref.getSavedPic()) {
            Bitmap bmp = new ImageSaver(getContext()).
                    setFileName("myProfile.png").
                    setDirectoryName("images").
                    load();
            user_pic.setImageBitmap(bmp);
        } else {
            if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()) {
                Picasso.with(getContext()).load(user.getProfilePicUrl())
                        .placeholder(R.drawable.user_pic)
                        .error(R.drawable.user_pic)
                        .transform(new RoundedTransformation())
                        .into(user_pic);
            }
        }
    }

    @OnClick(R.id.changePhotoText)
    public void userPic() {
        showPhotoOptions();
    }

    @OnClick(R.id.carOwner_sw)
    public void carOwnerSw() {
        car_lay.setVisibility(carOwner_sw.isChecked() ? View.VISIBLE : View.GONE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @OnClick(R.id.location_et)
    public void locationEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Editar Perfil");
        intent.putExtra("selection", "neigh");
        intent.putExtra("allP", false);
        intent.putExtra("otherP", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    public void saveProfileBtn() {
        try {
            ((MyProfileAct) getActivity()).progressBar.getIndeterminateDrawable().setColorFilter(0xFF000000, android.graphics.PorterDuff.Mode.MULTIPLY);
            ((MyProfileAct) getActivity()).edit_bt.setVisibility(View.GONE);
            ((MyProfileAct) getActivity()).progressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("Error:", "Getting null activity: " + e.toString());
        }

        if (App.getUser() == null) {
            return;
        }

        BitmapDrawable bmpDrawable = (BitmapDrawable) user_pic.getDrawable();
        Bitmap bitmap = bmpDrawable.getBitmap();

        final User editedUser = new User();
        prepEditedUser(editedUser);

        int validation = fieldsValidated();
        if (validation == 1) {
            CustomDialogClass cdc = new CustomDialogClass(getActivity(), "MyProfileEdit", this);
            cdc.show();
            cdc.enableOnePositiveOption();
            cdc.setTitleText(getResources().getString(R.string.fragment_myprofileedit_invalid_data));
            cdc.setPButtonText(getResources().getString(R.string.ok_uppercase));
            cdc.setMessageText(getResources().getString(R.string.fragment_myprofileedit_invalid_phone));
            try {
                ((MyProfileAct) getActivity()).progressBar.setVisibility(View.GONE);
                ((MyProfileAct) getActivity()).edit_bt.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("Error:", "Getting null activity: " + e.toString());
            }
            return;
        } else if (validation == 2) {
            CustomDialogClass cdc = new CustomDialogClass(getActivity(), "MyProfileEdit", this);
            cdc.show();
            cdc.enableOnePositiveOption();
            cdc.setTitleText(getResources().getString(R.string.fragment_myprofileedit_invalid_data));
            cdc.setPButtonText(getResources().getString(R.string.ok_uppercase));
            cdc.setMessageText(getResources().getString(R.string.fragment_myprofileedit_invalid_email));
            try {
                ((MyProfileAct) getActivity()).progressBar.setVisibility(View.GONE);
                ((MyProfileAct) getActivity()).edit_bt.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("Error:", "Getting null activity: " + e.toString());
            }
            return;
        } else if (validation == 3) {
            CustomDialogClass cdc = new CustomDialogClass(getActivity(), "MyProfileEdit", this);
            cdc.show();
            cdc.enableOnePositiveOption();
            cdc.setTitleText(getResources().getString(R.string.fragment_myprofileedit_invalid_data));
            cdc.setPButtonText(getResources().getString(R.string.ok_uppercase));
            cdc.setMessageText(getResources().getString(R.string.fragment_myprofileedit_invalid_plate));
            try {
                ((MyProfileAct) getActivity()).progressBar.setVisibility(View.GONE);
                ((MyProfileAct) getActivity()).edit_bt.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("Error:", "Getting null activity: " + e.toString());
            }
            return;
        } else if (validation == 0) {
            if (Util.isNetworkAvailable(getContext())) {
                CaronaeAPI.service().updateUser(String.valueOf(App.getUser().getDbId()), editedUser)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    if (!App.getUser().sameFieldsState(editedUser)) {
                                        User user = App.getUser();
                                        if (user == null) {
                                            return;
                                        }
                                        new ImageSaver(getContext()).
                                                setFileName("myProfile.png").
                                                setDirectoryName("images").
                                                save(bitmap);
                                        SharedPref.setSavedPic(true);
                                        user.setUser(editedUser);
                                        saveProfilePicUrl(profileUrlPic);
                                        SharedPref.saveUser(user);
                                    }
                                    try {
                                        ((MyProfileAct) getActivity()).progressBar.setVisibility(View.GONE);
                                        ((MyProfileAct) getActivity()).edit_bt.setVisibility(View.VISIBLE);
                                        ((MyProfileAct) getActivity()).onSuccessSave();
                                    } catch (Exception e) {
                                        Log.e("Error:", "Getting null activity: " + e.toString());
                                    }
                                } else {
                                    Util.treatResponseFromServer(response);
                                    onErrorUpdatingProfile();
                                    Log.e("updateUser", response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                onErrorUpdatingProfile();
                                Log.e("updateUser", t.getMessage());
                            }
                        });
            } else {
                onErrorUpdatingProfile();
            }
        }
    }

    private int fieldsValidated() {
        String phone = phoneNumber_et.getText().toString();
        String mail = email_et.getText().toString();
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
        String userPhoneNumber, userPlate;
        if (phoneNumber_et.getText().toString().length() == 16) {
            userPhoneNumber = (phoneNumber_et.getText().toString().substring(1, 4)) +
                    (phoneNumber_et.getText().toString().substring(6, 11)) +
                    (phoneNumber_et.getText().toString().substring(12));
        } else {
            userPhoneNumber = phoneNumber_et.getText().toString();
        }
        userPlate = carPlate_et.getText().toString();
        String bPR = "^[A-Z]{3}-[0-9]{4}$";
        String plate = Pattern.compile(bPR).matcher(userPlate).matches() ? userPlate.substring(0,3) + userPlate.substring(4) : userPlate;
        editedUser.setPhoneNumber(userPhoneNumber);//(0XX) XXXXX-XXXX
        editedUser.setEmail(email_et.getText().toString());
        editedUser.setLocation(location_et.getText().toString());
        editedUser.setCarOwner(carOwner_sw.isChecked());
        editedUser.setCarModel(carModel_et.getText().toString());
        editedUser.setCarColor(carColor_et.getText().toString());
        editedUser.setProfilePicUrl(profileUrlPic);
        editedUser.setCarPlate(plate);//AAA-0000
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void saveProfilePicUrl(String profilePicUrl) {
        User editedUser = App.getUser();
        editedUser.setProfilePicUrl(profilePicUrl);
        CaronaeAPI.service().updateUser(Integer.toString(App.getUser().getDbId()), editedUser)
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

    //Use this function to initialize the user input fields format.
    void setETFormat() {
        final MaskedTextChangedListener phoneListener = new MaskedTextChangedListener(
                "({0}[00]) [00000]-[0000]",
                true,
                phoneNumber_et,
                null,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {
                        Log.d(ProfileAct.class.getSimpleName(), extractedValue);
                        Log.d(ProfileAct.class.getSimpleName(), String.valueOf(maskFilled));
                    }
                }
        );
        phoneNumber_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (TextUtils.isEmpty(phoneNumber_et.getText().toString())) {
                    phoneNumber_et.setText("(021) ");
                }
                return false;
            }
        });
        phoneNumber_et.addTextChangedListener(phoneListener);
        phoneNumber_et.setOnFocusChangeListener(phoneListener);



    }

    //Use this function to get user phone number correctly formated
    String getFormatedNumber(String phone) {
        final Mask mask = new Mask("({0}[00]) [00000]-[0000]");
        final String input = phone;
        final Mask.Result result = mask.apply(
                new CaretString(
                        input,
                        input.length()
                ),
                true // you may consider disabling autocompletion for your case
        );
        final String output = result.getFormattedText().getString();
        return output;
    }

    private void showPhotoOptions() {
        CustomBottomDialogClass dialog = new CustomBottomDialogClass(getActivity(), "MyProfileEdit", this);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        dialog.show();

        /*
        CharSequence options[] = new CharSequence[] {"Usar foto do Facebook", "Remover minha foto"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case 0:
                        useFacebookPhoto();
                        break;
                    case 1:
                        removePhoto();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        dialog.show();*/
    }

    public void removePhoto() {
        BitmapDrawable bmpDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.user_pic);
        Bitmap removedPhoto = bmpDrawable.getBitmap();
        user_pic.setImageBitmap(removedPhoto);
        profileUrlPic = "";
    }

    public void changeToMediaAct() {
        Intent intent = new Intent(getActivity(), MediaAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_down_slide_in, R.anim.anim_up_slide_out);
    }

    public void useFacebookPhoto() {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            String faceId = profile.getId();
            String profilePicUrl = "https://graph.facebook.com/" + faceId + "/picture?type=large";
            profileUrlPic = profilePicUrl;

            Picasso.with(getContext()).load(profilePicUrl)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(user_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            BitmapDrawable bmpDrawable = (BitmapDrawable) user_pic.getDrawable();
                            Bitmap bitmap = bmpDrawable.getBitmap();
                            new ImageSaver(getContext()).
                                    setFileName("myProfile.png").
                                    setDirectoryName("images").
                                    save(bitmap);
                            SharedPref.setSavedPic(true);
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            onFacebookPhotoChangeFailed();
        }
    }

    public void onFacebookPhotoChangeFailed() {
        CustomDialogClass customDialogClass = new CustomDialogClass(getActivity(), "MyProfileEdit", this);
        customDialogClass.show();
        customDialogClass.setTitleText(getActivity().getResources().getString(R.string.facebook_error_title));
        customDialogClass.setMessageText(getActivity().getResources().getString(R.string.facebook_error_message));
        customDialogClass.setPButtonText(getActivity().getResources().getString(R.string.ok_uppercase));
        customDialogClass.enableOnePositiveOption();
    }

    public void onErrorUpdatingProfile() {
        Activity activity = getActivity();
        try {
            ((MyProfileAct) activity).progressBar.setVisibility(View.GONE);
            ((MyProfileAct) activity).edit_bt.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("Error:", "Getting null activity: " + e.toString());
        }
        CustomDialogClass customDialogClass = new CustomDialogClass(activity, "MyProfileEdit", this);
        customDialogClass.show();
        customDialogClass.setTitleText(getActivity().getResources().getString(R.string.saving_profile_error_title));
        customDialogClass.setMessageText(getActivity().getResources().getString(R.string.saving_profile_error_message));
        customDialogClass.setPButtonText(getActivity().getResources().getString(R.string.ok_uppercase));
        customDialogClass.enableOnePositiveOption();
    }

}
