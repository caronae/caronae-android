package br.ufrj.caronae.acts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.RideRequest;
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
        carModel_tv.setText(driver.getCarModel());
        carColor_tv.setText(driver.getCarColor());
        carPlate_tv.setText(driver.getCarPlate());
        description_tv.setText(rideWithUsers.getDescription());

        join_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getNetworkService().requestJoin(new RideIdForJson(rideWithUsers.getDbId()), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Util.toast(R.string.requestSent);

                        RideRequest rideRequest = new RideRequest(rideWithUsers.getDbId(), rideWithUsers.isGoing(), rideWithUsers.getDate());
                        rideRequest.save();

                        join_bt.setVisibility(View.INVISIBLE);
                        App.getBus().post(rideRequest);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Util.toast(R.string.errorRequestSent);

                        Log.e("requestJoin", error.getMessage());
                    }
                });
            }
        });
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
