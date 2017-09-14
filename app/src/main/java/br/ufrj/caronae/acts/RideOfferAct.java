package br.ufrj.caronae.acts;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SwipeDismissBaseActivity;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.frags.AllRidesFrag;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideOfferAct extends SwipeDismissBaseActivity {

    @Bind(R.id.user_pic)
    public ImageView user_pic;
    @Bind(R.id.location_dt)
    public TextView location_dt;
    @Bind(R.id.name_dt)
    public TextView name_dt;
    @Bind(R.id.profile_dt)
    public TextView profile_dt;
    @Bind(R.id.course_dt)
    public TextView course_dt;
    @Bind(R.id.time_dt)
    public TextView time_dt;
    @Bind(R.id.date_dt)
    public TextView date_dt;
    @Bind(R.id.join_bt)
    public Button join_bt;
    @Bind(R.id.way_dt)
    public TextView way_dt;
    @Bind(R.id.way_text_frame)
    public CardView way_text_frame;
    @Bind(R.id.place_dt)
    public TextView place_dt;
    @Bind(R.id.place_text_frame)
    CardView place_text_frame;
    @Bind(R.id.description_dt)
    public TextView description_dt;
    @Bind(R.id.description_text_frame)
    CardView description_text_frame;
    @Bind(R.id.requested_dt)
    public TextView requested_dt;
    @Bind(R.id.share_ride_button)
    ImageButton shareButton;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.main_layout)
    RelativeLayout mainLayout;
    @Bind(R.id.progress_bar_layout)
    FrameLayout progressBarLayout;

    CoordinatorLayout coordinatorLayout;

    RideForJson rideWithUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);

        setContentView(R.layout.dialog_ride_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.detail_coordinator_layout);

        if (!startWithLink()) {
            rideWithUsers = getIntent().getExtras().getParcelable("ride");
            configureActivityWithRide(rideWithUsers, false);
        } else {
            configureActivityWithLink();
        }
    }

    private boolean startWithLink() {
        Uri uri = getIntent().getData();
        if (uri == null)
            return false;
        return true;
    }

    private void createChatAssets(RideForJson rideWithUsers) {

        Context context = this.getApplicationContext();

        int color = 0, bgRes = 0;
        if (rideWithUsers.getZone().equals("Centro")) {
            color = ContextCompat.getColor(context, R.color.zone_centro);
            bgRes = R.drawable.bg_bt_raise_zone_centro;
        }
        if (rideWithUsers.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(context, R.color.zone_sul);
            bgRes = R.drawable.bg_bt_raise_zone_sul;
        }
        if (rideWithUsers.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(context, R.color.zone_oeste);
            bgRes = R.drawable.bg_bt_raise_zone_oeste;
        }
        if (rideWithUsers.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(context, R.color.zone_norte);
            bgRes = R.drawable.bg_bt_raise_zone_norte;
        }
        if (rideWithUsers.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(context, R.color.zone_baixada);
            bgRes = R.drawable.bg_bt_raise_zone_baixada;
        }
        if (rideWithUsers.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(context, R.color.zone_niteroi);
            bgRes = R.drawable.bg_bt_raise_zone_niteroi;
        }
        if (rideWithUsers.getZone().equals("Outros")) {
            color = ContextCompat.getColor(context, R.color.zone_outros);
            bgRes = R.drawable.bg_bt_raise_zone_outros;
        }

        final String location;
        if (rideWithUsers.isGoing())
            location = rideWithUsers.getNeighborhood() + " ➜ " + rideWithUsers.getHub();
        else
            location = rideWithUsers.getHub() + " ➜ " + rideWithUsers.getNeighborhood();

        final int finalColor = color, finalBgRes = bgRes;

        List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideWithUsers.getDbId() + "");
        if (l == null || l.isEmpty())
            new ChatAssets(rideWithUsers.getDbId() + "", location, finalColor, finalBgRes,
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
        super.onBackPressed();
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
        final boolean requested = getIntent().getBooleanExtra("requested", false);
        if (rideWithUsers == null) {
            Util.toast(getString(R.string.act_activeride_rideNUll));
            finish();
        }

        AllRidesFrag.setPageThatWas(rideWithUsers.isGoing());


        final User driver = rideWithUsers.getDriver();

        final boolean isDriver = driver.getDbId() == App.getUser().getDbId();

        int color = Util.getColorbyZone(rideWithUsers.getZone());
        join_bt.setBackgroundColor(color);
        location_dt.setTextColor(color);

        final String location;
        if (rideWithUsers.isGoing())
            location = rideWithUsers.getNeighborhood() + " ➜ " + rideWithUsers.getHub();
        else
            location = rideWithUsers.getHub() + " ➜ " + rideWithUsers.getNeighborhood();

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
                    startActivity(intent);
                }
            }
        });
        location_dt.setText(location);
        name_dt.setText(driver.getName());
        profile_dt.setText(driver.getProfile());
        if (rideWithUsers.getRoute().equals("")) {
            way_text_frame.setVisibility(View.GONE);
        } else {
            way_dt.setText(rideWithUsers.getRoute());
        }
        if (rideWithUsers.getPlace().equals("")) {
            place_text_frame.setVisibility(View.GONE);
        } else {
            place_dt.setText(rideWithUsers.getPlace());
        }
        course_dt.setText(driver.getCourse());
        if (rideWithUsers.isGoing())
            time_dt.setText(getString(R.string.arrivingAt, Util.formatTime(rideWithUsers.getTime())));
        else
            time_dt.setText(getString(R.string.leavingAt, Util.formatTime(rideWithUsers.getTime())));
        time_dt.setTextColor(color);
        date_dt.setText(Util.formatBadDateWithoutYear(rideWithUsers.getDate()));
        date_dt.setTextColor(color);
        if (rideWithUsers.getDescription().equals("")) {
            description_text_frame.setVisibility(View.GONE);
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
                    join_bt.setText("CARONA CHEIA");
                    join_bt.setClickable(false);
                } else {
                    join_bt.setClickable(true);
                    final Context context = this;
                    join_bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            List<ActiveRide> list = ActiveRide.find(ActiveRide.class, "date = ? and going = ?", rideWithUsers.getDate(), rideWithUsers.isGoing() ? "1" : "0");
                            if (list != null && !list.isEmpty()) {
                                Util.toast(getString(R.string.act_rideOffer_rideConflict));
                                return;
                            }

                            com.rey.material.app.Dialog.Builder builder = new SimpleDialog.Builder(R.style.SlideInDialog) {

                                @Override
                                protected void onBuildDone(com.rey.material.app.Dialog dialog) {
                                    dialog.layoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dialog.getWindow().getAttributes().windowAnimations = R.style.SlideInRightDialog;
                                }

                                @Override
                                public void onPositiveActionClicked(com.rey.material.app.DialogFragment fragment) {
                                    final ProgressDialog pd = ProgressDialog.show(context, "", getString(R.string.wait), true, true);
                                    App.getNetworkService(context).requestJoin(new RideIdForJson(rideWithUsers.getDbId()))
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

                                                        pd.dismiss();
                                                        Util.snack(coordinatorLayout, getResources().getString(R.string.requestSent));
                                                    } else {
                                                        Util.treatResponseFromServer(response);
                                                        pd.dismiss();
                                                        Util.snack(coordinatorLayout, getResources().getString(R.string.errorRequestSent));
                                                        Log.e("requestJoin", response.message());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    pd.dismiss();
                                                    Util.snack(coordinatorLayout, getResources().getString(R.string.requestSent));
                                                    Log.e("requestJoin", t.getMessage());
                                                }
                                            });

                                    super.onPositiveActionClicked(fragment);
                                }

                                @Override
                                public void onNegativeActionClicked(com.rey.material.app.DialogFragment fragment) {
                                    super.onNegativeActionClicked(fragment);
                                }
                            };

                            ((SimpleDialog.Builder) builder).message(getString(R.string.act_rideOffer_requestWarn))
                                    .title(getString(R.string.attention))
                                    .positiveAction(getString(R.string.ok))
                                    .negativeAction(getString(R.string.cancel));

                            com.rey.material.app.DialogFragment fragment = com.rey.material.app.DialogFragment.newInstance(builder);
                            fragment.show(getSupportFragmentManager(), "a");
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
                Intent intent = new Intent(this, MainAct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        else
            rideId = params.get(1);

        mainLayout.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.VISIBLE);
        App.getNetworkService(this).getRide(rideId)
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
                                configureActivityWithRide(ride, ride.getAvailableSlots() == 0 ? true : false);
                                mainLayout.setVisibility(View.VISIBLE);
                                progressBarLayout.setVisibility(View.GONE);
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

    private void showCustomDialog(String header, String text) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_alert_dialog);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(header);
        TextView body = (TextView) dialog.findViewById(R.id.body);
        body.setText(text);

        android.widget.Button okButton = (android.widget.Button) dialog.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.inst(), MainAct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                App.inst().startActivity(intent);
            }
        });

        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
