package br.ufrj.caronae.acts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.CustomDialogClass;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.SwipeDismissBaseActivity;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.FacebookFriendForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideOfferAct extends SwipeDismissBaseActivity {

    @BindView(R.id.user_pic)
    public ImageView user_pic;
    @BindView(R.id.share_ic)
    public ImageView share_ic;
    @BindView(R.id.clock)
    public ImageView clock;

    @BindView(R.id.share_tv)
    public TextView share_tv;
    @BindView(R.id.location_dt)
    public TextView location_dt;
    @BindView(R.id.name_dt)
    public TextView name_dt;
    @BindView(R.id.profile_dt)
    public TextView profile_dt;
    @BindView(R.id.time_dt)
    public TextView time_dt;
    @BindView(R.id.way_dt)
    public TextView way_dt;
    @BindView(R.id.place_dt)
    public TextView place_dt;
    @BindView(R.id.description_dt)
    public TextView description_dt;
    @BindView(R.id.requested_dt)
    public TextView requested_dt;
    @BindView(R.id.mutual_friends_tv)
    TextView mFriends_tv;

    @BindView(R.id.join_bt)
    public Button join_bt;

    @BindView(R.id.share_bt)
    RelativeLayout shareButton;
    @BindView(R.id.title_lay)
    RelativeLayout locationBackground;

    RideForJson rideWithUsers;

    int zoneColorInt;
    boolean requested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ride_detail);
        ButterKnife.bind(this);
        boolean fromAllRides = getIntent().getBooleanExtra("fromAllRides", false);

        if(fromAllRides)
        {
            overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        }
        if (!startWithLink()) {
            rideWithUsers = getIntent().getExtras().getParcelable("ride");
            configureActivityWithRide(rideWithUsers, false);
        } else {
            configureActivityWithLink();
        }
    }

    private boolean startWithLink() {
        Uri uri = getIntent().getData();
        return uri != null;
    }

    private void createChatAssets(RideForJson rideWithUsers) {

        final String location;
        if (rideWithUsers.isGoing())
            location = rideWithUsers.getNeighborhood() + " ➜ " + rideWithUsers.getHub();
        else
            location = rideWithUsers.getHub() + " ➜ " + rideWithUsers.getNeighborhood();

        zoneColorInt = Util.getColors(rideWithUsers.getZone());

        List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideWithUsers.getDbId() + "");
        if (l == null || l.isEmpty())
            new ChatAssets(rideWithUsers.getDbId() + "", location, zoneColorInt, zoneColorInt,
                    Util.formatBadDateWithoutYear(rideWithUsers.getDate()),
                    Util.formatTime(rideWithUsers.getTime())).save();
    }

    public Animation getAnimationForSendButton() {
        Animation anim = new AlphaAnimation(1, 0);
        anim.setDuration(this.getApplicationContext().getResources().getInteger(R.integer.button_anim_duration));
        anim.setFillEnabled(true);
        anim.setFillAfter(true);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                join_bt.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return anim;
    }

    public Animation getAnimationForResquestedText() {
        Animation anim = new AlphaAnimation(0, 1);
        anim.setDuration(this.getApplicationContext().getResources().getInteger(R.integer.button_anim_duration));
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        return anim;
    }

    @Override
    public void onBackPressed() {
        SharedPref.NAV_INDICATOR = "AllRides";
        Intent intent = new Intent(this, MainAct.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
    }

    private void configureShareButton() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Util.getTextToShareRide(rideWithUsers));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
                startActivity(intent.createChooser(intent, "Compartilhar Carona"));
            }
        });
    }

    private void configureActivityWithRide(final RideForJson rideWithUsers, boolean isFull) {
        requested = getIntent().getBooleanExtra("requested", false);
        if (rideWithUsers == null) {
            Util.toast(getString(R.string.act_activeride_rideNUll));
            finish();
        }

        final User driver = rideWithUsers.getDriver();

        try {
            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token != null) {
                if (driver.getFaceId() != null) {
                    CaronaeAPI.service(getApplicationContext()).getMutualFriends(token.getToken(), driver.getFaceId())
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
                                        //Util.toast(getString(R.string.act_profile_errorMutualFriends));
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
        final boolean isDriver = driver.getDbId() == App.getUser().getDbId();
        CircleImageView photo_iv;
        photo_iv = (CircleImageView)user_pic;

        zoneColorInt = Util.getColors(rideWithUsers.getZone());

        Drawable background = join_bt.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(zoneColorInt);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(zoneColorInt);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(zoneColorInt);
        }
        locationBackground.setBackgroundColor(zoneColorInt);
        photo_iv.setBorderColor(zoneColorInt);

        final String location;
        if (rideWithUsers.isGoing())
            location = rideWithUsers.getNeighborhood().toUpperCase() + " ➜ " + rideWithUsers.getHub().toUpperCase();
        else
            location = rideWithUsers.getHub().toUpperCase() + " ➜ " + rideWithUsers.getNeighborhood().toUpperCase();

        String profilePicUrl = driver.getProfilePicUrl();
        if (profilePicUrl == null || profilePicUrl.isEmpty()) {
            Picasso.with(this.getApplicationContext()).load(R.drawable.user_pic)
                    .into(user_pic);
        } else {
            Picasso.with(this.getApplicationContext()).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(user_pic);
        }
        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDriver) {//dont allow user to open own profile
                    Intent intent = new Intent(getApplicationContext(), ProfileAct.class);
                    intent.putExtra("user", new Gson().toJson(driver));
                    intent.putExtra("from", "rideoffer");
                    intent.putExtra("fromAnother", true);
                    intent.putExtra("requested", requested);
                    intent.putExtra("ride", rideWithUsers);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
                }
            }
        });
        location_dt.setText(location);
        name_dt.setText(driver.getName());
        String info;
        if(driver.getProfile().equals("Servidor")) {
            info = driver.getProfile();
        }
        else
        {
            info = driver.getProfile() + " | " + driver.getCourse();
        }
        profile_dt.setText(info);
        if (rideWithUsers.getRoute().equals("")) {
            way_dt.setText("- - -");
        } else {
            String route = rideWithUsers.getRoute();
            route = route.replace(", ","\n");
            route = route.replace(",","");
            way_dt.setText(route);
        }
        if (rideWithUsers.getPlace().equals("")) {
            place_dt.setText("- - -");
        } else {
            place_dt.setText(rideWithUsers.getPlace());
        }
        String dateDescription;
        if (rideWithUsers.isGoing())
            dateDescription = getString(R.string.arrivingAt, Util.formatTime(rideWithUsers.getTime()));
        else
            dateDescription = getString(R.string.leavingAt, Util.formatTime(rideWithUsers.getTime()));

        dateDescription = dateDescription + " | " + Util.getWeekDayFromDate(rideWithUsers.getDate()) + " | " +Util.formatBadDateWithoutYear(rideWithUsers.getDate());
        clock.setColorFilter(zoneColorInt, PorterDuff.Mode.SRC_IN);
        share_ic.setColorFilter(zoneColorInt, PorterDuff.Mode.SRC_IN);
        share_tv.setTextColor(zoneColorInt);
        GradientDrawable strokeShare_bt = (GradientDrawable)shareButton.getBackground();
        strokeShare_bt.setStroke(2, zoneColorInt);
        time_dt.setText(dateDescription);
        time_dt.setTextColor(zoneColorInt);
        if (rideWithUsers.getDescription().equals("")) {
            description_dt.setText("- - -");
        } else {
            description_dt.setText(rideWithUsers.getDescription());
        }

        if (isDriver) {
            join_bt.setVisibility(View.GONE);

        } else {
            if (requested) {
                join_bt.setVisibility(View.GONE);
                requested_dt.setVisibility(View.VISIBLE);
            } else {
                if (isFull) {
                    join_bt.setText(R.string.full_ride);
                    join_bt.setClickable(false);
                } else {
                    join_bt.setClickable(true);
                    final Context context = this;
                    final Activity activity = this;
                    join_bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            List<ActiveRide> list = ActiveRide.find(ActiveRide.class, "date = ? and going = ?", rideWithUsers.getDate(), rideWithUsers.isGoing() ? "1" : "0");
                            if (list != null && !list.isEmpty()) {
                                Util.toast(getString(R.string.act_rideOffer_rideConflict));
                            }
                            else
                            {
                                CustomDialogClass customDialogClass;
                                customDialogClass = new CustomDialogClass(activity, "RideOfferAct", null);
                                customDialogClass.show();
                                int colorInt = getResources().getColor(R.color.darkblue2);
                                customDialogClass.setNegativeButtonColor(colorInt);
                                customDialogClass.setTitleText(getString(R.string.act_rideOffer_request_warning_title));
                                customDialogClass.setMessageText(getString(R.string.act_rideOffer_request_warning_message));
                                customDialogClass.setNButtonText(getString(R.string.act_rideOffer_request_warning_positive_button));
                                customDialogClass.setPButtonText(getString(R.string.cancel));
                            }
                        }
                    });
                }
            }
        }

        configureShareButton();
    }

    private void configureActivityWithLink() {

        Uri uri = getIntent().getData();
        final String rideId;
        List<String> params = uri.getPathSegments();

        if (params.size() == 1) {
            rideId = params.get(0);
            if (rideId.equals("carona")){
                SharedPref.NAV_INDICATOR = "AllRides";
                Intent intent = new Intent(App.getInst(), MainAct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
            }
        }
        else
            rideId = params.get(1);


        CaronaeAPI.service(this).getRide(rideId)
                .enqueue(new Callback<RideForJson>() {
                    @Override
                    public void onResponse(Call<RideForJson> call, Response<RideForJson> response) {
                        if (response.isSuccessful()) {
                            RideForJson ride = response.body();
                            if (Util.getStringDateInMillis(ride.getTime() + " " + ride.getDate()) < (new Date()).getTime()){
                                showCustomDialog(getResources().getString(R.string.ride_in_past_header),
                                        getResources().getString(R.string.ride_in_past_body));
                            } else {
                                ride.setDbId(Integer.parseInt(ride.getId() + ""));
                                configureActivityWithRide(ride, ride.getAvailableSlots() == 0);
                            }
                        } else {
                            showCustomDialog(getResources().getString(R.string.ride_failure_header),
                                    getResources().getString(R.string.ride_failure_non_exist_body));
                        }
                    }

                    @Override
                    public void onFailure(Call<RideForJson> call, Throwable t) {
                        showCustomDialog(getResources().getString(R.string.ride_failure_header),
                                getResources().getString(R.string.ride_failure_fail_body));
                    }
                });
    }

    private void showCustomDialog(String title, String text) {
        CustomDialogClass cdc = new CustomDialogClass(this, "RideOfferActError", null);
        cdc.show();
        cdc.enableOnePositiveOption();
        cdc.setPButtonText("OK");
        cdc.setMessageText(text);
        cdc.setTitleText(title);
    }

    public void customDialogAction()
    {
        SharedPref.NAV_INDICATOR = "AllRides";
        Intent intent = new Intent(App.getInst(), MainAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getInst().startActivity(intent);
        overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @OnClick(R.id.back_bt)
    public void backToMain()
    {
        SharedPref.NAV_INDICATOR = "AllRides";
        Intent intent = new Intent(this, MainAct.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
    }

    public void joinAction() {
        CaronaeAPI.service(this).requestJoin(String.valueOf(rideWithUsers.getDbId()))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            RideRequestSent rideRequest = new RideRequestSent(rideWithUsers.getDbId(), rideWithUsers.isGoing(), rideWithUsers.getDate());
                            rideRequest.save();

                            createChatAssets(rideWithUsers);

                            join_bt.startAnimation(getAnimationForSendButton());

                            requested_dt.startAnimation(getAnimationForResquestedText());

                            App.getBus().post(rideRequest);
                        } else {
                            Util.treatResponseFromServer(response);
                            Log.e("requestJoin", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("requestJoin", t.getMessage());
                    }
                });
    }
}
