package br.ufrj.caronae.acts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.redmadrobot.inputmask.helper.Mask;
import com.redmadrobot.inputmask.model.CaretString;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import br.ufrj.caronae.App;
import br.ufrj.caronae.customizedviews.CustomDialogClass;
import br.ufrj.caronae.R;
import br.ufrj.caronae.customizedviews.CustomPhoneDialogClass;
import br.ufrj.caronae.customizedviews.RoundedTransformation;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.customizedviews.SwipeDismissBaseActivity;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.FacebookFriendForJson;
import br.ufrj.caronae.models.modelsforjson.JoinRequestIDsForJson;
import br.ufrj.caronae.models.modelsforjson.MyRidesForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideDetailAct extends SwipeDismissBaseActivity {

    @BindView(R.id.user_pic)
    public ImageView user_pic;
    @BindView(R.id.share_ic)
    public ImageView share_ic;
    @BindView(R.id.clock)
    public ImageView clock;
    @BindView(R.id.phone_icon)
    ImageView phone_ic;
    @BindView(R.id.requester_photo)
    CircleImageView requesterPhoto;

    @BindView(R.id.back_title)
    public TextView back_tv;
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
    @BindView(R.id.mutual_friends_tv)
    TextView mFriends_tv;
    @BindView(R.id.cancel_ride)
    TextView cancelRide_bt;
    @BindView(R.id.leave_ride)
    TextView leaveRide_bt;
    @BindView(R.id.chat_bt)
    TextView chat_bt;
    @BindView(R.id.plate_tv)
    TextView plate_tv;
    @BindView(R.id.car_model_tv)
    TextView carModel_tv;
    @BindView(R.id.car_color_tv)
    TextView carColor_tv;
    @BindView(R.id.no_riders)
    TextView noRiders;
    @BindView(R.id.accept_tv)
    TextView accept_tv;
    @BindView(R.id.requester_name)
    TextView requesterName;
    @BindView(R.id.requester_status)
    TextView requesterStatus;
    @BindView(R.id.phone_tv)
    TextView phone_tv;

    @BindView(R.id.join_bt)
    public Button join_bt;
    @BindView(R.id.finish_bt)
    public Button finish_bt;
    @BindView(R.id.requested_dt)
    Button requested_dt;

    @BindView(R.id.can_join)
    RelativeLayout joinLayout;
    @BindView(R.id.share_bt)
    RelativeLayout shareButton;
    @BindView(R.id.title_lay)
    RelativeLayout locationBackground;
    @BindView(R.id.car_detail)
    RelativeLayout carDetails;
    @BindView(R.id.riders_layout)
    RelativeLayout ridersLayout;
    @BindView(R.id.invite_lay)
    RelativeLayout inviteLay;
    @BindView(R.id.remove_request)
    RelativeLayout removeRequest;
    @BindView(R.id.accept_request)
    RelativeLayout acceptRequest;
    @BindView(R.id.request)
    RelativeLayout requestLay;
    @BindView(R.id.can_finish)
    RelativeLayout canFinishLay;

    @BindView(R.id.riders_profile)
    LinearLayout ridersProfile;

    @BindView(R.id.loading_in_progress)
    ProgressBar progressBar;

    RideForJson rideWithUsers;
    List<User> usersRequest;
    public JoinRequestIDsForJson answerRequest;

    int zoneColorInt, idRide;
    boolean isFull, startLink;
    String fromWhere = "", isGoing, status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean starting = getIntent().getBooleanExtra("starting", false);
        if(starting)
        {
            overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        }
        try
        {
            idRide = getIntent().getExtras().getInt("id");
        }catch(Exception e){}
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ride_detail);
        ButterKnife.bind(this);
        isGoing = "1";
        try {
            fromWhere = getIntent().getStringExtra("fromWhere");
        }catch(Exception e){}

        if(fromWhere != null) {
            if (fromWhere.equals("SearchRides"))
            {
                back_tv.setText("Pesquisa");
            }
            else if(fromWhere.equals(getResources().getString(R.string.fragment_myrides_title)))
            {
                back_tv.setText(R.string.fragment_myrides_title);
            }
        }
        startLink = getIntent().getBooleanExtra("startLink", false);
        if (!startLink) {
            rideWithUsers = getIntent().getExtras().getParcelable("ride");
            if(idRide == 0) {
                idRide = rideWithUsers.getDbId();
            }
            if(rideWithUsers.getRiders() != null) {
                configureActivityWithRide(rideWithUsers, Integer.parseInt(rideWithUsers.getSlots()) - rideWithUsers.getRiders().size() <= 0, false);
            }
            else
            {
                configureActivityWithRide(rideWithUsers, false, false);
            }
        } else {
            configureActivityWithLink();
        }
    }

    private void createChatAssets(RideForJson rideWithUsers) {

        final String location;
        if (rideWithUsers.isGoing())
            location = rideWithUsers.getNeighborhood() + " ➜ " + rideWithUsers.getHub();
        else
            location = rideWithUsers.getHub() + " ➜ " + rideWithUsers.getNeighborhood();

        zoneColorInt = Util.getColors(rideWithUsers.getZone());

        List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", Integer.toString(idRide));
        if (l == null || l.isEmpty())
            new ChatAssets(Integer.toString(idRide), location, zoneColorInt,
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
        if(progressBar.getVisibility() != View.VISIBLE) {
            Intent intent;
            switch (back_tv.getText().toString()) {
                case "Todas":
                    SharedPref.NAV_INDICATOR = "AllRides";
                    intent = new Intent(this, MainAct.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    break;
                case "Pesquisa":
                    intent = new Intent(this, RideSearchAct.class);
                    intent.putExtra("isGoing", isGoing);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    break;
                case "Minhas":
                    SharedPref.NAV_INDICATOR = "MyRides";
                    intent = new Intent(this, MainAct.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    break;
                default:
                    SharedPref.NAV_INDICATOR = "AllRides";
                    intent = new Intent(this, MainAct.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    break;
            }
        }
    }

    private void configureShareButton() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Util.getTextToShareRide(rideWithUsers, idRide));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
                startActivity(intent.createChooser(intent, "Compartilhar Carona"));
            }
        });
    }

    private void configureActivityWithRide(final RideForJson rideWithUsers, boolean fully, boolean withLink) {
        if (rideWithUsers == null) {
            Util.toast(getString(R.string.activity_ridedetail_ride_error));
            finish();
        }
        if(!withLink) {
            if(SharedPref.checkExistence(SharedPref.MYACTIVERIDESID_KEY) && SharedPref.getMyActiveRidesId().contains(idRide))
            {
                this.status = "active";
            }
            else if(SharedPref.checkExistence(SharedPref.MYPENDINGRIDESID_KEY) && SharedPref.getMyPendingRidesId().contains(idRide))
            {
                this.status = "pending";
            }
        }
        final User driver = rideWithUsers.getDriver();

        try {
            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token != null) {
                if (driver.getFaceId() != null) {
                    CaronaeAPI.service().getMutualFriends(token.getToken(), driver.getFaceId())
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
        Drawable backR = requested_dt.getBackground();
        if (backR instanceof ShapeDrawable) {
            ((ShapeDrawable)backR).getPaint().setColor(zoneColorInt);
        } else if (backR instanceof GradientDrawable) {
            ((GradientDrawable)backR).setColor(zoneColorInt);
        } else if (backR instanceof ColorDrawable) {
            ((ColorDrawable)backR).setColor(zoneColorInt);
        }
        locationBackground.setBackgroundColor(zoneColorInt);
        photo_iv.setBorderColor(zoneColorInt);
        requesterPhoto.setBorderColor(zoneColorInt);

        final String location;
        if (rideWithUsers.isGoing()) {
            location = rideWithUsers.getNeighborhood().toUpperCase() + " ➜ " + rideWithUsers.getHub().toUpperCase();
            isGoing = "1";
        }
        else {
            location = rideWithUsers.getHub().toUpperCase() + " ➜ " + rideWithUsers.getNeighborhood().toUpperCase();
            isGoing = "0";
        }
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
                    if(status.equals("active"))
                    {
                        intent.putExtra("showPhone", true);
                    }
                    intent.putExtra("user", new Gson().toJson(driver));
                    intent.putExtra("status", status);
                    intent.putExtra("from", "rideoffer");
                    intent.putExtra("fromAnother", true);
                    intent.putExtra("ride", rideWithUsers);
                    intent.putExtra("fromWhere", fromWhere);
                    intent.putExtra("id", idRide);
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
            dateDescription = getString(R.string.arriving_at, Util.formatTime(rideWithUsers.getTime()));
        else
            dateDescription = getString(R.string.leaving_at, Util.formatTime(rideWithUsers.getTime()));

        dateDescription = dateDescription + " | " + Util.getWeekDayFromDate(rideWithUsers.getDate()) + " | " +Util.formatBadDateWithoutYear(rideWithUsers.getDate());
        clock.setColorFilter(zoneColorInt, PorterDuff.Mode.SRC_IN);
        share_ic.setColorFilter(zoneColorInt, PorterDuff.Mode.SRC_IN);
        share_tv.setTextColor(zoneColorInt);

        accept_tv.setTextColor(zoneColorInt);
        GradientDrawable strokeShare_bt = (GradientDrawable)shareButton.getBackground();
        strokeShare_bt.setStroke(2, zoneColorInt);
        GradientDrawable strokeRemove_bt = (GradientDrawable)removeRequest.getBackground();
        strokeRemove_bt.setStroke(2, getResources().getColor(R.color.dark_gray));
        GradientDrawable strokeAccept_bt = (GradientDrawable)acceptRequest.getBackground();
        strokeAccept_bt.setStroke(2, zoneColorInt);
        time_dt.setText(dateDescription);
        time_dt.setTextColor(zoneColorInt);
        if (rideWithUsers.getDescription().equals("")) {
            description_dt.setText("- - -");
        } else {
            description_dt.setText(rideWithUsers.getDescription());
        }
        if (isDriver)
        {
            configureOfferedRide();
            this.status = "offered";
        }
        else
        {
            if(status.equals("pending"))
            {
                join_bt.setVisibility(View.GONE);
                requested_dt.setVisibility(View.VISIBLE);
                inviteLay.setVisibility(View.VISIBLE);
            }
            else if(status.equals("active"))
            {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String getCurrentDateTime = sdf.format(calendar.getTime());
                String time = rideWithUsers.getDate()+" "+rideWithUsers.getTime().substring(0,rideWithUsers.getTime().length()-3);
                if (getCurrentDateTime.compareTo(time) < 0)
                {
                    //Future
                    leaveRide_bt.setVisibility(View.VISIBLE);
                }
                else
                {
                    //Past
                    leaveRide_bt.setVisibility(View.GONE);
                }
                joinLayout.setVisibility(View.GONE);
                join_bt.setVisibility(View.GONE);
                requested_dt.setVisibility(View.GONE);
                inviteLay.setVisibility(View.GONE);
                String phone = getFormatedNumber(rideWithUsers.getDriver().getPhoneNumber());
                phone_tv.setText(phone);
                phone_ic.setVisibility(View.VISIBLE);
                phone_tv.setVisibility(View.VISIBLE);
                setNumberClickedAction();
                chat_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChat();
                    }
                });
                chat_bt.setVisibility(View.VISIBLE);
                carDetails.setVisibility(View.VISIBLE);
                ridersLayout.setVisibility(View.VISIBLE);
                verifyRiders();
            }
            else
            {
                if (fully)
                {
                    join_bt.setText(R.string.full_ride_uppercase);
                    join_bt.setClickable(false);
                    join_bt.setFocusable(false);
                }
                else
                {
                    join_bt.setClickable(true);
                    join_bt.setFocusable(true);
                    final Activity activity = this;
                    join_bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            List<Ride> list = Ride.find(Ride.class, "date = ? and going = ?", rideWithUsers.getDate(), rideWithUsers.isGoing() ? "1" : "0");
                            if (list != null && !list.isEmpty()) {
                                Util.toast(getString(R.string.fragment_rideoffer_ride_conflict_message));
                            }
                            else
                            {
                                CustomDialogClass customDialogClass;
                                customDialogClass = new CustomDialogClass(activity, "RideDetailAct", null);
                                customDialogClass.show();
                                int colorInt = getResources().getColor(R.color.darkblue);
                                customDialogClass.setNegativeButtonColor(colorInt);
                                customDialogClass.setTitleText(getString(R.string.activity_ridedetail_request_warning_title));
                                customDialogClass.setMessageText(getString(R.string.act_rideOffer_request_warning_message));
                                customDialogClass.setNButtonText(getString(R.string.request));
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
        final String rideId = getIntent().getStringExtra("rideLinkId");
        final Context ctx = this;

        if (rideId.equals("carona")){
            SharedPref.NAV_INDICATOR = "AllRides";
            Intent intent = new Intent(App.getInst(), MainAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
        }

        idRide = Integer.parseInt(rideId);
        ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.wait), true, true);
        CaronaeAPI.service().getRide(rideId)
            .enqueue(new Callback<RideForJson>() {
                @Override
                public void onResponse(Call<RideForJson> call, Response<RideForJson> response) {
                    if (response.isSuccessful()) {
                        rideWithUsers = response.body();
                        CaronaeAPI.service().getMyRides(Integer.toString(App.getUser().getDbId()))
                            .enqueue(new retrofit2.Callback<MyRidesForJson>() {
                                @Override
                                public void onResponse(Call<MyRidesForJson> call, Response<MyRidesForJson> response) {
                                    if (response.isSuccessful()) {
                                        MyRidesForJson data = response.body();
                                        List<RideForJson> pendingRides = data.getPendingRides();
                                        List<RideForJson> activeRides = data.getActiveRides();
                                        boolean isActive = false;
                                        if(pendingRides != null && !pendingRides.isEmpty())
                                        {
                                            for(int i = 0; i < pendingRides.size(); i++) {
                                                if (pendingRides.get(i).getId().intValue() == idRide) {
                                                    status = "pending";
                                                }
                                            }
                                        }
                                        if(activeRides != null && !activeRides.isEmpty())
                                        {
                                            for(int i = 0; i < activeRides.size(); i++) {
                                                if (activeRides.get(i).getId().intValue() == idRide) {
                                                    isActive = true;
                                                    rideWithUsers = activeRides.get(i);
                                                    status = "active";
                                                }
                                            }
                                        }
                                        if (!isActive && Util.getStringDateInMillis(rideWithUsers.getTime() + " " + rideWithUsers.getDate()) < (new Date()).getTime()){
                                            showCustomDialog(getResources().getString(R.string.ride_finished_title),
                                                    getResources().getString(R.string.ride_finished_message));
                                        } else {
                                            rideWithUsers.setDbId(Integer.parseInt(rideId));
                                            if (rideWithUsers.getRiders() != null) {
                                                configureActivityWithRide(rideWithUsers, Integer.parseInt(rideWithUsers.getSlots()) - rideWithUsers.getRiders().size() <= 0, true);
                                            }
                                            else
                                            {
                                                configureActivityWithRide(rideWithUsers, false, true);
                                            }
                                        }
                                        if(rideWithUsers.getDriver().getDbId() == App.getUser().getDbId() || isActive)
                                        {
                                            back_tv.setText(R.string.fragment_myrides_title);
                                        }
                                        pd.dismiss();
                                    } else {
                                        Util.treatResponseFromServer(response);
                                        Util.debug(response.message());
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyRidesForJson> call, Throwable t) {
                                }
                            });
                    } else {
                        showCustomDialog(getResources().getString(R.string.ride_failure_title),
                                getResources().getString(R.string.ride_failure_not_find_message));
                    }
                }

                @Override
                public void onFailure(Call<RideForJson> call, Throwable t) {
                    showCustomDialog(getResources().getString(R.string.ride_failure_title),
                            getResources().getString(R.string.ride_failure_fail_message));
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
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @OnClick(R.id.back_bt)
    public void backToMain()
    {
        backToLast();
    }

    public void joinAction() {
        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.wait), true, true);
        CaronaeAPI.service().requestJoin(String.valueOf(idRide))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            RideRequestSent rideRequest = new RideRequestSent(idRide, rideWithUsers.isGoing(), rideWithUsers.getDate());
                            rideRequest.save();
                            createChatAssets(rideWithUsers);
                            if(SharedPref.checkExistence(SharedPref.MYPENDINGRIDESID_KEY))
                            {
                                List<Integer> ids = SharedPref.getMyPendingRidesId();
                                ids.add(idRide);
                                SharedPref.setMyPendingRidesId(ids);
                            }
                            else
                            {
                                List<Integer> ids = new ArrayList<>();
                                ids.add(idRide);
                                SharedPref.setMyPendingRidesId(ids);
                            }
                            join_bt.startAnimation(getAnimationForSendButton());
                            inviteLay.setVisibility(View.VISIBLE);
                            requested_dt.startAnimation(getAnimationForResquestedText());
                            SharedPref.lastMyRidesUpdate = 350;
                            SharedPref.lastAllRidesUpdate = null;
                            App.getBus().post(rideRequest);
                            pd.dismiss();
                        } else {
                            Util.treatResponseFromServer(response);
                            Log.e("requestJoin", response.message());
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("requestJoin", t.getMessage());
                        pd.dismiss();
                    }
                });
    }

    private void configureOfferedRide()
    {
        String brazilianPlateRegex = "^[A-Z]{3}[0-9]{4}$";
        String plate = Pattern.compile(brazilianPlateRegex).matcher(rideWithUsers.getDriver().getCarPlate()).matches() ? rideWithUsers.getDriver().getCarPlate().substring(0,3)+"-"+rideWithUsers.getDriver().getCarPlate().substring(3):rideWithUsers.getDriver().getCarPlate();
        plate_tv.setText(plate);
        String phone = getFormatedNumber(rideWithUsers.getDriver().getPhoneNumber());
        phone_tv.setText(phone);
        phone_ic.setVisibility(View.VISIBLE);
        phone_tv.setVisibility(View.VISIBLE);
        setNumberClickedAction();
        carModel_tv.setText(rideWithUsers.getDriver().getCarModel());
        carColor_tv.setText(rideWithUsers.getDriver().getCarColor());
        joinLayout.setVisibility(View.GONE);
        ridersLayout.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String getCurrentDateTime = sdf.format(calendar.getTime());
        String time = rideWithUsers.getDate()+" "+rideWithUsers.getTime().substring(0,rideWithUsers.getTime().length()-3);
        if (getCurrentDateTime.compareTo(time) < 0)
        {
            //Future
            cancelRide_bt.setVisibility(View.VISIBLE);
        }
        else
        {
            //Past
            zoneColorInt = Util.getColors(rideWithUsers.getZone());
            Drawable background = finish_bt.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable)background).getPaint().setColor(zoneColorInt);
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable)background).setColor(zoneColorInt);
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable)background).setColor(zoneColorInt);
            }
            cancelRide_bt.setVisibility(View.GONE);
            canFinishLay.setVisibility(View.VISIBLE);
        }

        chat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });
        chat_bt.setVisibility(View.VISIBLE);
        carDetails.setVisibility(View.VISIBLE);
        verifyRiders();
        verifyRequesters();
    }

    @OnClick(R.id.cancel_ride)
    public void cancelRide()
    {
        CustomDialogClass cdc = new CustomDialogClass(this, "CancelRide", null);
        cdc.show();
        cdc.setTitleText("Deseja mesmo desistir da carona?");
        cdc.setMessageText("Você é livre para cancelar caronas caso não possa participar, mas é importante fazer isso com responsabilidade. Caso haja outros usuários na carona, eles serão notificados.");
        cdc.setNButtonText("Desistir");
        cdc.setPButtonText("Voltar");
    }

    @OnClick(R.id.leave_ride)
    public void leavingRide()
    {
        CustomDialogClass cdc = new CustomDialogClass(this, "CancelRide", null);
        cdc.show();
        cdc.setTitleText("Deseja mesmo desistir da carona?");
        cdc.setMessageText("Você é livre para desistir da carona caso não possa participar, mas é importante fazer isso com responsabilidade. O motorista da carona será notificado.");
        cdc.setNButtonText("Desistir");
        cdc.setPButtonText("Voltar");
    }

    public void cancel()
    {
        progressBar.setVisibility(View.GONE);
        if( rideWithUsers.getRoutineId() == null || rideWithUsers.getRoutineId().isEmpty() || rideWithUsers.getDriver().getDbId() != App.getUser().getDbId()) {
            progressBar.setVisibility(View.VISIBLE);
            leaveRide(idRide);
        }
        else if(status.equals("offered"))
        {
            CharSequence options[] = new CharSequence[] {"Desistir somente desta", "Desistir da rotina"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Esta carona pertence a uma rotina.");
            builder.setCancelable(true);
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which)
                    {
                        case 0:
                            progressBar.setVisibility(View.VISIBLE);
                            leaveRide(idRide);
                            break;
                        case 1:
                            progressBar.setVisibility(View.VISIBLE);
                            cancelRoutine(rideWithUsers.getRoutineId());
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

    private void cancelRoutine(String routineId)
    {
        Activity act = this;
        CaronaeAPI.service().deleteAllRidesFromRoutine(routineId).
            enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        SharedPref.lastAllRidesUpdate = null;
                        SharedPref.lastMyRidesUpdate = 350;
                        backToLast();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        CustomDialogClass cdc = new CustomDialogClass(act, "", null);
                        cdc.show();
                        cdc.setTitleText("Algo deu errado.");
                        cdc.setMessageText("Não foi possível cancelar sua carona. (A conexão à internet talvez esteja inativa.)");
                        cdc.setPButtonText("OK");
                        cdc.enableOnePositiveOption();
                        Util.treatResponseFromServer(response);
                        Log.e("Error ", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    CustomDialogClass cdc = new CustomDialogClass(act, "", null);
                    cdc.show();
                    cdc.setTitleText("Algo deu errado.");
                    cdc.setMessageText("Não foi possível cancelar sua carona. (A conexão à internet talvez esteja inativa.)");
                    cdc.setPButtonText("OK");
                    cdc.enableOnePositiveOption();
                    Log.e("Error ", t.getLocalizedMessage());
                }
            });
    }

    private void leaveRide(int rideId)
    {
        Activity act = this;
        CaronaeAPI.service().leaveRide(Integer.toString(rideId)).
            enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        SharedPref.lastAllRidesUpdate = null;
                        SharedPref.lastMyRidesUpdate = 350;
                        backToLast();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        CustomDialogClass cdc = new CustomDialogClass(act, "", null);
                        cdc.show();
                        cdc.setTitleText("Algo deu errado.");
                        cdc.setMessageText("Não foi possível cancelar sua carona. (A conexão à internet talvez esteja inativa.)");
                        cdc.setPButtonText("OK");
                        cdc.enableOnePositiveOption();
                        Util.treatResponseFromServer(response);
                        Log.e("Error ", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    CustomDialogClass cdc = new CustomDialogClass(act, "", null);
                    cdc.show();
                    cdc.setTitleText("Algo deu errado.");
                    cdc.setMessageText("Não foi possível cancelar sua carona. (A conexão à internet talvez esteja inativa.)");
                    cdc.setPButtonText("OK");
                    cdc.enableOnePositiveOption();
                    Log.e("Error ", t.getLocalizedMessage());
                }
            });
    }

    public void backToLast()
    {
        if(progressBar.getVisibility() != View.VISIBLE) {
            Intent intent;
            switch (back_tv.getText().toString()) {
                case "Todas":
                    SharedPref.NAV_INDICATOR = "AllRides";
                    intent = new Intent(this, MainAct.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    break;
                case "Pesquisa":
                    intent = new Intent(this, RideSearchAct.class);
                    intent.putExtra("isGoing", isGoing);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    break;
                case "Minhas":
                    SharedPref.NAV_INDICATOR = "MyRides";
                    intent = new Intent(this, MainAct.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    break;
                default:
                    SharedPref.NAV_INDICATOR = "AllRides";
                    intent = new Intent(this, MainAct.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
                    break;
            }
        }
    }

    private void openChat()
    {
        final String location;
        if (rideWithUsers.isGoing())
            location = rideWithUsers.getNeighborhood() + " ➜ " + rideWithUsers.getHub();
        else
            location = rideWithUsers.getHub() + " ➜ " + rideWithUsers.getNeighborhood();

        int color = Util.getColors(rideWithUsers.getZone());
        List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", Integer.toString(idRide));
        if (l == null || l.isEmpty())
            new ChatAssets(Integer.toString(idRide), location, color,
                    Util.formatBadDateWithoutYear(rideWithUsers.getDate()),
                    Util.formatTime(rideWithUsers.getTime())).save();

        Intent intent = new Intent(RideDetailAct.this, ChatAct.class);
        intent.putExtra("rideId", Integer.toString(idRide));
        intent.putExtra("status", status);
        intent.putExtra("ride", rideWithUsers);
        intent.putExtra("fromWhere", fromWhere);
        intent.putExtra("id", idRide);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    private void verifyRiders()
    {
        if(rideWithUsers.getRiders() != null && rideWithUsers.getRiders().size() > 0)
        {
            ridersProfile.setVisibility(View.VISIBLE);
            noRiders.setVisibility(View.GONE);
            CircleImageView[] riderPhoto = {findViewById(R.id.rider_0), findViewById(R.id.rider_1), findViewById(R.id.rider_2), findViewById(R.id.rider_3), findViewById(R.id.rider_4), findViewById(R.id.rider_5)};
            TextView[] riderName = {findViewById(R.id.rider_0_name), findViewById(R.id.rider_1_name), findViewById(R.id.rider_2_name), findViewById(R.id.rider_3_name), findViewById(R.id.rider_4_name), findViewById(R.id.rider_5_name)};
            for(int i = 0; i < rideWithUsers.getRiders().size(); i++)
            {
                if (rideWithUsers.getRiders().get(i).getProfilePicUrl() != null && !rideWithUsers.getRiders().get(i).getProfilePicUrl().isEmpty()) {
                    Picasso.with(getBaseContext()).load(rideWithUsers.getRiders().get(i).getProfilePicUrl())
                            .placeholder(R.drawable.user_pic)
                            .error(R.drawable.user_pic)
                            .transform(new RoundedTransformation())
                            .into(riderPhoto[i]);
                } else {
                    riderPhoto[i].setImageResource(R.drawable.user_pic);
                }
                riderPhoto[i].setVisibility(View.VISIBLE);
                final User currentRider = rideWithUsers.getRiders().get(i);
                String[] name = currentRider.getName().split(" ");
                riderName[i].setText(name[0]);
                riderName[i].setVisibility(View.VISIBLE);
                riderPhoto[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentRider.getDbId() != App.getUser().getDbId()) { //dont allow user to open own profile
                            Intent intent = new Intent(getApplicationContext(), ProfileAct.class);
                            intent.putExtra("showPhone", true);
                            intent.putExtra("user", new Gson().toJson(currentRider));
                            intent.putExtra("status", status);
                            intent.putExtra("from", "rideoffer");
                            intent.putExtra("fromAnother", true);
                            intent.putExtra("ride", rideWithUsers);
                            intent.putExtra("fromWhere", fromWhere);
                            intent.putExtra("id", idRide);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
                        }
                    }
                });
            }
        }
        else
        {
            noRiders.setVisibility(View.VISIBLE);
            ridersProfile.setVisibility(View.GONE);
        }
    }

    private void verifyRequesters()
    {
        final Context ctx = this;
        CaronaeAPI.service().getRequesters(Integer.toString(idRide)).
            enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.isSuccessful()) {
                        List<User> requesters = response.body();
                        if(requesters != null && !requesters.isEmpty())
                        {
                            usersRequest = requesters;
                            if(!isFull) {
                                requesterName.setText(requesters.get(usersRequest.size()-1).getName());
                                String statusR;
                                statusR = requesters.get(usersRequest.size()-1).getProfile() + " | " + requesters.get(usersRequest.size()-1).getCourse();
                                requesterStatus.setText(statusR);
                                if (requesters.get(usersRequest.size()-1).getProfilePicUrl() == null || requesters.get(usersRequest.size()-1).getProfilePicUrl().isEmpty()) {
                                    Picasso.with(ctx).load(R.drawable.user_pic)
                                            .into(requesterPhoto);
                                } else {
                                    Picasso.with(ctx).load(requesters.get(usersRequest.size()-1).getProfilePicUrl())
                                            .placeholder(R.drawable.user_pic)
                                            .error(R.drawable.user_pic)
                                            .transform(new RoundedTransformation())
                                            .into(requesterPhoto);
                                }
                                requesterPhoto.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(), ProfileAct.class);
                                        intent.putExtra("status", status);
                                        intent.putExtra("user", new Gson().toJson(requesters.get(usersRequest.size()-1)));
                                        intent.putExtra("from", "rideoffer");
                                        intent.putExtra("fromAnother", true);
                                        intent.putExtra("ride", rideWithUsers);
                                        intent.putExtra("fromWhere", fromWhere);
                                        intent.putExtra("id", idRide);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
                                    }
                                });
                                requestLay.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                for(int i = 0; i < usersRequest.size(); i++)
                                {
                                    answerRequest = new JoinRequestIDsForJson(usersRequest.get(i).getDbId(), false);
                                    onRequestAction(answerRequest);
                                }
                            }
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Util.treatResponseFromServer(response);
                        Log.e("Error ", response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Error ", t.getLocalizedMessage());
                }
            });
    }

    @OnClick(R.id.accept_request)
    public void aRequest()
    {
        answerRequest = new JoinRequestIDsForJson(usersRequest.get(usersRequest.size()-1).getDbId(), true);
        if(rideWithUsers.getRiders() == null || Integer.parseInt(rideWithUsers.getSlots()) - rideWithUsers.getRiders().size() > 1) {
            onRequestAction(answerRequest);
        }else
        {
            String[] userName = usersRequest.get(usersRequest.size()-1).getName().split(" ");
            CustomDialogClass cdc = new CustomDialogClass(this, "RIDA", null);
            cdc.show();
            cdc.setTitleText("Deseja mesmo aceitar "+userName[0]+"?");
            cdc.setMessageText("Ao aceitar, sua carona estará cheia e você irá recusar os outros caronistas.");
            cdc.setNButtonText("Cancelar");
            cdc.setPButtonText("Aceitar");
        }
    }

    @OnClick(R.id.remove_request)
    public void rRequest()
    {
        answerRequest = new JoinRequestIDsForJson(usersRequest.get(usersRequest.size()-1).getDbId(), false);
        onRequestAction(answerRequest);
    }

    @OnClick(R.id.finish_bt)
    public void fButton()
    {
        finishRide();
    }

    private void updateRide(String action)
    {
        CaronaeAPI.service().getRide(Integer.toString(idRide))
            .enqueue(new Callback<RideForJson>() {
                @Override
                public void onResponse(Call<RideForJson> call, Response<RideForJson> response) {
                    if (response.isSuccessful()) {
                        rideWithUsers = response.body();
                        if(action.equals("AfterAnswerRequest"))
                        {
                            if(rideWithUsers.getRiders() != null && Integer.parseInt(rideWithUsers.getSlots()) - rideWithUsers.getRiders().size() < 1)
                            {
                                isFull = true;
                            }
                            configureOfferedRide();
                        }
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<RideForJson> call, Throwable t) {

                }
            });
    }

    public void onRequestAction(JoinRequestIDsForJson answerRequest)
    {
        final Activity act = this;
        CaronaeAPI.service().answerJoinRequest(Integer.toString(idRide), answerRequest).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            requestLay.setVisibility(View.GONE);
                            updateRide("AfterAnswerRequest");
                        } else {
                            progressBar.setVisibility(View.GONE);
                            CustomDialogClass cdc = new CustomDialogClass(act, "", null);
                            cdc.show();
                            cdc.setTitleText("Algo deu errado.");
                            cdc.setMessageText("Não foi possível cancelar sua carona. (A conexão à internet talvez esteja inativa.)");
                            cdc.setPButtonText("OK");
                            cdc.enableOnePositiveOption();
                            Util.treatResponseFromServer(response);
                            Log.e("Error ", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        CustomDialogClass cdc = new CustomDialogClass(act, "", null);
                        cdc.show();
                        cdc.setTitleText("Algo deu errado.");
                        cdc.setMessageText("Não foi possível cancelar sua carona. (A conexão à internet talvez esteja inativa.)");
                        cdc.setPButtonText("OK");
                        cdc.enableOnePositiveOption();
                        Log.e("Error ", t.getLocalizedMessage());
                    }
                });
    }

    private void finishRide()
    {
        progressBar.setVisibility(View.VISIBLE);
        CaronaeAPI.service().finishRide(Integer.toString(idRide))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            SharedPref.lastAllRidesUpdate = null;
                            SharedPref.lastMyRidesUpdate = 350;
                            progressBar.setVisibility(View.GONE);
                            backToLast();
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setNumberClickedAction()
    {
        phone_tv.setOnClickListener((View v) -> {
            actionNumberTouch(0);
        });
        phone_tv.setOnLongClickListener((View v) ->{
            actionNumberTouch(1);
            return true;
        });
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
            CustomPhoneDialogClass dialog = new CustomPhoneDialogClass(this, "RideDetailAct", null, rideWithUsers.getDriver().getPhoneNumber());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM;
            dialog.show();
        }
    }

    public void callUserPhone()
    {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + rideWithUsers.getDriver().getPhoneNumber()));
        startActivity(callIntent);
    }

    public void addUserPhone()
    {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, rideWithUsers.getDriver().getName());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, rideWithUsers.getDriver().getPhoneNumber());
        startActivity(intent);
    }

    public void copyUserPhone()
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("PhoneNumber", rideWithUsers.getDriver().getPhoneNumber());
        clipboard.setPrimaryClip(clip);
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
