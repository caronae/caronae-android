package br.ufrj.caronae.acts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.RidersAdapter;
import br.ufrj.caronae.asyncs.UnsubGcmTopic;
import br.ufrj.caronae.models.ActiveRide;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.Ride;
import br.ufrj.caronae.models.RideEndedEvent;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import br.ufrj.caronae.models.modelsforjson.RideIdForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ActiveRideAct extends AppCompatActivity {

    @Bind(R.id.user_pic)
    public ImageView user_pic;
    @Bind(R.id.location_tv)
    public TextView location_tv;
    @Bind(R.id.name_tv)
    public TextView name_tv;
    @Bind(R.id.profile_tv)
    public TextView profile_tv;
    @Bind(R.id.course_tv)
    public TextView course_tv;
    @Bind(R.id.phoneNumber_tv)
    public TextView phoneNumber_tv;
    @Bind(R.id.chat_bt)
    public Button chat_bt;
    @Bind(R.id.finish_bt)
    public Button finish_bt;
    @Bind(R.id.time_tv)
    public TextView time_tv;
    @Bind(R.id.date_tv)
    public TextView date_tv;
    @Bind(R.id.leave_bt)
    public Button leave_bt;
    @Bind(R.id.way_tv)
    public TextView way_tv;
    @Bind(R.id.place_tv)
    public TextView place_tv;
    @Bind(R.id.carModel_tv)
    public TextView carModel_tv;
    @Bind(R.id.carColor_tv)
    public TextView carColor_tv;
    @Bind(R.id.carPlate_tv)
    public TextView carPlate_tv;
    @Bind(R.id.description_tv)
    public TextView description_tv;
    @Bind(R.id.lay1)
    public RelativeLayout lay1;
    @Bind(R.id.ridersList)
    public RecyclerView ridersList;

    private String rideId2;

    private boolean notVisible;
    private boolean scheduledToClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_ride);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        final RideForJson rideWithUsers = getIntent().getExtras().getParcelable("ride");

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
        chat_bt.setBackgroundResource(bgRes);

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
                    Intent intent = new Intent(ActiveRideAct.this, ProfileAct.class);
                    intent.putExtra("user", new Gson().toJson(driver));
                    intent.putExtra("from", "activeRides");
                    startActivity(intent);
                }
            }
        });
        location_tv.setText(location);
        name_tv.setText(driver.getName());
        profile_tv.setText(driver.getProfile());
        way_tv.setText(rideWithUsers.getRoute());
        place_tv.setText(rideWithUsers.getPlace());
        phoneNumber_tv.setText(driver.getPhoneNumber());
        course_tv.setText(driver.getCourse());
        if (rideWithUsers.isGoing())
            time_tv.setText(getString(R.string.arrivingAt, Util.formatTime(rideWithUsers.getTime())));
        else
            time_tv.setText(getString(R.string.leavingAt, Util.formatTime(rideWithUsers.getTime())));
        time_tv.setTextColor(color);
        date_tv.setText(Util.formatBadDateWithoutYear(rideWithUsers.getDate()));
        date_tv.setTextColor(color);
        carModel_tv.setText(driver.getCarModel());
        carColor_tv.setText(driver.getCarColor());
        carPlate_tv.setText(driver.getCarPlate());
        description_tv.setText(rideWithUsers.getDescription());

        ridersList.setAdapter(new RidersAdapter(rideWithUsers.getRiders(), this));
        ridersList.setHasFixedSize(true);
        ridersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        final int finalColor = color, finalBgRes = bgRes;
        chat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideWithUsers.getDbId() + "");
                if (l == null || l.isEmpty())
                    new ChatAssets(rideWithUsers.getDbId() + "", location, finalColor, finalBgRes,
                            Util.formatBadDateWithoutYear(rideWithUsers.getDate()),
                            Util.formatTime(rideWithUsers.getTime())).save();

                Intent intent = new Intent(ActiveRideAct.this, ChatAct.class);
                intent.putExtra("rideId", rideWithUsers.getDbId() + "");
                startActivity(intent);
            }
        });

        rideId2 = rideWithUsers.getDbId() + "";
        final String rideId = rideId2;

        if (isDriver) {
            leave_bt.setText(R.string.cancelCaps);
        }
        leave_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        final ProgressDialog pd = ProgressDialog.show(ActiveRideAct.this, "", getString(R.string.wait), true, true);
                        App.getNetworkService().leaveRide(new RideIdForJson(rideWithUsers.getDbId()), new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                pd.dismiss();
                                if (isDriver)
                                    Util.toast(getString(R.string.act_activeride_cancelledRide));
                                else
                                    Util.toast(getString(R.string.act_activeride_quitRide));

                                new UnsubGcmTopic(ActiveRideAct.this, rideId).execute();

                                List<Ride> rides = Ride.find(Ride.class, "db_id = ?", rideId);
                                if (rides != null && !rides.isEmpty())
                                    rides.get(0).delete();

                                ActiveRide.deleteAll(ActiveRide.class, "db_id = ?", rideId);

                                SharedPref.saveRemoveRideFromList(rideId);
                                finish();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                pd.dismiss();
                                Util.toast(R.string.errorRideDeleted);
                                try {
                                    Log.e("leaveRide", error.getMessage());
                                } catch (Exception e) {//sometimes RetrofitError is null
                                    Log.e("leaveRide", e.getMessage());
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
                String title;
                if (isDriver)
                    title = getString(R.string.act_activeRide_sureWantToCancel);
                else
                    title = getString(R.string.act_activeRide_sureWantToQuit);
                builder.title(title)
                        .positiveAction(getString(R.string.ok))
                        .negativeAction(getString(R.string.cancel));

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        });

        if (!isDriver) {
            finish_bt.setVisibility(View.GONE);
        }
        finish_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        final ProgressDialog pd = ProgressDialog.show(ActiveRideAct.this, "", getString(R.string.wait), true, true);
                        App.getNetworkService().finishRide(new RideIdForJson(rideWithUsers.getDbId()), new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                pd.dismiss();
                                Util.toast(R.string.rideFinished);

                                new UnsubGcmTopic(ActiveRideAct.this, rideId).execute();

                                List<Ride> rides = Ride.find(Ride.class, "db_id = ?", rideId);
                                if (rides != null && !rides.isEmpty())
                                    rides.get(0).delete();

                                ActiveRide.deleteAll(ActiveRide.class, "db_id = ?", rideId);

                                SharedPref.saveRemoveRideFromList(rideId);
                                finish();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                pd.dismiss();
                                Util.toast(R.string.errorFinishRide);

                                try {
                                    Log.e("finish_bt", error.getMessage());
                                } catch (Exception e) {//sometimes RetrofitError is null
                                    Log.e("finish_bt", e.getMessage());
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

                builder.title(getString(R.string.act_activeride_sureWantToFinish))
                        .positiveAction(getString(R.string.ok))
                        .negativeAction(getString(R.string.cancel));

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        });

        App.getBus().register(this);
        notVisible = false;
        scheduledToClose = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        App.getBus().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        notVisible = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        notVisible = false;

        if (scheduledToClose)
            showCloseDialog();
    }

    @Subscribe
    public void rideEndedEvent(RideEndedEvent rideEndedEvent) {
        final String rideId = rideEndedEvent.getRideId();
        Log.i("rideEndedEvent", "activerideact" + rideId);

        if (rideId2.equals(rideId)) {
            if (notVisible) {
                scheduledToClose = true;
                return;
            }

            showCloseDialog();
        }
    }

    private void showCloseDialog() {
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                SharedPref.saveRemoveRideFromList(rideId2);
                ActiveRideAct.this.finish();

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.title("Opa...")
                .positiveAction(getString(R.string.ok))
                .contentView(R.layout.rideended);

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
