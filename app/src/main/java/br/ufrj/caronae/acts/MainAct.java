package br.ufrj.caronae.acts;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.data.ImageSaver;
import br.ufrj.caronae.R;
import br.ufrj.caronae.customizedviews.RoundedTransformation;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.frags.AllRidesFrag;
import br.ufrj.caronae.frags.MyRidesFrag;
import br.ufrj.caronae.frags.OptionsMenuFrag;
import br.ufrj.caronae.frags.RideFilterFrag;
import br.ufrj.caronae.frags.RideOfferFrag;
import br.ufrj.caronae.frags.RideSearchFrag;
import br.ufrj.caronae.frags.RidesHistoryFrag;
import br.ufrj.caronae.httpapis.CaronaeAPI;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.MyRidesForJson;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import retrofit2.Call;
import retrofit2.Response;

import static br.ufrj.caronae.acts.StartAct.MSG_TYPE_ALERT;
import static br.ufrj.caronae.acts.StartAct.MSG_TYPE_ALERT_HEADER;

public class MainAct extends AppCompatActivity {

    private static final int GPLAY_UNAVAILABLE = 123;

    private CallbackManager callbackManager;
    private AllRidesFrag allRidesFrag;

    ImageButton dissmissFilter;
    CardView filterCard;
    public RelativeLayout secondary;
    public TextView filterText;
    public BottomNavigationView navigation;
    TextView cancel_bt;
    public ImageView logo;
    public TextView title;
    boolean backToMain;

    private ArrayList<Class> backstack;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_allrides:
                    if(!SharedPref.NAV_INDICATOR.equals("AllRides")) {
                        selectDrawerItem(item, false);
                        SharedPref.NAV_INDICATOR = "AllRides";
                    }
                    return true;
                case R.id.navigation_myrides:
                    if(!SharedPref.NAV_INDICATOR.equals("MyRides")) {
                        selectDrawerItem(item, false);
                        SharedPref.NAV_INDICATOR = "MyRides";
                    }
                    return true;
                case R.id.navigation_menu:
                    if(!SharedPref.NAV_INDICATOR.equals("Menu")) {
                        selectDrawerItem(item, false);
                        SharedPref.NAV_INDICATOR = "Menu";
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allRidesFrag = new AllRidesFrag();
        dissmissFilter = (ImageButton) findViewById(R.id.dismiss_filter);
        filterCard = (CardView) findViewById(R.id.filter_card);
        filterText = (TextView) findViewById(R.id.filter_text);
        startFilterCard();
        configureDismissFilterButton();

        Util.setColors();

        if(App.isUserLoggedIn())
        {
            if (App.getUser().getProfilePicUrl() != null && !App.getUser().getProfilePicUrl().isEmpty())
            {
                saveProfilePhoto();
            }
        }
        setTitle("");

        if(!SharedPref.checkExistence(SharedPref.MYACTIVERIDESID_KEY) || !SharedPref.checkExistence(SharedPref.MYPENDINGRIDESID_KEY))
        {
            CaronaeAPI.service().getMyRides(Integer.toString(App.getUser().getDbId()))
                .enqueue(new retrofit2.Callback<MyRidesForJson>() {
                    @Override
                    public void onResponse(Call<MyRidesForJson> call, Response<MyRidesForJson> response) {
                        if (response.isSuccessful()) {
                            MyRidesForJson data = response.body();
                            List<RideForJson> activeRides = data.getActiveRides();
                            List<RideForJson> offeredRides = data.getOfferedRides();
                            List<RideForJson> pendingRides = data.getPendingRides();
                            SharedPref.OPEN_MY_RIDES = true;
                            SharedPref.MY_RIDES_ACTIVE = activeRides;
                            SharedPref.MY_RIDES_OFFERED = offeredRides;
                            SharedPref.MY_RIDES_PENDING = pendingRides;
                            if(!activeRides.isEmpty()) {
                                List<Integer> aRideId = new ArrayList<>();
                                for(int i = 0; i < activeRides.size(); i++)
                                {
                                    aRideId.add(activeRides.get(i).getId().intValue());
                                }
                                SharedPref.setMyActiveRidesId(aRideId);
                            }
                            if(!pendingRides.isEmpty())
                            {
                                List<Integer> pRideId = new ArrayList<>();
                                for(int i = 0; i < pendingRides.size(); i++)
                                {
                                    pRideId.add(pendingRides.get(i).getId().intValue());
                                }
                                SharedPref.setMyPendingRidesId(pRideId);
                            }
                        } else {
                            Util.treatResponseFromServer(response);
                            Util.debug(response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<MyRidesForJson> call, Throwable t) {
                        Util.debug(t.getMessage());
                    }
                });
        }

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        cancel_bt = findViewById(R.id.cancel_bt);
        secondary = findViewById(R.id.secondaryitems);
        logo = findViewById(R.id.header_image);
        title = findViewById(R.id.title);
        navigation.getMenu().getItem(0).setChecked(false);
        navigation.getMenu().getItem(1).setChecked(false);
        navigation.getMenu().getItem(2).setChecked(false);
        verifyItem();
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyItem();
            }
        });

