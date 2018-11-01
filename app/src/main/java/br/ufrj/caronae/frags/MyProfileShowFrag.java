package br.ufrj.caronae.frags;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.redmadrobot.inputmask.helper.Mask;
import com.redmadrobot.inputmask.model.CaretString;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

import br.ufrj.caronae.App;
import br.ufrj.caronae.customizedviews.CustomDialogClass;
import br.ufrj.caronae.customizedviews.CustomPhoneDialogClass;
import br.ufrj.caronae.data.ImageSaver;
import br.ufrj.caronae.R;
import br.ufrj.caronae.customizedviews.RoundedTransformation;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.LoginAct;
import br.ufrj.caronae.asyncs.LogOut;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MyProfileShowFrag extends Fragment {

    @BindView(R.id.user_pic)
    ImageView user_pic;
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.profile_tv)
    TextView profile_tv;
    @BindView(R.id.phone_tv)
    TextView phone_tv;
    @BindView(R.id.createdAt_tv)
    TextView createdAt_tv;
    @BindView(R.id.ridesOffered_tv)
    TextView ridesOffered_tv;
    @BindView(R.id.ridesTaken_tv)
    TextView ridesTaken_tv;
    @BindView(R.id.carPlate_tv)
    TextView carPlate_tv;
    @BindView(R.id.carModel_tv)
    TextView carModel_tv;
    @BindView(R.id.carColor_tv)
    TextView carColor_tv;

    User user;

    public MyProfileShowFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile_show, container, false);
        ButterKnife.bind(this, view);
        user = App.getUser();
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
                                    fillUserFields(user);
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
                            public void onFailure(Call<RideHistoryForJson> call, Throwable t) {
                                if(!SharedPref.getRidesOffered().isEmpty() && !SharedPref.getRidesOffered().equals("missing")) {
                                    ridesOffered_tv.setText(SharedPref.getRidesOffered());
                                    ridesTaken_tv.setText(SharedPref.getRidesTaken());
                                }
                                Log.e("getRidesHistoryCount", t.getMessage());
                            }
                        });
            phone_tv.setOnClickListener((View v) -> {
                actionNumberTouch(0, user);
            });
            phone_tv.setOnLongClickListener((View v) ->{
                actionNumberTouch(1, user);
                return true;
            });
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
        if(!TextUtils.isEmpty(user.getPhoneNumber()))
        {
            String phone;
            phone = getFormatedNumber(user.getPhoneNumber());
            phone_tv.setText(phone);
        }
        if(!TextUtils.isEmpty(user.getCreatedAt()))
        {
            String date = user.getCreatedAt().split(" ")[0];
            date = Util.formatBadDateWithYear(date).substring(3);
            createdAt_tv.setText(date);
        }
        if(user.isCarOwner()) {
            String brazilianPlateRegex = "^[A-Z]{3}[0-9]{4}$";
            String plate = Pattern.compile(brazilianPlateRegex).matcher(user.getCarPlate()).matches() ? user.getCarPlate().substring(0,3) + "-" + user.getCarPlate().substring(3) : user.getCarPlate();
            carPlate_tv.setText(plate);
            carModel_tv.setText(user.getCarModel());
            carColor_tv.setText(user.getCarColor());
        }
        else
        {
            carPlate_tv.setText(" - ");
            carModel_tv.setText(" - ");
            carColor_tv.setText(" - ");
        }
    }
    @OnClick(R.id.logout_bt)
    public void logoutBt() {
        showExitAlertDialog();
    }

    public void setActionExit()
    {
        new LogOut(getContext()).execute();
        startActivity(new Intent(getContext(), LoginAct.class));
        getActivity().finish();
        SharedPref.NAV_INDICATOR = "AllRides";
    }

    //Creates an alert dialog to confirm if the user really wants to logout
    private void showExitAlertDialog()
    {
        CustomDialogClass customDialogClass;
        customDialogClass = new CustomDialogClass(getActivity(), "MyProfileShow", this);
        customDialogClass.show();
        customDialogClass.setTitleText(getActivity().getResources().getString(R.string.confirm_logout));
        customDialogClass.setPButtonText(getActivity().getResources().getString(R.string.cancel));
        customDialogClass.setNButtonText(getActivity().getResources().getString(R.string.exit));
        customDialogClass.setMessageVisibility(View.GONE);
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

    //Define actions when the user holds or touch the number on Profile Activity
    private void actionNumberTouch(int action, User user)
    {
        if(action == 0)
        {
            callUserPhone();
        }
        else
        {
            CustomPhoneDialogClass cpdc = new CustomPhoneDialogClass(getActivity(), "MyProfileShow", this, user.getPhoneNumber());
            WindowManager.LayoutParams wmlp = cpdc.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM;
            cpdc.show();
        }
    }

    public void callUserPhone()
    {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + user.getPhoneNumber()));
        startActivity(callIntent);
    }

    public void addUserPhone()
    {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, user.getName());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, user.getPhoneNumber());
        startActivity(intent);
    }

    public void copyUserPhone()
    {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("PhoneNumber", user.getPhoneNumber());
        clipboard.setPrimaryClip(clip);
    }
}
