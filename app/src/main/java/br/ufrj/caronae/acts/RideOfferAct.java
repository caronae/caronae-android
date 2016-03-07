package br.ufrj.caronae.acts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.RideRequestSent;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RideOfferAct extends AppCompatActivity {

    @Bind(R.id.user_pic)
    public ImageView user_pic;
    @Bind(R.id.seeProfile_iv)
    public TextView seeProfile_iv;
    @Bind(R.id.location_tv)
    public TextView location_tv;
    @Bind(R.id.name_tv)
    public TextView name_tv;
    @Bind(R.id.profile_tv)
    public TextView profile_tv;
    @Bind(R.id.course_tv)
    public TextView course_tv;
    @Bind(R.id.time_tv)
    public TextView time_tv;
    @Bind(R.id.date_tv)
    public TextView date_tv;
    @Bind(R.id.join_bt)
    public Button join_bt;
    @Bind(R.id.way_tv)
    public TextView way_tv;
    @Bind(R.id.place_tv)
    public TextView place_tv;
    @Bind(R.id.description_tv)
    public TextView description_tv;
    @Bind(R.id.requested_tv)
    public TextView requested_tv;
    @Bind(R.id.lay1)
    public RelativeLayout lay1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_offer);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        final RideForJson rideWithUsers = getIntent().getExtras().getParcelable("ride");
        final boolean requested = getIntent().getExtras().getBoolean("requested");

        if (rideWithUsers == null) {
            Util.toast(getString(R.string.act_activeride_rideNUll));
            finish();
            return;
        }

        final User driver = rideWithUsers.getDriver();

        final boolean isDriver = driver.getDbId() == App.getUser().getDbId();

        int color = 0, bgRes = 0;
        if (rideWithUsers.getZone().equals("Centro")) {
            color = ContextCompat.getColor(this, R.color.zone_centro);
            bgRes = R.drawable.bg_bt_raise_zone_centro;
        }
        if (rideWithUsers.getZone().equals("Zona Sul")) {
            color = ContextCompat.getColor(this, R.color.zone_sul);
            bgRes = R.drawable.bg_bt_raise_zone_sul;
        }
        if (rideWithUsers.getZone().equals("Zona Oeste")) {
            color = ContextCompat.getColor(this, R.color.zone_oeste);
            bgRes = R.drawable.bg_bt_raise_zone_oeste;
        }
        if (rideWithUsers.getZone().equals("Zona Norte")) {
            color = ContextCompat.getColor(this, R.color.zone_norte);
            bgRes = R.drawable.bg_bt_raise_zone_norte;
        }
        if (rideWithUsers.getZone().equals("Baixada")) {
            color = ContextCompat.getColor(this, R.color.zone_baixada);
            bgRes = R.drawable.bg_bt_raise_zone_baixada;
        }
        if (rideWithUsers.getZone().equals("Grande Niterói")) {
            color = ContextCompat.getColor(this, R.color.zone_niteroi);
            bgRes = R.drawable.bg_bt_raise_zone_niteroi;
        }
        if (rideWithUsers.getZone().equals("Outros")) {
            color = ContextCompat.getColor(this, R.color.zone_outros);
            bgRes = R.drawable.bg_bt_raise_zone_outros;
        }
        lay1.setBackgroundColor(color);
        join_bt.setBackgroundResource(bgRes);

        final String location;
        if (rideWithUsers.isGoing())
            location = rideWithUsers.getNeighborhood() + " ➜ " + rideWithUsers.getHub();
        else
            location = rideWithUsers.getHub() + " ➜ " + rideWithUsers.getNeighborhood();

        String profilePicUrl = driver.getProfilePicUrl();
        if (profilePicUrl == null || profilePicUrl.isEmpty()) {
            Picasso.with(this).load(R.drawable.user_pic)
                    .into(user_pic);
        } else {
            Picasso.with(this).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation(0))
                    .into(user_pic);
        }
        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDriver) {//dont allow user to open own profile
                    Intent intent = new Intent(RideOfferAct.this, ProfileAct.class);
                    intent.putExtra("user", new Gson().toJson(driver));
                    intent.putExtra("from", "rideoffer");
                    startActivity(intent);
                }
            }
        });
        location_tv.setText(location);
        name_tv.setText(driver.getName());
        profile_tv.setText(driver.getProfile());
        way_tv.setText(rideWithUsers.getRoute());
        place_tv.setText(rideWithUsers.getPlace());
        course_tv.setText(driver.getCourse());
        if (rideWithUsers.isGoing())
            time_tv.setText(getString(R.string.arrivingAt, Util.formatTime(rideWithUsers.getTime())));
        else
            time_tv.setText(getString(R.string.leavingAt, Util.formatTime(rideWithUsers.getTime())));
        time_tv.setTextColor(color);
        date_tv.setText(Util.formatBadDateWithoutYear(rideWithUsers.getDate()));
        date_tv.setTextColor(color);
        description_tv.setText(rideWithUsers.getDescription());

        if (isDriver) {
            join_bt.setVisibility(View.INVISIBLE);
            seeProfile_iv.setVisibility(View.GONE);
        } else {
            if (requested) {
                join_bt.setVisibility(View.INVISIBLE);
                requested_tv.setVisibility(View.VISIBLE);
            } else {
                join_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<ActiveRide> list = ActiveRide.find(ActiveRide.class, "date = ? and going = ?", rideWithUsers.getDate(), rideWithUsers.isGoing() ? "1" : "0");
                        if (list != null && !list.isEmpty()) {
                            Util.toast(getString(R.string.act_rideOffer_rideConflict));
                            return;
                        }

                        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

                            @Override
                            protected void onBuildDone(Dialog dialog) {
                                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            }

                            @Override
                            public void onPositiveActionClicked(DialogFragment fragment) {
                                final ProgressDialog pd = ProgressDialog.show(RideOfferAct.this, "", getString(R.string.wait), true, true);
                                App.getNetworkService().requestJoin(new RideIdForJson(rideWithUsers.getDbId()), new Callback<Response>() {
                                    @Override
                                    public void success(Response response, Response response2) {
                                        RideRequestSent rideRequest = new RideRequestSent(rideWithUsers.getDbId(), rideWithUsers.isGoing(), rideWithUsers.getDate());
                                        rideRequest.save();

                                        join_bt.setVisibility(View.INVISIBLE);
                                        requested_tv.setVisibility(View.VISIBLE);
                                        App.getBus().post(rideRequest);

                                        pd.dismiss();
                                        Util.toast(R.string.requestSent);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        pd.dismiss();
                                        Util.toast(R.string.errorRequestSent);

                                        try {
                                            Log.e("requestJoin", error.getMessage());
                                        } catch (Exception e) {//sometimes RetrofitError is null
                                            Log.e("requestJoin", e.getMessage());
                                        }
                                    }
                                });

                                super.onPositiveActionClicked(fragment);
                            }

                            @Override
                            public void onNegativeActionClicked(DialogFragment fragment) {
                                super.onNegativeActionClicked(fragment);
                            }
                        };

                        ((SimpleDialog.Builder) builder).message(getString(R.string.act_rideOffer_requestWarn))
                                .title(getString(R.string.attention))
                                .positiveAction(getString(R.string.ok))
                                .negativeAction(getString(R.string.cancel));

                        DialogFragment fragment = DialogFragment.newInstance(builder);
                        fragment.show(getSupportFragmentManager(), null);
                    }
                });
            }
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
}