        SharedPref.setChatActIsForeground(false);

        FacebookSdk.sdkInitialize(getApplicationContext());
        getFbCallbackManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Subscripe to topics
        FirebaseTopicsHandler.subscribeFirebaseTopic("user-" + App.getUser().getDbId());
        FirebaseTopicsHandler.subscribeFirebaseTopic(SharedPref.TOPIC_GERAL);

        String title = PreferenceManager.getDefaultSharedPreferences(this).getString(MSG_TYPE_ALERT_HEADER, "");
        String alert = PreferenceManager.getDefaultSharedPreferences(this).getString(MSG_TYPE_ALERT, "");

        if (!alert.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(alert);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setTitle(title);
            AlertDialog dialog = builder.create();
            dialog.show();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(StartAct.MSG_TYPE_ALERT, "").commit();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(StartAct.MSG_TYPE_ALERT_HEADER, "").commit();
        }

        backstack = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        User user = App.getUser();
        Fragment fragment;
        navigation.getMenu().getItem(0).setChecked(false);
        navigation.getMenu().getItem(1).setChecked(false);
        navigation.getMenu().getItem(2).setChecked(false);
        if(user == null)
        {
            Intent login = new Intent(this, LoginAct.class);
            startActivity(login);
        }
        else if (user.hasIncompleteProfile()) {
            Intent firstLogin = new Intent(this, WelcomeAct.class);
            startActivity(firstLogin);
        }
        else if(!backToMain) {
            switch (SharedPref.NAV_INDICATOR)
            {
                case "MyRides":
                    fragment = new MyRidesFrag();
                    navigation.getMenu().getItem(1).setChecked(true);
                    break;
                case "Menu":
                    fragment = new OptionsMenuFrag();
                    navigation.getMenu().getItem(2).setChecked(true);
                    break;
                default:
                    fragment = allRidesFrag;
                    navigation.getMenu().getItem(0).setChecked(true);
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            backstack.add(fragment.getClass());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!backToMain)
        {
            if (!SharedPref.FRAGMENT_INDICATOR.equals("")) {
                if (SharedPref.FRAGMENT_INDICATOR.equals(MyRidesFrag.class.getName()))
                    SharedPref.FRAGMENT_INDICATOR = "";
                showActiveRidesFrag();
            }
        }
    }

