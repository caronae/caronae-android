package br.ufrj.caronae.acts;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

public class RideDetailAct extends SwipeDismissBaseActivity {

    @BindView(R.id.user_pic)
    public ImageView user_pic;
    @BindView(R.id.share_ic)
    public ImageView share_ic;
    @BindView(R.id.clock)
    public ImageView clock;
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
    @BindView(R.id.requested_dt)
    public TextView requested_dt;
    @BindView(R.id.mutual_friends_tv)
    TextView mFriends_tv;
    @BindView(R.id.cancel_ride)
    TextView cancelRide_bt;
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

    @BindView(R.id.join_bt)
    public Button join_bt;

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

    @BindView(R.id.riders_profile)
    LinearLayout ridersProfile;

    @BindView(R.id.loading_in_progress)
    ProgressBar progressBar;

    RideForJson rideWithUsers;

    int zoneColorInt, idRide;
    boolean requested;
    String fromWhere = "", isGoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean starting = getIntent().getBooleanExtra("starting", false);
        if(starting)
        {
            overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        }
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
                back_tv.setText(R.string.title_ride_search);
            }
            else if(fromWhere.equals(getResources().getString(R.string.title_myrides)))
            {
                back_tv.setText(R.string.title_myrides);
            }
        }
        if (!startWithLink()) {
            rideWithUsers = getIntent().getExtras().getParcelable("ride");
            idRide = getIntent().getIntExtra("rideId", 0);
            configureActivityWithRide(rideWithUsers, false);
        } else {
            configureActivityWithLink();
            if(rideWithUsers.getDriver().getDbId() == App.getUser().getDbId())
            {
                back_tv.setText(R.string.title_myrides);
            }
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
        Intent intent;
        switch (back_tv.getText().toString())
        {
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
                    intent.putExtra("user", new Gson().toJson(driver));
                    intent.putExtra("from", "rideoffer");
                    intent.putExtra("fromAnother", true);
                    intent.putExtra("requested", requested);
                    intent.putExtra("ride", rideWithUsers);
                    intent.putExtra("fromWhere", fromWhere);
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

        accept_tv.setTextColor(zoneColorInt);
        GradientDrawable strokeShare_bt = (GradientDrawable)shareButton.getBackground();
        strokeShare_bt.setStroke(2, zoneColorInt);
        GradientDrawable strokeRemove_bt = (GradientDrawable)removeRequest.getBackground();
        strokeRemove_bt.setStroke(2, zoneColorInt);
        GradientDrawable strokeAccept_bt = (GradientDrawable)acceptRequest.getBackground();
        strokeAccept_bt.setStroke(2, zoneColorInt);
        time_dt.setText(dateDescription);
        time_dt.setTextColor(zoneColorInt);
        if (rideWithUsers.getDescription().equals("")) {
            description_dt.setText("- - -");
        } else {
            description_dt.setText(rideWithUsers.getDescription());
        }
        if (isDriver) {
            configureOfferedRide(rideWithUsers);
        }else{
            if (requested) {
                join_bt.setVisibility(View.GONE);
                requested_dt.setVisibility(View.VISIBLE);
                inviteLay.setVisibility(View.VISIBLE);
            } else {
                if (isFull) {
                    join_bt.setText(R.string.full_ride);
                    join_bt.setClickable(false);
                } else {
                    join_bt.setClickable(true);
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
                                customDialogClass = new CustomDialogClass(activity, "RideDetailAct", null);
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
        else {
            rideId = params.get(1);
        }
        idRide = Integer.parseInt(rideId);
        CaronaeAPI.service(this).getRide(rideId)
                .enqueue(new Callback<RideForJson>() {
                    @Override
                    public void onResponse(Call<RideForJson> call, Response<RideForJson> response) {
                        if (response.isSuccessful()) {
                            RideForJson ride = response.body();
                            rideWithUsers = ride;
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
        backToLast();
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
                            inviteLay.setVisibility(View.VISIBLE);
                            requested_dt.startAnimation(getAnimationForResquestedText());
                            SharedPref.lastMyRidesUpdate = 300;
                            SharedPref.lastAllRidesUpdate = 300;
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

    private void configureOfferedRide(RideForJson ride)
    {
        rideWithUsers = ride;

        String plate = ride.getDriver().getCarPlate().substring(0,3)+"-"+ride.getDriver().getCarPlate().substring(3);
        plate_tv.setText(plate);
        carModel_tv.setText(ride.getDriver().getCarModel());
        carColor_tv.setText(ride.getDriver().getCarColor());
        joinLayout.setVisibility(View.GONE);
        ridersLayout.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String getCurrentDateTime = sdf.format(calendar.getTime());
        String time = ride.getDate()+" "+ride.getTime().substring(0,ride.getTime().length()-3);
        Util.debug(getCurrentDateTime);
        Util.debug(time);
        if (getCurrentDateTime.compareTo(time) < 0)
        {
            //Future
            cancelRide_bt.setVisibility(View.VISIBLE);
        }
        else
        {
            //Past
            cancelRide_bt.setVisibility(View.GONE);
        }

        chat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        });
        chat_bt.setVisibility(View.VISIBLE);
        carDetails.setVisibility(View.VISIBLE);
        if(ride.getRiders() != null && ride.getRiders().size() > 0)
        {
            ridersProfile.setVisibility(View.VISIBLE);
            noRiders.setVisibility(View.GONE);
            CircleImageView[] riderPhoto = {findViewById(R.id.rider_0), findViewById(R.id.rider_1), findViewById(R.id.rider_2), findViewById(R.id.rider_3), findViewById(R.id.rider_4), findViewById(R.id.rider_5)};
            for(int i = 0; i < ride.getRiders().size(); i++)
            {
                if (ride.getRiders().get(i).getProfilePicUrl() != null && !ride.getRiders().get(i).getProfilePicUrl().isEmpty()) {
                    Picasso.with(getBaseContext()).load(ride.getRiders().get(i).getProfilePicUrl())
                            .placeholder(R.drawable.user_pic)
                            .error(R.drawable.user_pic)
                            .transform(new RoundedTransformation())
                            .into(riderPhoto[i]);
                } else {
                    riderPhoto[i].setImageResource(R.drawable.user_pic);
                }
                riderPhoto[i].setVisibility(View.VISIBLE);
                final User currentRider = ride.getRiders().get(i);
                riderPhoto[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentRider.getDbId() != App.getUser().getDbId()) { //dont allow user to open own profile
                            Intent intent = new Intent(getApplicationContext(), ProfileAct.class);
                            intent.putExtra("user", new Gson().toJson(currentRider));
                            intent.putExtra("from", "rideoffer");
                            intent.putExtra("fromAnother", true);
                            intent.putExtra("requested", requested);
                            intent.putExtra("ride", rideWithUsers);
                            intent.putExtra("fromWhere", fromWhere);
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

    public void cancel()
    {
        progressBar.setVisibility(View.VISIBLE);
        if( rideWithUsers.getRoutineId() == null || rideWithUsers.getRoutineId().isEmpty() || rideWithUsers.getDriver().getDbId() != App.getUser().getDbId()) {
            leaveRide(idRide);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
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
        CaronaeAPI.service(this).deleteAllRidesFromRoutine(routineId).
            enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        SharedPref.lastAllRidesUpdate = 300;
                        SharedPref.lastMyRidesUpdate = 300;
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
        CaronaeAPI.service(this).leaveRide(Integer.toString(rideId)).
            enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        SharedPref.lastAllRidesUpdate = 300;
                        SharedPref.lastMyRidesUpdate = 300;
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
        Intent intent;
        switch (back_tv.getText().toString())
        {
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
            new ChatAssets(Integer.toString(idRide), location, color, color,
                    Util.formatBadDateWithoutYear(rideWithUsers.getDate()),
                    Util.formatTime(rideWithUsers.getTime())).save();

        Intent intent = new Intent(RideDetailAct.this, ChatAct.class);
        intent.putExtra("rideId", Integer.toString(idRide));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }
}
