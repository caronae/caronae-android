package br.ufrj.caronae.acts;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.SwipeDismissBaseActivity;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.adapters.RidersAdapter;
import br.ufrj.caronae.frags.MyRidesFrag;
import br.ufrj.caronae.models.ChatAssets;
import br.ufrj.caronae.models.RideEndedEvent;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveRideAct extends SwipeDismissBaseActivity {

    @BindView(R.id.user_pic)
    public ImageView user_pic;
    @BindView(R.id.share_ride_button)
    public ImageButton shareBt;

    @BindView(R.id.location_tv)
    public TextView location_tv;
    @BindView(R.id.name_tv)
    public TextView name_tv;
    @BindView(R.id.profile_tv)
    public TextView profile_tv;
    @BindView(R.id.course_tv)
    public TextView course_tv;
    @BindView(R.id.phoneNumber_tv)
    public TextView phoneNumber_tv;
    @BindView(R.id.time_tv)
    public TextView time_tv;
    @BindView(R.id.date_tv)
    public TextView date_tv;
    @BindView(R.id.way_tv)
    public TextView way_tv;
    @BindView(R.id.way_text_tv)
    public TextView way_text_tv;
    @BindView(R.id.place_tv)
    public TextView place_tv;
    @BindView(R.id.place_text_tv)
    public TextView place_text_tv;
    @BindView(R.id.carModel_tv)
    public TextView carModel_tv;
    @BindView(R.id.carColor_tv)
    public TextView carColor_tv;
    @BindView(R.id.carPlate_tv)
    public TextView carPlate_tv;
    @BindView(R.id.description_tv)
    public TextView description_tv;
    @BindView(R.id.description_text_tv)
    public TextView description_text_tv;

    @BindView(R.id.chat_bt)
    public com.github.clans.fab.FloatingActionButton chat_bt;

    @BindView(R.id.finish_bt)
    public Button finish_bt;
    @BindView(R.id.leave_bt)
    public Button leave_bt;

    @BindView(R.id.lay1)
    public RelativeLayout lay1;
    @BindView(R.id.ridersList)
    public RecyclerView ridersList;

    private String rideId2;

    private boolean notVisible;
    private boolean scheduledToClose;

    RideForJson rideWithUsers;

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

        rideWithUsers = getIntent().getExtras().getParcelable("ride");

        if (rideWithUsers == null) {
            Util.toast(getString(R.string.act_activeride_rideNUll));
            finish();
            return;
        }

        final User driver = rideWithUsers.getDriver();

        final boolean isDriver = driver.getDbId() == App.getUser().getDbId();

        int color = Util.getColors(rideWithUsers.getZone());

        lay1.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
        chat_bt.setColorNormal(color);
        chat_bt.setColorPressed(color);
        finish_bt.setTextColor(color);

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
                    .transform(new RoundedTransformation())
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
        CircleImageView profileCircledImage = (CircleImageView)user_pic;
        location_tv.setText(location);
        name_tv.setText(driver.getName());
        profile_tv.setText(driver.getProfile());
        if (rideWithUsers.getRoute().equals("")){
            way_tv.setVisibility(View.GONE);
            way_text_tv.setVisibility(View.GONE);
        } else {
            way_tv.setText(rideWithUsers.getRoute());
        }
        if (rideWithUsers.getPlace().equals("")){
            place_text_tv.setVisibility(View.GONE);
            place_tv.setVisibility(View.GONE);
        } else {
            place_tv.setText(rideWithUsers.getPlace());
        }
        phoneNumber_tv.setText(driver.getPhoneNumber());
        course_tv.setText(driver.getCourse());
        if (rideWithUsers.isGoing())
            time_tv.setText(getString(R.string.arrivingAt, Util.formatTime(rideWithUsers.getTime())));
        else
            time_tv.setText(getString(R.string.leavingAt, Util.formatTime(rideWithUsers.getTime())));
        time_tv.setTextColor(color);
        date_tv.setText(Util.formatBadDateWithoutYear(rideWithUsers.getDate()));
        date_tv.setTextColor(color);
        profileCircledImage.setBorderColor(color);
        carModel_tv.setText(driver.getCarModel());
        carColor_tv.setText(driver.getCarColor());
        carPlate_tv.setText(driver.getCarPlate());
        if (rideWithUsers.getDescription().equals("")){
            description_text_tv.setVisibility(View.GONE);
            description_tv.setVisibility(View.GONE);
        } else {
            description_tv.setText(rideWithUsers.getDescription());
        }

        ridersList.setAdapter(new RidersAdapter(rideWithUsers.getRiders(), this));
        ridersList.setHasFixedSize(true);
        ridersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        chat_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ChatAssets> l = ChatAssets.find(ChatAssets.class, "ride_id = ?", rideWithUsers.getDbId() + "");
                if (l == null || l.isEmpty())
                    new ChatAssets(rideWithUsers.getDbId() + "", location, color, color,
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
            leave_bt.setText(R.string.act_activeride_quitBtn);
        }
        leave_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        if (!isDriver) {
            finish_bt.setVisibility(View.GONE);
        }
        finish_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        phoneNumber_tv.setOnClickListener((View v) -> {
            actionNumberTouch(0, driver);
        });
        phoneNumber_tv.setOnLongClickListener((View v) ->{
            actionNumberTouch(1, driver);
            return true;
        });

        App.getBus().register(this);
        notVisible = false;
        scheduledToClose = false;

        configureShareBt();
    }

    private void configureShareBt() {
        if (Util.getStringDateInMillis(rideWithUsers.getTime() + " " + rideWithUsers.getDate()) < (new Date()).getTime()){
            shareBt.setVisibility(View.GONE);
        }else {
            shareBt.setVisibility(View.VISIBLE);
            shareBt.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    //Define actions when the user holds or touch the number on ActiveHide Activity
    private void actionNumberTouch(int action, User user)
    {
        if(action == 0)
        {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber_tv.getText()));
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
                            callIntent.setData(Uri.parse("tel:" + phoneNumber_tv.getText()));
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
    public void onBackPressed() {
        super.onBackPressed();
        SharedPref.FRAGMENT_INDICATOR = MyRidesFrag.class.getName();
        overridePendingTransition(R.anim.anim_left_slide_in, R.anim.anim_right_slide_out);
    }
}
