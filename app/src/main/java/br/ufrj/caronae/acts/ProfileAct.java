package br.ufrj.caronae.acts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.HistoryRideCountForJson;
import butterknife.Bind;
import butterknife.ButterKnife;
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
        User user = new Gson().fromJson(user2, User.class);

        name_tv.setText(user.getName());
        profile_tv.setText(user.getProfile());
        course_tv.setText(user.getCourse());
        phone_tv.setText(user.getPhoneNumber());
        Picasso.with(this).load(user.getProfilePicUrl())
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
            }
        });


        String from = getIntent().getExtras().getString("from");
        if (from != null && !from.equals("requesters")) {
            call_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phone_tv.getText()));
                    startActivity(callIntent);
                }
            });
        } else {
            phone_tv.setVisibility(View.GONE);
            call_tv.setVisibility(View.GONE);
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
