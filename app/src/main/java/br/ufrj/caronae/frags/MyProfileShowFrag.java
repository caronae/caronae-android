package br.ufrj.caronae.frags;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.redmadrobot.inputmask.helper.Mask;
import com.redmadrobot.inputmask.model.CaretString;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.LoginAct;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.RidesHistoryAdapter;
import br.ufrj.caronae.asyncs.LogOut;
import br.ufrj.caronae.comparators.RideComparatorByDateAndTimeReverse;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.HistoryRideCountForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
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
        /*if(SharedPref.loadPic() != null)
        {
            user_pic.setImageBitmap(SharedPref.loadPic());
        }*/
        name_tv.setText(user.getName());
        String info;
        info = user.getProfile() + " | " + user.getCourse();
        profile_tv.setText(info);
        if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()) {
            Picasso.with(getContext()).load(user.getProfilePicUrl())
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(user_pic);
            //SharedPref.savePic(((BitmapDrawable)user_pic.getDrawable()).getBitmap());
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
            String plate;
            plate = getFormatedPlate(user.getCarPlate());
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

    //Creates an alert dialog to confirm if the user really wants to logout
    private void showExitAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.confirm_logout)
                .setCancelable(false)
                .setPositiveButton(R.string.frag_logout_title, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new LogOut(getContext()).execute();
                        startActivity(new Intent(getContext(), LoginAct.class));
                        getActivity().finish();
                        SharedPref.NAV_INDICATOR = "AllRides";
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(getResources().getColor(R.color.darkblue2));
        nbutton.setText(R.string.cancel);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(getResources().getColor(R.color.red));
        pbutton.setText(R.string.frag_logout_title);
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

    //Define actions when the user holds or touch the number on Profile Activity
    private void actionNumberTouch(int action, User user)
    {
        if(action == 0)
        {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phone_tv.getText()));
            startActivity(callIntent);
        }
        else
        {
            CharSequence options[] = new CharSequence[] {"Ligar para "+user.getPhoneNumber(), "Adicionar aos Contatos", "Copiar"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which)
                    {
                        case 0:
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + phone_tv.getText()));
                            startActivity(callIntent);
                            break;
                        case 1:
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                            intent.putExtra(ContactsContract.Intents.Insert.NAME, user.getName());
                            intent.putExtra(ContactsContract.Intents.Insert.PHONE, user.getPhoneNumber());
                            startActivity(intent);
                            break;
                        case 2:
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PhoneNumber", user.getPhoneNumber());
                            clipboard.setPrimaryClip(clip);
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
    }
}
