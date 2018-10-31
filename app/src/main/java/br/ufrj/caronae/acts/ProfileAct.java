package br.ufrj.caronae.acts;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.redmadrobot.inputmask.helper.Mask;
import com.redmadrobot.inputmask.model.CaretString;
import com.squareup.picasso.Picasso;

import br.ufrj.caronae.R;
import br.ufrj.caronae.customizedviews.CustomPhoneDialogClass;
import br.ufrj.caronae.customizedviews.ExpandPhotoDialogClass;
import br.ufrj.caronae.customizedviews.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.FacebookFriendForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideHistoryForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileAct extends AppCompatActivity {

    @BindView(R.id.user_pic_iv)
    ImageView user_pic_iv;
    @BindView(R.id.phone_icon)
    ImageView phone_icon;
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.title_ride)
    TextView title_tv;
    @BindView(R.id.profile_tv)
    TextView profile_tv;
    @BindView(R.id.createdAt_tv)
    TextView createdAt_tv;
    @BindView(R.id.ridesOffered_tv)
    TextView ridesOffered_tv;
    @BindView(R.id.ridesTaken_tv)
    TextView ridesTaken_tv;
    @BindView(R.id.phone_tv)
    TextView phone_tv;
    @BindView(R.id.mutual_friends_tv)
    TextView mFriends_tv;

    User user;

    private boolean fromAnother;
    private RideForJson rideOffer;
    private String from, user2, fromWhere = "", status, profilePhotoURL = "";
    private int idRide;
    boolean showPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        try {
            fromWhere = getIntent().getStringExtra("fromWhere");
            status = getIntent().getStringExtra("status");
        }catch(Exception e){}
        fromAnother = getIntent().getBooleanExtra("fromAnother", false);
        if(fromAnother) {
            rideOffer = getIntent().getExtras().getParcelable("ride");
        }
        showPhone = getIntent().getBooleanExtra("showPhone", false);
        idRide = getIntent().getExtras().getInt("id");
        user2 = getIntent().getExtras().getString("user");
        user = new Gson().fromJson(user2, User.class);
        title_tv.setText(user.getName());
        name_tv.setText(user.getName());
        String profileInfo;
        if(user.getProfile().equals("Servidor"))
        {
            profileInfo = user.getProfile();
        }
        else
        {
            profileInfo = user.getProfile() + " | " + user.getCourse();
        }
        profile_tv.setText(profileInfo);
        String profilePicUrl = user.getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty())
            profilePhotoURL = profilePicUrl;
            Picasso.with(this).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(user_pic_iv);

        try {
            String date = user.getCreatedAt().split(" ")[0];
            date = Util.formatBadDateWithYear(date).substring(3);
            createdAt_tv.setText(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CaronaeAPI.service().getRidesHistory(Integer.toString(user.getDbId()))
                .enqueue(new Callback<RideHistoryForJson>() {
                             @Override
                             public void onResponse(Call<RideHistoryForJson> call, Response<RideHistoryForJson> response) {
                                 if (response.isSuccessful()) {
                                     RideHistoryForJson historyRide = response.body();
                                     ridesOffered_tv.setText(String.valueOf(historyRide.getRidesHistoryOfferedCount()));
                                     ridesTaken_tv.setText(String.valueOf(historyRide.getRidesHistoryTakenCount()));
                                 } else {
                                     Util.treatResponseFromServer(response);
                                     Util.toast(R.string.ridecount_error);
                                     Log.e("getRidesHistoryCount", response.message());
                                 }

                             }

                             @Override
                             public void onFailure(Call<RideHistoryForJson> call, Throwable t) {
                                 Util.toast(R.string.ridecount_error);
                                 Log.e("getRidesHistoryCount", t.getMessage());
                             }
                         }

                    );

        try {
            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token != null) {
                if (user.getFaceId() != null) {
                    CaronaeAPI.service().getMutualFriends(token.getToken(), user.getFaceId())
                            .enqueue(new Callback<FacebookFriendForJson>() {
                                @Override
                                public void onResponse(Call<FacebookFriendForJson> call, Response<FacebookFriendForJson> response) {
                                    if (response.isSuccessful()) {
                                        String mutualFriendsText;
                                        FacebookFriendForJson mutualFriends = response.body();
                                        int totalCount = mutualFriends.getTotalCount();
                                        if (totalCount < 1) {
                                            mutualFriendsText = "Amigos em comum: 0";
                                            mFriends_tv.setText(mutualFriendsText);
                                            return;
                                        }
                                        int size = mutualFriends.getMutualFriends().size();
                                        if(totalCount > 1)
                                        {
                                            mutualFriendsText = "Amigos em comum: " + totalCount + " no total e " + size + " no Caronaê";
                                        }
                                        else
                                        {
                                            mutualFriendsText = "Amigos em comum: " + totalCount;
                                        }
                                        mFriends_tv.setText(mutualFriendsText);
                                    } else {
                                        Util.treatResponseFromServer(response);
                                        Log.e("getMutualFriends", response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<FacebookFriendForJson> call, Throwable t) {
                                    Log.e("getMutualFriends", t.getMessage());
                                }
                            });

                } else {
                    Log.d("profileact,facebook", "user face id is null");
                }
            }
        } catch (Exception e) {
            Log.e("profileact", e.getMessage());
        }

        from = getIntent().getExtras().getString("from");
        if (showPhone) {
            phone_icon.setVisibility(View.VISIBLE);
            String phone = getFormatedNumber(user.getPhoneNumber());
            phone_tv.setText(phone);
            phone_tv.setVisibility(View.VISIBLE);
            //Controls the options that appears on app when user touch (short or long) on phone number
            phone_tv.setOnClickListener((View v) -> {
                actionNumberTouch(0);
            });
            phone_tv.setOnLongClickListener((View v) ->{
                actionNumberTouch(1);
                return true;
            });
        } else {
            phone_icon.setVisibility(View.GONE);
            phone_tv.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.report_bt)
    public void reportBt() {
        finish();
        Intent intent = new Intent(this, FalaeAct.class);
        intent.putExtra("showPhone", showPhone);
        intent.putExtra("user", user2);
        intent.putExtra("status", status);
        intent.putExtra("from", "rideoffer");
        intent.putExtra("fromAnother", true);
        intent.putExtra("fromProfile", true);
        intent.putExtra("driver", name_tv.getText());
        intent.putExtra("ride", rideOffer);
        intent.putExtra("fromWhere", fromWhere);
        intent.putExtra("id",  idRide);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.user_pic_iv)
    public void showProfilePhoto()
    {
        if(!profilePhotoURL.isEmpty())
        {
            ExpandPhotoDialogClass epdc = new ExpandPhotoDialogClass(this,this);
            epdc.show();
            epdc.setImage(profilePhotoURL);
        }
    }

    //Define actions when the user holds or touch the number on Profile Activity
    private void actionNumberTouch(int action)
    {
        if(action == 0)
        {
            callUserPhone();
        }
        else
        {
            CustomPhoneDialogClass dialog = new CustomPhoneDialogClass(this, "ProfileAct", null, user.getPhoneNumber());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM;
            dialog.show();
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
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("PhoneNumber", user.getPhoneNumber());
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.back_bt)
    public void backToRide()
    {
        Intent intent = new Intent(this, RideDetailAct.class);
        intent.putExtra("ride", rideOffer);
        intent.putExtra("status", status);
        intent.putExtra("fromWhere", fromWhere);
        intent.putExtra("id",  idRide);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
    }

    @Override
    public void onBackPressed() {
        if(fromAnother) {
            Intent intent = new Intent(this, RideDetailAct.class);
            intent.putExtra("ride", rideOffer);
            intent.putExtra("fromWhere", fromWhere);
            intent.putExtra("status", status);
            intent.putExtra("id",  idRide);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
        }
        else{
            super.onBackPressed();
        }
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
}
