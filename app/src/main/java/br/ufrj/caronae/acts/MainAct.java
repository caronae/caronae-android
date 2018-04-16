package br.ufrj.caronae.acts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
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

    private CallbackManager callbackManager;

    static ImageButton dissmissFilter;
    static CardView filterCard;
    public static TextView filterText;
    public static BottomNavigationView navigation;
    static TextView cancel_bt;

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
        setTitle("");

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        cancel_bt = (TextView)findViewById(R.id.cancel_bt);

        filterCard = (CardView) findViewById(R.id.filter_card);
        filterText = (TextView) findViewById(R.id.filter_text);
        dissmissFilter = (ImageButton) findViewById(R.id.dissmiss_filter);

        startFilterCard();

        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(SharedPref.NAV_INDICATOR)
                {
                    case "AllRides":
                        selectDrawerItem(navigation.getMenu().getItem(0), true);
                        break;
                    case "MyRides":
                        selectDrawerItem(navigation.getMenu().getItem(1), true);
                        break;
                    case "Menu":
                        selectDrawerItem(navigation.getMenu().getItem(2), true);
                        break;
                }
            }
        });

        configureDissmissFilterButton();

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

    private void selectDrawerItem(MenuItem menuItem, boolean slideVertical) {
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
            fragmentManager.popBackStack();
            if(slideVertical) {
                transaction.setCustomAnimations(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
            }
            transaction.replace(R.id.flContent, fragment).commit();
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
        }
    }

    //Better safe than sorry
    private void backstackSafeCheck() {
        if (backstack == null)
            backstack = new ArrayList<>();
    }

    public static void showMainItems()
    {
        if(navigation.getVisibility() == View.INVISIBLE) {
            navigation.setVisibility(View.VISIBLE);
        }
        if(cancel_bt.getVisibility() == View.VISIBLE)
        {
            cancel_bt.setVisibility(View.INVISIBLE);
        }

    }

    //Controls the actions of the buttons of search and filter that are present in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        navigation.setVisibility(View.INVISIBLE);
        cancel_bt.setVisibility(View.VISIBLE);
        backstackSafeCheck();
        hideFilterCard(getBaseContext());
        Class fragmentClass = null;
        Fragment fragment = null;
        if (item.getItemId() == R.id.search_frag_bt) {
            backstack.remove(RideSearchFrag.class);
            backstack.add(RideSearchFrag.class);
            fragmentClass = RideSearchFrag.class;
        } else if (item.getItemId() == R.id.filter_frag_bt){

            backstack.remove(RideFilterFrag.class);
            backstack.add(RideFilterFrag.class);
            fragmentClass = RideFilterFrag.class;
        }
        else if(item.getItemId() == R.id.new_ride_bt) {
            backstack.remove(TabbedRideOfferFrag.class);
            backstack.add(TabbedRideOfferFrag.class);
            fragmentClass = TabbedRideOfferFrag.class;
            setupUI(getWindow().getDecorView().getRootView(), this);
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
                    .setPositiveButton(getString(R.string.act_main_oknogplay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //
                        }
                    }).show();
        }
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

    public static void setupUI(View view, Activity act) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(act);
                    act.getWindow().getDecorView().clearFocus();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, act);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