    private void selectDrawerItem(MenuItem menuItem, boolean slideVertical) {
        Menu menu = navigation.getMenu();
        Fragment fragment = null;
        int selectedIndex;
        Class fragmentClass;
        menu.getItem(0).setChecked(false);
        menu.getItem(1).setChecked(false);
        menu.getItem(2).setChecked(false);
        
        switch (menuItem.getItemId()) {
            case R.id.navigation_allrides:
                if(!filterText.getText().equals(""))
                {
                    showFilterCard(getBaseContext());
                }
                else
                {
                    hideFilterCard(getBaseContext());
                }
                fragmentClass = AllRidesFrag.class;
                fragment = allRidesFrag;
                selectedIndex = 0;
                break;
            case R.id.navigation_myrides:
                hideFilterCard(getBaseContext());
                fragmentClass = MyRidesFrag.class;
                selectedIndex = 1;
                break;
            default:
                fragmentClass = OptionsMenuFrag.class;
                hideFilterCard(getBaseContext());
                selectedIndex = 2;
                break;
        }

        menu.getItem(selectedIndex).setChecked(true);

        if (fragmentClass == null)
            finish();
        else {
            if (fragment == null) {
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            backstackSafeCheck();
            backstack.remove(fragmentClass);
            backstack.add(fragmentClass);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(slideVertical) {
                transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
            }
            transaction.replace(R.id.flContent, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        openFragFromNotif(intent);
    }

    private void openFragFromNotif(Intent intent) {
        Class fragmentClass = AllRidesFrag.class;
        String msgType = intent.getStringExtra("msgType");
        if (msgType == null)
            return;
        if (msgType.equals("accepted") ||
                msgType.equals("refused") ||
                msgType.equals("quitter") ||
                msgType.equals("chat") ||
                msgType.equals("cancelled")) {
            fragmentClass = MyRidesFrag.class;
        }
        if (msgType.equals("joinRequest")) {
            fragmentClass = MyRidesFrag.class;
        }
        if (msgType.equals("finished")) {
            fragmentClass = RidesHistoryFrag.class;
        }

        backstack.remove(fragmentClass);
        backstack.add(fragmentClass);
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(secondary.getVisibility() == View.VISIBLE)
        {
            Fragment fragment = null;
            Class fragmentClass;
            if (!backstack.isEmpty())
                backstack.remove(backstack.size() - 1);
            if(title.getText().equals("Filtrar carona"))
            {
                backstack.remove(AllRidesFrag.class);
                backstack.add(AllRidesFrag.class);
                fragmentClass = AllRidesFrag.class;
            }
            else if(title.getText().equals("Criar carona"))
            {
                backstack.remove(MyRidesFrag.class);
                backstack.add(MyRidesFrag.class);
                fragmentClass = MyRidesFrag.class;
            }
            else
            {
                backstack.remove(AllRidesFrag.class);
                backstack.add(AllRidesFrag.class);
                fragmentClass = AllRidesFrag.class;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragmentManager.popBackStack();
            transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
            transaction.replace(R.id.flContent, fragment).commit();
        }
        else {
            backstackSafeCheck();
            if (!backstack.isEmpty())
                backstack.remove(backstack.size() - 1);
            if (backstack.isEmpty()) {
                finish();
            } else {
                Class fragmentClass = backstack.get(backstack.size() - 1);
                Fragment fragment = null;
                try {
                    if (!filterText.getText().equals("") && fragmentClass.equals(AllRidesFrag.class)) {
                        showFilterCard(getBaseContext());
                    } else {
                        hideFilterCard(getBaseContext());
                    }
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BottomNavigationView bNavView = (BottomNavigationView) findViewById(R.id.navigation);
                navigation.getMenu().getItem(0).setChecked(false);
                navigation.getMenu().getItem(1).setChecked(false);
                navigation.getMenu().getItem(2).setChecked(false);
                if (fragmentClass.equals(AllRidesFrag.class)) {
                    if (!bNavView.getMenu().getItem(0).isChecked()) {
                        SharedPref.NAV_INDICATOR = "AllRides";
                        bNavView.getMenu().getItem(0).setChecked(true);
                    }
                } else if (fragmentClass.equals(MyRidesFrag.class)) {
                    if (!bNavView.getMenu().getItem(1).isChecked()) {
                        SharedPref.NAV_INDICATOR = "MyRides";
                        bNavView.getMenu().getItem(1).setChecked(true);
                    }
                } else if (fragmentClass.equals(OptionsMenuFrag.class)) {
                    if (!bNavView.getMenu().getItem(2).isChecked()) {
                        SharedPref.NAV_INDICATOR = "Menu";
                        bNavView.getMenu().getItem(2).setChecked(true);
                    }
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out);
                transaction.replace(R.id.flContent, fragment).commit();
            }
        }
    }

    //Better safe than sorry
    private void backstackSafeCheck() {
        if (backstack == null)
            backstack = new ArrayList<>();
    }

    public void showMainItems()
    {
        if(navigation.getVisibility() == View.INVISIBLE) {
            navigation.setVisibility(View.VISIBLE);
            logo.setVisibility(View.VISIBLE);
        }
        if(secondary.getVisibility() == View.VISIBLE)
        {
            secondary.setVisibility(View.INVISIBLE);
        }
    }

    //Controls the actions of the buttons of search and filter that are present in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        navigation.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        secondary.setVisibility(View.VISIBLE);
        backstackSafeCheck();
        hideFilterCard(getBaseContext());
        Class fragmentClass = null;
        Fragment fragment = null;
        backToMain = true;
        if (item.getItemId() == R.id.search_frag_bt) {
            title.setText("Buscar carona");
            backstack.remove(RideSearchFrag.class);
            backstack.add(RideSearchFrag.class);
            fragmentClass = RideSearchFrag.class;
        } else if (item.getItemId() == R.id.filter_frag_bt){
            title.setText("Filtrar carona");
            backstack.remove(RideFilterFrag.class);
            backstack.add(RideFilterFrag.class);
            fragmentClass = RideFilterFrag.class;
        }
        else if(item.getItemId() == R.id.new_ride_bt) {
            title.setText("Criar carona");
            backstack.remove(RideOfferFrag.class);
            backstack.add(RideOfferFrag.class);
            fragmentClass = RideOfferFrag.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack();
        transaction.setCustomAnimations(R.anim.anim_down_slide_in, R.anim.anim_up_slide_out);
        transaction.replace(R.id.flContent, fragment).commit();
        return super.onOptionsItemSelected(item);
    }

    public CallbackManager getFbCallbackManager() {
        if (callbackManager == null)
            callbackManager = CallbackManager.Factory.create();

        return callbackManager;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFbCallbackManager().onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPLAY_UNAVAILABLE) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.gplay_unavailable)
                    .setPositiveButton(getString(R.string.ok_uppercase), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //
                        }
                    }).show();
        }
    }

