package br.ufrj.caronae.acts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.RidersAdapter;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.FacebookFriendForJson;
import br.ufrj.caronae.models.modelsforjson.FalaeMsgForJson;
import br.ufrj.caronae.models.modelsforjson.HistoryRideCountForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileAct extends AppCompatActivity {

    @Bind(R.id.user_pic_iv)
    ImageView user_pic_iv;
    @Bind(R.id.name_tv)
    TextView name_tv;
    @Bind(R.id.profile_tv)
    TextView profile_tv;
    @Bind(R.id.course_tv)
    TextView course_tv;
    @Bind(R.id.createdAt_tv)
    TextView createdAt_tv;
    @Bind(R.id.ridesOffered_tv)
    TextView ridesOffered_tv;
    @Bind(R.id.ridesTaken_tv)
    TextView ridesTaken_tv;
    @Bind(R.id.phone_tv)
    TextView phone_tv;
    @Bind(R.id.call_tv)
    TextView call_tv;
    @Bind(R.id.mutualFriendsList)
    RecyclerView mutualFriendsList;
    @Bind(R.id.mutualFriends_lay)
    RelativeLayout mutualFriends_lay;
    @Bind(R.id.mutualFriends_tv)
    TextView mutualFriends_tv;
    @Bind(R.id.openProfile_tv)
    TextView openProfile_tv;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        String user2 = getIntent().getExtras().getString("user");
        user = new Gson().fromJson(user2, User.class);

        name_tv.setText(user.getName());
        profile_tv.setText(user.getProfile());
        course_tv.setText(user.getCourse());
        phone_tv.setText(user.getPhoneNumber());
        String profilePicUrl = user.getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty())
            Picasso.with(this).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation(0))
                    .into(user_pic_iv);

        try {
            String date = user.getCreatedAt().split(" ")[0];
            date = Util.formatBadDateWithYear(date);
            createdAt_tv.setText(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        App.getNetworkService().getRidesHistoryCount(user.getDbId() + "", new Callback<HistoryRideCountForJson>() {
            @Override
            public void success(HistoryRideCountForJson historyRideCountForJson, Response response) {
                ridesOffered_tv.setText(String.valueOf(historyRideCountForJson.getOfferedCount()));
                ridesTaken_tv.setText(String.valueOf(historyRideCountForJson.getTakenCount()));
            }

            @Override
            public void failure(RetrofitError error) {
                Util.toast(R.string.act_profile_errorCountRidesHistory);
                try {
                    Log.e("getRidesHistoryCount", error.getMessage());
                } catch (Exception e) {//sometimes RetrofitError is null
                    Log.e("getRidesHistoryCount", e.getMessage());
                }
            }
        });

        try {
            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token != null) {
                if (user.getFaceId() != null) {
                    String name = user.getName().split(" ")[0];
                    openProfile_tv.setText(getString(R.string.act_profile_openFbProfile, name));
                    openProfile_tv.setVisibility(View.VISIBLE);
                    App.getNetworkService().getMutualFriends(token.getToken(), user.getFaceId(), new Callback<FacebookFriendForJson>() {
                        @Override
                        public void success(FacebookFriendForJson mutualFriends, Response response) {
                            if (mutualFriends.getTotalCount() < 1)
                                return;

                            mutualFriends_lay.setVisibility(View.VISIBLE);

                            int totalCount = mutualFriends.getTotalCount();
                            String s = mutualFriends.getTotalCount() > 1 ? "s" : "";
                            int size = mutualFriends.getMutualFriends().size();
                            String s1 = mutualFriends.getMutualFriends().size() != 1 ? "m" : "";
                            mutualFriends_tv.setText(getString(R.string.act_profile_mutualFriends, totalCount, s, size, s1));

                            mutualFriendsList.setAdapter(new RidersAdapter(mutualFriends.getMutualFriends(), ProfileAct.this));
                            mutualFriendsList.setHasFixedSize(true);
                            mutualFriendsList.setLayoutManager(new LinearLayoutManager(ProfileAct.this, LinearLayoutManager.HORIZONTAL, false));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            //Util.toast(getString(R.string.act_profile_errorMutualFriends));
                            try {
                                Log.e("getMutualFriends", error.getMessage());
                            } catch (Exception e) {//sometimes RetrofitError is null
                                Log.e("getMutualFriends", e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.i("profileact,facebook", "user face id is null");
                }
            }
        } catch (Exception e) {
            Log.e("profileact", e.getMessage());
        }

        String from = getIntent().getExtras().getString("from");
        if (from != null && (from.equals("activeRides") || from.equals("riders"))) {
            call_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phone_tv.getText()));
                    startActivity(callIntent);
                }
            });
        } else {
            phone_tv.setVisibility(View.INVISIBLE);
            call_tv.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.openProfile_tv)
    public void openProfileTv() {
        Intent facebookIntent = getFBIntent(user.getFaceId());
        startActivity(facebookIntent);
    }

    public Intent getFBIntent(String facebookId) {
        String facebookProfileUri = "https://www.facebook.com/" + facebookId;
        return new Intent(Intent.ACTION_VIEW, Uri.parse(facebookProfileUri));
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
                if (msg.isEmpty())
                    return;

                App.getNetworkService().falaeSendMessage(new FalaeMsgForJson(getString(R.string.frag_falae_reportRb) + user.getName() + " - ID:" + user.getDbId(), msg), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Util.toast(getString(R.string.act_profile_reportOk));
                        Log.i("falaeSendMessage", "falae message sent succesfully");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Util.toast(getString(R.string.frag_falae_errorSent));
                        Log.e("falaeSendMessage", error.getMessage());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
