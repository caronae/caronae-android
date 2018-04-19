package br.ufrj.caronae.acts;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.squareup.picasso.Picasso;

import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.RidersAdapter;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.FacebookFriendForJson;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import br.ufrj.caronae.models.modelsforjson.HistoryRideCountForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
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

    private boolean fromOffer;
    private boolean requested;
    private RideForJson rideOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        fromOffer = getIntent().getBooleanExtra("fromOffer", false);
        if(fromOffer) {
            rideOffer = getIntent().getExtras().getParcelable("ride");
            requested = getIntent().getBooleanExtra("requested", false);
        }
        String user2 = getIntent().getExtras().getString("user");
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
        phone_tv.setText(user.getPhoneNumber());
        String profilePicUrl = user.getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty())
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

        CaronaeAPI.service(getApplicationContext()).getRidesHistoryCount(user.getDbId() + "")
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
                         }

                    );

        try {
            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token != null) {
                if (user.getFaceId() != null) {
                    CaronaeAPI.service(getApplicationContext()).getMutualFriends(token.getToken(), user.getFaceId())
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

        String from = getIntent().getExtras().getString("from");
        if (from != null && (from.equals("activeRides"))) {
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
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                EditText msg_et = (EditText) fragment.getDialog().findViewById(R.id.msg_et);
                String msg = msg_et.getText().toString();
                if (msg.isEmpty()) {
                    Util.toast(getString(R.string.frag_falae_msgblank));
                    return;
                }
                msg= msg
                        + "\n\n--------------------------------\n"
                        + "Device: " + android.os.Build.MODEL + " (Android " + android.os.Build.VERSION.RELEASE + ")\n"
                        + "Versão do app: " + Util.getAppVersionName(getBaseContext());
                CaronaeAPI.service(getApplicationContext()).falaeSendMessage(new FalaeMsgForJson(getString(R.string.frag_falae_reportRb) + user.getName() + " - ID:" + user.getDbId(), msg))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Util.toast(getString(R.string.act_profile_reportOk));
                                    Log.i("falaeSendMessage", "falae message sent succesfully");
                                } else {
                                    Util.treatResponseFromServer(response);
                                    Util.toast(getString(R.string.frag_falae_errorSent));
                                    Log.e("falaeSendMessage", response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Util.toast(getString(R.string.frag_falae_errorSent));
                                Log.e("falaeSendMessage", t.getMessage());
                            }
                        });

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        String name = user.getName().split(" ")[0];
        builder.title("Reportar " + name)
                .positiveAction(getString(R.string.send_bt))
                .negativeAction(getString(R.string.cancel))
                .contentView(R.layout.report_dialog);

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }

    //Define actions when the user holds or touch the number on Profile Activity
    private void actionNumberTouch(int action)
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
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
        Intent intent = new Intent(this, RideOfferAct.class);
        intent.putExtra("ride", rideOffer);
        intent.putExtra("requested", requested);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
    }

    @Override
    public void onBackPressed() {
        if(fromOffer) {
            Intent intent = new Intent(this, RideOfferAct.class);
            intent.putExtra("ride", rideOffer);
            intent.putExtra("requested", requested);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
        }
        else{
            super.onBackPressed();
        }
    }
}
