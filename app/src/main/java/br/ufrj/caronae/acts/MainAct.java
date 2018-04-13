package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.firebase.FirebaseTopicsHandler;
import br.ufrj.caronae.frags.AboutFrag;
import br.ufrj.caronae.frags.AllRidesFrag;
import br.ufrj.caronae.frags.FAQFrag;
import br.ufrj.caronae.frags.FalaeFrag;
import br.ufrj.caronae.frags.MyProfileEditFrag;
import br.ufrj.caronae.frags.MyRidesFrag;
import br.ufrj.caronae.frags.OptionsMenuFrag;
import br.ufrj.caronae.frags.RideFilterFrag;
import br.ufrj.caronae.frags.RideSearchFrag;
import br.ufrj.caronae.frags.RidesHistoryFrag;
import br.ufrj.caronae.frags.TabbedRideOfferFrag;
import br.ufrj.caronae.frags.TermsOfUseFrag;
import br.ufrj.caronae.models.User;
import br.ufrj.caronae.models.modelsforjson.RideFiltersForJson;

import static br.ufrj.caronae.acts.StartAct.MSG_TYPE_ALERT;
import static br.ufrj.caronae.acts.StartAct.MSG_TYPE_ALERT_HEADER;

public class MainAct extends AppCompatActivity {

    private static final int GPLAY_UNAVAILABLE = 123;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private CallbackManager callbackManager;
    private TextView versionText;

    static ImageButton dissmissFilter;
    static CardView filterCard;
    public static TextView filterText;