    public void showActiveRidesFrag() {
        backstackSafeCheck();
        SharedPref.NAV_INDICATOR = "MyRides";
        backstack.remove(MyRidesFrag.class);
        backstack.add(MyRidesFrag.class);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out);
        transaction.replace(R.id.flContent, new MyRidesFrag()).commit();
        hideFilterCard(getBaseContext());
    }

    private void configureDismissFilterButton(){
        dissmissFilter.setOnClickListener((View v) -> {
            SharedPref.setFilterPref(false);
            filterText.setText("");
            hideFilterCard(getApplicationContext());
            allRidesFrag.needsUpdating();
        });
    }

    public void updateFilterCard(Context context){
        String resumeLocation = "", center = "", campus = "", zone = "";
        if(Util.isZone(SharedPref.getLocationFilter()))
        {
            zone = SharedPref.getLocationFilter();
        }
        else
        {
            if(!SharedPref.getLocationFilter().equals("Todos os Bairros")) {
                resumeLocation = SharedPref.getLocationFilter();
            }
        }

        if(Util.isCampus(SharedPref.getCenterFilter()))
        {
            campus = SharedPref.getCenterFilter();
        }
        else
        {
            if(!SharedPref.getCenterFilter().equals("Todos os Campi")) {
                center = SharedPref.getCenterFilter();
            }
        }
        String filtering = "Filtrando: ";
        SpannableString cardText;

        if (!resumeLocation.equals("")) {
            if (center.equals("")) {
                if (campus.equals("")) {
                    center = "Todos os Campi";
                } else {
                    center = campus;
                }
                if(center.equals("Todos os Campi"))
                {
                    cardText = new SpannableString(filtering + resumeLocation);
                }
                else if(resumeLocation.equals("Todos os Bairros"))
                {
                    cardText = new SpannableString(filtering + center);
                }
                else {
                    cardText = new SpannableString(filtering + center + ", " + resumeLocation);
                }
                cardText.setSpan(new StyleSpan(Typeface.BOLD), 0, filtering.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                filterText.setText(cardText);
            }
        } else if (!zone.equals("")){
            if (center.equals("")) {
                if (campus.equals("")) {
                    center = "Todos os Campi";
                } else {
                    center = campus;
                }
                if(center.equals("Todos os Campi"))
                {
                    cardText = new SpannableString(filtering + zone);
                }
                else if(zone.equals("Todos os Bairros"))
                {
                    cardText = new SpannableString(filtering + center);
                }
                else {
                    cardText = new SpannableString(filtering + center + ", " + zone);
                }
                cardText.setSpan(new StyleSpan(Typeface.BOLD), 0, filtering.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                filterText.setText(cardText);
            }
        }
        showFilterCard(context);
    }

    public void startFilterCard() {
        if(SharedPref.getFiltersPref()) {
            String resumeLocations = "", center = "", campus = "", zone = "";

            if (Util.isZone(SharedPref.getLocationFilter())) {
                zone = SharedPref.getLocationFilter();
            } else {
                if (!SharedPref.getLocationFilter().equals("Todos os Bairros")) {
                    resumeLocations = SharedPref.getLocationFilter();
                }
            }
            if (Util.isCampus(SharedPref.getCenterFilter())) {
                campus = SharedPref.getCenterFilter();
            } else {
                if (!SharedPref.getCenterFilter().equals("Todos os Campi")) {
                    center = SharedPref.getCenterFilter();
                }
            }
            String filtering = "Filtrando: ";
            SpannableString cardText;

            if (!resumeLocations.equals("")) {
                if (center.equals("")) {
                    if (campus.equals("")) {
                        center = "Todos os Campi";
                    } else {
                        center = campus;
                    }
                }
                if (center.equals("Todos os Campi")) {
                    cardText = new SpannableString(filtering + resumeLocations);
                } else if (zone.equals("Todos os Bairros")) {
                    cardText = new SpannableString(filtering + center);
                } else {
                    cardText = new SpannableString(filtering + center + ", " + resumeLocations);
                }
                cardText.setSpan(new StyleSpan(Typeface.BOLD), 0, filtering.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                filterText.setText(cardText);
                filterCard.setVisibility(View.VISIBLE);
            } else {
                if (zone.equals("")) {
                    zone = "Todos os Bairros";
                }
                if (center.equals("")) {
                    if (campus.equals("")) {
                        center = "Todos os Campi";
                    } else {
                        center = campus;
                    }
                }
                if (center.equals("Todos os Campi")) {
                    cardText = new SpannableString(filtering + zone);
                } else if (zone.equals("Todos os Bairros")) {
                    cardText = new SpannableString(filtering + center);
                } else {
                    cardText = new SpannableString(filtering + center + ", " + zone);
                }
                cardText.setSpan(new StyleSpan(Typeface.BOLD), 0, filtering.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                filterText.setText(cardText);
                filterCard.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showFilterCard(final Context context){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_fade_in);
        filterCard.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                filterCard.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        filterCard.startAnimation(animation);
    }

    public void hideFilterCard(final Context context){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                filterCard.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        filterCard.startAnimation(animation);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void saveProfilePhoto()
    {
        try
        {
            if (Util.isNetworkAvailable(getBaseContext()))
            {
                ImageView iv = new ImageView(getApplicationContext());
                iv.setVisibility(View.INVISIBLE);
                Picasso.with(getApplicationContext()).load(App.getUser().getProfilePicUrl())
                        .placeholder(R.drawable.user_pic)
                        .error(R.drawable.user_pic)
                        .transform(new RoundedTransformation())
                        .into(iv, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        BitmapDrawable bmpDrawable = (BitmapDrawable)iv.getDrawable();
                                        Bitmap bitmap = bmpDrawable.getBitmap();
                                        new ImageSaver(getBaseContext()).
                                                setFileName("myProfile.png").
                                                setDirectoryName("images").
                                                save(bitmap);
                                        SharedPref.setSavedPic(true);
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
            }
        }
        catch(Exception e){}
    }

    public void verifyItem()
    {
        switch(SharedPref.NAV_INDICATOR)
        {
            case "AllRides":
                backToMain = false;
                selectDrawerItem(navigation.getMenu().getItem(0), true);
                navigation.getMenu().getItem(0).setChecked(true);
                break;
            case "MyRides":
                backToMain = false;
                selectDrawerItem(navigation.getMenu().getItem(1), true);
                navigation.getMenu().getItem(1).setChecked(true);
                break;
            case "Menu":
                backToMain = false;
                selectDrawerItem(navigation.getMenu().getItem(2), true);
                navigation.getMenu().getItem(2).setChecked(true);
                break;
            default:
                backToMain = false;
                selectDrawerItem(navigation.getMenu().getItem(0), true);
                navigation.getMenu().getItem(0).setChecked(true);
                break;
        }
    }

    public void setCheckedItem()
    {
        switch(SharedPref.NAV_INDICATOR)
        {
            case "AllRides":
                navigation.getMenu().getItem(0).setChecked(true);
                break;
            case "MyRides":
                navigation.getMenu().getItem(1).setChecked(true);
                break;
            case "Menu":
                navigation.getMenu().getItem(2).setChecked(true);
                break;
            default:
                navigation.getMenu().getItem(0).setChecked(true);
                break;
        }
    }
}
