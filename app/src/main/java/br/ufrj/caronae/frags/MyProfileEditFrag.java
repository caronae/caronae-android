package br.ufrj.caronae.frags;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
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
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.squareup.picasso.Picasso;

import java.net.URL;

import br.ufrj.caronae.App;
import br.ufrj.caronae.CustomDialogClass;
import br.ufrj.caronae.ImageSaver;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.FalaeAct;
import br.ufrj.caronae.acts.MyProfileAct;
import br.ufrj.caronae.acts.ProfileAct;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.HistoryRideCountForJson;
import br.ufrj.caronae.models.modelsforjson.IdForJson;
import br.ufrj.caronae.models.modelsforjson.UrlForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;

public class MyProfileEditFrag extends Fragment {

    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.profile_tv)
    TextView profile_tv;
    @BindView(R.id.createdAt_tv)
    TextView createdAt_tv;
    @BindView(R.id.email_et)
    android.widget.EditText email_et;
    @BindView(R.id.phoneNumber_et)
    android.widget.EditText phoneNumber_et;
    @BindView(R.id.location_et)
    android.widget.EditText location_et;
    @BindView(R.id.carOwner_sw)
    SwitchCompat carOwner_sw;
    @BindView(R.id.carModel_et)
    android.widget.EditText carModel_et;
    @BindView(R.id.carColor_et)
    android.widget.EditText carColor_et;
    @BindView(R.id.carPlate_et)
    android.widget.EditText carPlate_et;
    @BindView(R.id.car_lay)
    RelativeLayout car_lay;
    @BindView(R.id.login_button)
    LoginButton loginButton;
    @BindView(R.id.user_pic)
    ImageView user_pic;
    @BindView(R.id.changePhotoText)
    TextView changePhoto;
    @BindView(R.id.ridesOffered_tv)
    TextView ridesOffered_tv;
    @BindView(R.id.ridesTaken_tv)
    TextView ridesTaken_tv;

    private CallbackManager callbackManager;
    private String course, profile, profileUrlPic;

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
        carPlate_et.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("face", "onSuccess = " + loginResult.toString());

                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    final String faceId = profile.getId();
                    CaronaeAPI.service(getContext()).saveFaceId(new IdForJson(faceId))
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

            CaronaeAPI.service(getContext()).getRidesHistoryCount(user.getDbId() + "")
                    .enqueue(new Callback<HistoryRideCountForJson>() {
                        @Override
                        public void onResponse(Call<HistoryRideCountForJson> call, Response<HistoryRideCountForJson> response) {

                            if (response.isSuccessful()) {
                                HistoryRideCountForJson historyRideCountForJson = response.body();
                                ridesOffered_tv.setText(String.valueOf(historyRideCountForJson.getOfferedCount()));
                                ridesTaken_tv.setText(String.valueOf(historyRideCountForJson.getTakenCount()));
                                SharedPref.setRidesTaken(String.valueOf(historyRideCountForJson.getTakenCount()));
                                SharedPref.setRidesOffered(String.valueOf(historyRideCountForJson.getOfferedCount()));
                            } else {
                                if(!SharedPref.getRidesOffered().isEmpty() && !SharedPref.getRidesOffered().equals("missing")) {
                                    ridesOffered_tv.setText(SharedPref.getRidesOffered());
                                    ridesTaken_tv.setText(SharedPref.getRidesTaken());
                                }
                                Util.treatResponseFromServer(response);
                                Log.e("getRidesHistoryCount", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<HistoryRideCountForJson> call, Throwable t) {
                            if(!SharedPref.getRidesOffered().isEmpty() && !SharedPref.getRidesOffered().equals("missing")) {
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
        //AAA-0000
        return plate.length() == 8 &&
                Character.isLetter(plate.charAt(0)) &&
                Character.isLetter(plate.charAt(1)) &&
                Character.isLetter(plate.charAt(2)) &&
                plate.charAt(3) == '-' &&
                Character.isDigit(plate.charAt(4)) &&
                Character.isDigit(plate.charAt(5)) &&
                Character.isDigit(plate.charAt(6)) &&
                Character.isDigit(plate.charAt(7));
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
        if (!TextUtils.isEmpty(user.getCarPlate())) {
            carPlate_et.setText(getFormatedPlate(user.getCarPlate()));
        }
        String date = user.getCreatedAt().split(" ")[0];
        date = Util.formatBadDateWithYear(date).substring(3);
        createdAt_tv.setText(date);
        if(SharedPref.getSavedPic())
        {
                Bitmap bmp = new ImageSaver(getContext()).
                    setFileName("myProfile.png").
                    setDirectoryName("images").
                    load();
                user_pic.setImageBitmap(bmp);
        }
        else
        {
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

    public void saveProfileBtn() {
        try {
            ((MyProfileAct) getActivity()).progressBar.getIndeterminateDrawable().setColorFilter(0xFF000000, android.graphics.PorterDuff.Mode.MULTIPLY);
            ((MyProfileAct) getActivity()).edit_bt.setVisibility(View.GONE);
            ((MyProfileAct) getActivity()).progressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) { Log.e("Error:", "Getting null activity: " + e.toString());}

        if (App.getUser() == null) {
            return;
        }

        BitmapDrawable bmpDrawable = (BitmapDrawable)user_pic.getDrawable();
        Bitmap bitmap = bmpDrawable.getBitmap();

        final User editedUser = new User();
        prepEditedUser(editedUser);

        int validation = fieldsValidated();
        if (validation == 1) {
            Util.toast(getString(R.string.frag_myprofile_invalidFieldsTelephone));
            return;
        } else if (validation == 2) {
            Util.toast(getString(R.string.frag_myprofile_invalidFieldsEmail));
            return;
        } else if (validation == 3) {
            Util.toast(getString(R.string.frag_myprofile_invalidFieldsPlate));
            return;
        } else if (validation == 0) {
            if(Util.isNetworkAvailable(getContext())) {
                CaronaeAPI.service(getContext()).updateUser(String.valueOf(App.getUser().getDbId()), editedUser)
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
            }
            else
            {
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
        if(phoneNumber_et.getText().toString().length() == 16)
        {
            userPhoneNumber = (phoneNumber_et.getText().toString().substring(1,4)) +
                    (phoneNumber_et.getText().toString().substring(6,11)) +
                    (phoneNumber_et.getText().toString().substring(12));
        }
        else
        {
            userPhoneNumber = phoneNumber_et.getText().toString();
        }
        if(carPlate_et.getText().toString().length() == 8)
        {
            userPlate = (carPlate_et.getText().toString().substring(0,3).toUpperCase()) +
                    (carPlate_et.getText().toString().substring(4));
        }
        else
        {
            userPlate = carPlate_et.getText().toString();
        }
        editedUser.setName(name_tv.getText().toString());
        editedUser.setPhoneNumber(userPhoneNumber);//(0XX) XXXXX-XXXX
        if(!course.isEmpty()) {
            editedUser.setCourse(course);
        }
        if(!profile.isEmpty())
        {
            editedUser.setProfile(profile);
        }
        editedUser.setEmail(email_et.getText().toString());
        editedUser.setLocation(location_et.getText().toString());
        editedUser.setCarOwner(carOwner_sw.isChecked());
        editedUser.setCarModel(carModel_et.getText().toString());
        editedUser.setCarColor(carColor_et.getText().toString());
        editedUser.setProfilePicUrl(profileUrlPic);
        editedUser.setCarPlate(userPlate);//AAA-0000
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void saveProfilePicUrl(String profilePicUrl) {
        CaronaeAPI.service(getContext()).saveProfilePicUrl(new UrlForJson(profilePicUrl))
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
    void setETFormat()
    {
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
                if(TextUtils.isEmpty(phoneNumber_et.getText().toString()))
                {
                    phoneNumber_et.setText("(021) ");
                }
                return false;
            }
        });
        phoneNumber_et.addTextChangedListener(phoneListener);
        phoneNumber_et.setOnFocusChangeListener(phoneListener);


        final MaskedTextChangedListener plateListener = new MaskedTextChangedListener(
                "[AAA]{-}[0000]",
                true,
                carPlate_et,
                null,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {
                        Log.d(ProfileAct.class.getSimpleName(), extractedValue);
                        Log.d(ProfileAct.class.getSimpleName(), String.valueOf(maskFilled));
                    }
                }
        );

        carPlate_et.addTextChangedListener(plateListener);
        carPlate_et.setOnFocusChangeListener(plateListener);
    }

    //Use this function to get user phone number correctly formated
    String getFormatedNumber(String phone)
    {
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
    //Use this function to get user car plate correctly formated
    String getFormatedPlate(String plate)
    {
        final Mask mask = new Mask("[AAA]-[0000]");
        final String input = plate;
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

    private void showPhotoOptions()
    {
        final User user = App.getUser();
        CharSequence options[] = new CharSequence[] {"Usar foto do Facebook", "Remover minha foto"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case 0:
                        Profile profile = Profile.getCurrentProfile();
                        if (profile != null) {
                            String faceId = profile.getId();
                            String profilePicUrl = "https://graph.facebook.com/" + faceId + "/picture?type=large";
                            profileUrlPic = profilePicUrl;

                            Picasso.with(getContext()).load(profilePicUrl)
                                    .error(R.drawable.auth_bg)
                                    .transform(new RoundedTransformation())
                                    .into(user_pic, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            BitmapDrawable bmpDrawable = (BitmapDrawable)user_pic.getDrawable();
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
                        }
                        else {
                            onFacebookPhotoChangeFailed();
                        }
                        break;
                    case 1:
                        BitmapDrawable bmpDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.user_pic);
                        Bitmap removedPhoto = bmpDrawable.getBitmap();
                        user_pic.setImageBitmap(removedPhoto);
                        profileUrlPic = "";
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        dialog.show();
    }

    public void onFacebookPhotoChangeFailed()
    {
        CustomDialogClass customDialogClass = new CustomDialogClass(getActivity(), "MyProfileEdit", this);
        customDialogClass.show();
        customDialogClass.setTitleText(getActivity().getResources().getString(R.string.facebook_error_title));
        customDialogClass.setMessageText(getActivity().getResources().getString(R.string.facebook_error_message));
        customDialogClass.setPButtonText(getActivity().getResources().getString(R.string.ok));
        customDialogClass.enableOnePositiveOption();
    }

    public void onErrorUpdatingProfile()
    {
        Activity activity = getActivity();
        try {
            ((MyProfileAct) activity).progressBar.setVisibility(View.GONE);
            ((MyProfileAct) activity).edit_bt.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            Log.e("Error:", "Getting null activity: " + e.toString());
        }
        CustomDialogClass customDialogClass = new CustomDialogClass(activity, "MyProfileEdit", this);
        customDialogClass.show();
        customDialogClass.setTitleText(getActivity().getResources().getString(R.string.saving_profile_error_title));
        customDialogClass.setMessageText(getActivity().getResources().getString(R.string.saving_profile_error_message));
        customDialogClass.setPButtonText(getActivity().getResources().getString(R.string.ok));
        customDialogClass.enableOnePositiveOption();
    }

}