    private ArrayList<Class> backstack;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_allrides:
                    if(!SharedPref.NAV_INDICATOR.equals("AllRides")) {
                        selectDrawerItem(item);
                        SharedPref.NAV_INDICATOR = "AllRides";
                    }
                    return true;
                case R.id.navigation_myrides:
                    if(!SharedPref.NAV_INDICATOR.equals("MyRides")) {
                        selectDrawerItem(item);
                        SharedPref.NAV_INDICATOR = "MyRides";
                    }
                    return true;
                case R.id.navigation_menu:
                    if(!SharedPref.NAV_INDICATOR.equals("Menu")) {
                        selectDrawerItem(item);
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        filterCard = (CardView) findViewById(R.id.filter_card);
        filterText = (TextView) findViewById(R.id.filter_text);
        dissmissFilter = (ImageButton) findViewById(R.id.dissmiss_filter);

        startFilterCard();

        configureDissmissFilterButton();

        SharedPref.setChatActIsForeground(false);

        FacebookSdk.sdkInitialize(getApplicationContext());
        getFbCallbackManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Subscripe to topics
        FirebaseTopicsHandler.subscribeFirebaseTopic("user-" + App.getUser().getDbId());
        FirebaseTopicsHandler.subscribeFirebaseTopic(SharedPref.TOPIC_GERAL);

        versionText = (TextView) findViewById(R.id.text_version);
        versionText.setText("Caronae " + Util.getAppVersionName(this));

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

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this,
                mDrawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                //update user pic on drawer if it changed
                String profilePicUrl = SharedPref.getDrawerPic();
                if (!profilePicUrl.equals(App.getUser().getProfilePicUrl())) {
                    if (App.getUser().getProfilePicUrl() != null)
                        profilePicUrl = App.getUser().getProfilePicUrl();
                    else
                        profilePicUrl = "";

                    ImageView user_pic = (ImageView) drawerView.findViewById(R.id.user_pic);

                    RelativeLayout drawerHeader = (RelativeLayout) drawerView.findViewById(R.id.drawer_header_layout);

                    drawerHeader.setOnClickListener((View v) -> showProfileFrag());

                    if (!profilePicUrl.isEmpty()) {
                        Picasso.with(MainAct.this).load(profilePicUrl)
                                .placeholder(R.drawable.user_pic)
                                .error(R.drawable.user_pic)
                                .transform(new RoundedTransformation())
                                .into(user_pic);
                    } else {
                        Picasso.with(MainAct.this).load(R.drawable.user_pic)
                                .placeholder(R.drawable.user_pic)
                                .error(R.drawable.user_pic)
                                .into(user_pic);
                    }

                    SharedPref.saveDrawerPic(profilePicUrl);
                }

                //hide keyboard when nav drawer opens
                InputMethodManager inputMethodManager = (InputMethodManager) MainAct.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                try {
                    //noinspection ConstantConditions
                    inputMethodManager.hideSoftInputFromWindow(MainAct.this.getCurrentFocus().getWindowToken(), 0);
                } catch (NullPointerException e) {
                    Log.e("onDrawerOpened", e.getMessage());
                }
            }
        };
        mDrawer.setDrawerListener(drawerToggle);

        final ActionBar ab = getSupportActionBar();

        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setTitle("");

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        getHeaderView(nvDrawer);

        backstack = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        User user = App.getUser();
        Fragment fragment;
        boolean goToMyRides = getIntent().getBooleanExtra(SharedPref.MY_RIDE_LIST_KEY, false);
        if (user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty() ||
                user.getLocation() == null || user.getLocation().isEmpty()) {
            fragment = new MyProfileEditFrag();
            Util.toast(getString(R.string.act_main_profileIncomplete));
        }
        else if (goToMyRides){
            fragment = new MyRidesFrag();
            backstack.add(new AllRidesFrag().getClass());
        }
        else {
            fragment = new AllRidesFrag();
        }
        if(SharedPref.NAV_INDICATOR.equals("AllRides"))
        {
            fragment = new AllRidesFrag();
        }
        else if(SharedPref.NAV_INDICATOR.equals("MyRides"))
        {
            fragment = new MyRidesFrag();
        }
        else
        {
            fragment = new OptionsMenuFrag();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        backstack.add(fragment.getClass());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SharedPref.FRAGMENT_INDICATOR.equals("")){
            if (SharedPref.FRAGMENT_INDICATOR.equals(MyRidesFrag.class.getName()))
                SharedPref.FRAGMENT_INDICATOR = "";
            showActiveRidesFrag();
        }
    }

    private void getHeaderView(NavigationView nvDrawer) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View nvHeader = inflater.inflate(R.layout.nav_header, null, false);

        TextView name_tv = (TextView) nvHeader.findViewById(R.id.name_tv);
        name_tv.setText(App.getUser().getName());
        TextView course_tv = (TextView) nvHeader.findViewById(R.id.course_tv);
        course_tv.setText(App.getUser().getCourse());
        ImageView user_pic = (ImageView) nvHeader.findViewById(R.id.user_pic);
        String profilePicUrl = App.getUser().getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty())
            Picasso.with(this).load(profilePicUrl)
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .transform(new RoundedTransformation())
                    .into(user_pic);

        nvDrawer.addHeaderView(nvHeader);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;

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
                break;
            case R.id.navigation_myrides:
                hideFilterCard(getBaseContext());
                fragmentClass = MyRidesFrag.class;
                break;
            case R.id.navigation_menu:
                fragmentClass = OptionsMenuFrag.class;
                hideFilterCard(getBaseContext());
                break;
            case R.id.nav_first_fragment:
                fragmentClass = MyProfileEditFrag.class;
                hideFilterCard(getBaseContext());
                break;
            case R.id.nav_second_fragment:
                if(!filterText.getText().equals(""))
                {
                    showFilterCard(getBaseContext());
                }
                else
                {
                    hideFilterCard(getBaseContext());
                }
                fragmentClass = AllRidesFrag.class;

                break;
            case R.id.nav_third_fragment:
                hideFilterCard(getBaseContext());
                fragmentClass = MyRidesFrag.class;
                break;
            case R.id.nav_fifth_fragment:
                hideFilterCard(getBaseContext());
                fragmentClass = RidesHistoryFrag.class;
                break;
            case R.id.nav_sixth_fragment:
                hideFilterCard(getBaseContext());
                fragmentClass = FalaeFrag.class;
                break;
            case R.id.nav_seventh_fragment:
                hideFilterCard(getBaseContext());
                fragmentClass = TermsOfUseFrag.class;
                break;
            case R.id.nav_eigth_fragment:
                hideFilterCard(getBaseContext());
                fragmentClass = AboutFrag.class;
                break;
            case R.id.nav_ninth_fragment:
                hideFilterCard(getBaseContext());
                fragmentClass = FAQFrag.class;
                break;
            case R.id.nav_tenth_fragment:
                hideFilterCard(getBaseContext());
                fragmentClass = null;
                //TODO: Transformar em um intent service, unsubscrive nao ta acontecendo imediatamente
                //Unsubscribe from lists
                App.LogOut();
                break;
            default:
                if(!filterText.getText().equals(""))
                {
                    showFilterCard(getBaseContext());
                }
                else
                {
                    hideFilterCard(getBaseContext());
                }
                fragmentClass = AllRidesFrag.class;
        }

        if (fragmentClass == null)
            finish();
        else {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            backstackSafeCheck();
            backstack.remove(fragmentClass);
            backstack.add(fragmentClass);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
            transaction.replace(R.id.flContent, fragment).commit();

            setTitle(menuItem.getTitle());
            mDrawer.closeDrawers();
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
        setTitle(retrieveTitle(fragmentClass.toString()));
    }

    @Override
    public void onBackPressed() {
        backstackSafeCheck();
        if (!backstack.isEmpty())
            backstack.remove(backstack.size() - 1);
        if (backstack.isEmpty()) {
            finish();
        } else {
            Class fragmentClass = backstack.get(backstack.size() - 1);

            Fragment fragment = null;
            try {
                if(!filterText.getText().equals("") && fragmentClass.equals(AllRidesFrag.class))
                {
                    showFilterCard(getBaseContext());
                }
                else
                {
                    hideFilterCard(getBaseContext());
                }
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BottomNavigationView bNavView = (BottomNavigationView) findViewById(R.id.navigation);
            if(fragmentClass.equals(AllRidesFrag.class))
            {
                if(!bNavView.getMenu().getItem(0).isChecked())
                {
                    SharedPref.NAV_INDICATOR = "AllRides";
                    bNavView.getMenu().getItem(0).setChecked(true);
                }
            }
            else if(fragmentClass.equals(MyRidesFrag.class))
            {
                if(!bNavView.getMenu().getItem(1).isChecked())
                {
                    SharedPref.NAV_INDICATOR = "MyRides";
                    bNavView.getMenu().getItem(1).setChecked(true);
                }
            }
            else if(fragmentClass.equals(OptionsMenuFrag.class))
            {
                if(!bNavView.getMenu().getItem(2).isChecked())
                {
                    SharedPref.NAV_INDICATOR = "Menu";
                    bNavView.getMenu().getItem(2).setChecked(true);
                }
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out);
            transaction.replace(R.id.flContent, fragment).commit();
            setTitle(retrieveTitle(fragmentClass.toString()));
            mDrawer.closeDrawers();
        }
    }

    //Better safe than sorry
    private void backstackSafeCheck() {
        if (backstack == null)
            backstack = new ArrayList<>();
    }

    private int retrieveTitle(String fragmentClass) {
        if (fragmentClass.equals(MyProfileEditFrag.class.toString()))
            return R.string.frag_profile_title;
        if (fragmentClass.equals(AllRidesFrag.class.toString()))
            return R.string.frag_allrides_title;
        if (fragmentClass.equals(MyRidesFrag.class.toString()))
            return R.string.frag_myactiverides_title;
        if (fragmentClass.equals(RidesHistoryFrag.class.toString()))
            return R.string.frag_history_title;
        if (fragmentClass.equals(FalaeFrag.class.toString()))
            return R.string.frag_falae_title;
        if (fragmentClass.equals(TabbedRideOfferFrag.class.toString()))
            return R.string.act_main_setRideOfferFragTitle;
        if (fragmentClass.equals(RideSearchFrag.class.toString()))
            return R.string.frag_searchride_title;
        if (fragmentClass.equals(AboutFrag.class.toString()))
            return R.string.frag_about_title;
        return R.string.app_name;
    }

    // Sync the toggle state after onRestoreInstanceState has occurred
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    // Pass any configuration change to the drawer toggles
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //Controls the actions of the buttons of search and filter that are present in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_frag_bt) {
            backstackSafeCheck();
            backstack.remove(RideSearchFrag.class);
            backstack.add(RideSearchFrag.class);
            hideFilterCard(getBaseContext());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
            transaction.replace(R.id.flContent, new RideSearchFrag()).commit();
            setTitle(item.getTitle());
        } else if (item.getItemId() == R.id.filter_frag_bt){
            backstackSafeCheck();
            backstack.remove(RideFilterFrag.class);
            backstack.add(RideFilterFrag.class);
            hideFilterCard(getBaseContext());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
            transaction.replace(R.id.flContent, new RideFilterFrag()).commit();
            setTitle(item.getTitle());
        }

        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFbCallbackManager().onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPLAY_UNAVAILABLE) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.gplay_unavailable)
                    .setPositiveButton(getString(R.string.act_main_oknogplay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //
                        }
                    }).show();
        }
    }

    public CallbackManager getFbCallbackManager() {
        if (callbackManager == null)
            callbackManager = CallbackManager.Factory.create();

        return callbackManager;
    }

    public void showRideOfferFrag() {
        backstackSafeCheck();
        backstack.remove(TabbedRideOfferFrag.class);
        backstack.add(TabbedRideOfferFrag.class);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
        transaction.replace(R.id.flContent, new TabbedRideOfferFrag()).commit();
        hideFilterCard(getBaseContext());
        setTitle(getString(R.string.act_main_setRideOfferFragTitle));
    }

    public void showActiveRidesFrag() {
        backstackSafeCheck();
        backstack.remove(MyRidesFrag.class);
        backstack.add(MyRidesFrag.class);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out);
        transaction.replace(R.id.flContent, new MyRidesFrag()).commit();
        hideFilterCard(getBaseContext());
        setTitle(getString(R.string.frag_myactiverides_title));
    }

    public void showRidesOfferListFrag() {
        backstackSafeCheck();
        backstack.remove(AllRidesFrag.class);
        backstack.add(AllRidesFrag.class);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out);
        transaction.replace(R.id.flContent, new AllRidesFrag()).commit();
        showFilterCard(getBaseContext());
        setTitle(getString(R.string.frag_allrides_title));
    }

    private void showProfileFrag() {
        backstackSafeCheck();
        backstack.remove(MyProfileEditFrag.class);
        backstack.add(MyProfileEditFrag.class);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out);
        transaction.replace(R.id.flContent, new MyProfileEditFrag()).commit();
        setTitle(retrieveTitle(MyProfileEditFrag.class.toString()));
        mDrawer.closeDrawers();
    }

    public void removeFromBackstack(Object o){
        backstack.remove(o.getClass());
    }

    private void configureDissmissFilterButton(){
        dissmissFilter.setOnClickListener((View v) -> {
            SharedPref.saveFilterPref(null);
            filterText.setText("");
            hideFilterCard(getApplicationContext());
        });
    }

    public static void updateFilterCard(Context context, String filtersJsonString){
        if (!filtersJsonString.equals(SharedPref.MISSING_PREF)) {
            RideFiltersForJson filters = loadFilters(filtersJsonString);
            String resumeLocation = filters.getResumeLocation();
            String center = filters.getCenter();
            String campus = filters.getCampus();
            String zone = filters.getZone();
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
    }

    private static RideFiltersForJson loadFilters(String filters) {
        return new Gson().fromJson(filters, RideFiltersForJson.class);
    }

    private void startFilterCard() {
        String filtersJsonString = SharedPref.getFiltersPref();
        if (!filtersJsonString.equals(SharedPref.MISSING_PREF)) {
            RideFiltersForJson filters = loadFilters(filtersJsonString);
            String resumeLocations = filters.getResumeLocation();
            String center = filters.getCenter();
            String campus = filters.getCampus();
            String zone = filters.getZone();
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
                if(center.equals("Todos os Campi"))
                {
                    cardText = new SpannableString(filtering + resumeLocations);
                }
                else if(zone.equals("Todos os Bairros"))
                {
                    cardText = new SpannableString(filtering + center);
                }
                else {
                    cardText = new SpannableString(filtering + center + ", " + resumeLocations);
                }
                cardText.setSpan(new StyleSpan(Typeface.BOLD), 0, filtering.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                filterText.setText(cardText);
                filterCard.setVisibility(View.VISIBLE);
            } else
            {
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
                filterCard.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void showFilterCard(final Context context){
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
}
