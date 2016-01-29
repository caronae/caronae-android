package br.ufrj.caronae.acts;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.RoundedTransformation;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.frags.AllRidesFrag;
import br.ufrj.caronae.frags.FalaeFrag;
import br.ufrj.caronae.frags.MyActiveRidesFrag;
import br.ufrj.caronae.frags.MyProfileFrag;
import br.ufrj.caronae.frags.MyRidesFrag;
import br.ufrj.caronae.frags.RequestersListFrag;
import br.ufrj.caronae.frags.RideOfferFrag;
import br.ufrj.caronae.frags.RideSearchFrag;
import br.ufrj.caronae.frags.RidesHistoryFrag;
import br.ufrj.caronae.gcm.RegistrationIntentService;
import br.ufrj.caronae.models.User;

public class MainAct extends AppCompatActivity {

    private static final int GPLAY_UNAVAILABLE = 123;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private CallbackManager callbackManager;

    private ArrayList<Class> backstack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        getFbCallbackManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
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

                    if (!profilePicUrl.isEmpty()) {
                        Picasso.with(MainAct.this).load(profilePicUrl)
                                .placeholder(R.drawable.user_pic)
                                .error(R.drawable.user_pic)
                                .transform(new RoundedTransformation(0))
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

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        getHeaderView(nvDrawer);

        User user = App.getUser();
        Fragment fragment;
        if (user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty() ||
                user.getLocation() == null || user.getLocation().isEmpty()) {
            fragment = new MyProfileFrag();
            Util.toast(getString(R.string.act_main_profileIncomplete));
        } else {
            fragment = new AllRidesFrag();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        backstack = new ArrayList<>();
        backstack.add(fragment.getClass());

        checkGPlay();
    }

    private void checkGPlay() {
        int resultGplay = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (resultGplay != ConnectionResult.SUCCESS) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, resultGplay, MainAct.GPLAY_UNAVAILABLE);
            if (dialog != null) {
                dialog.show();
            } else {
                Util.toast(R.string.gplay_unavailable);
            }
        } else {
            if (SharedPref.getUserGcmToken().equals(SharedPref.MISSING_PREF)) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
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
                    .transform(new RoundedTransformation(0))
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
            case R.id.nav_first_fragment:
                fragmentClass = MyProfileFrag.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = AllRidesFrag.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = MyRidesFrag.class;
                break;
            case R.id.nav_fourth_fragment:
                fragmentClass = MyActiveRidesFrag.class;
                break;
            case R.id.nav_fifth_fragment:
                fragmentClass = RidesHistoryFrag.class;
                break;
            case R.id.nav_sixth_fragment:
                fragmentClass = FalaeFrag.class;
                break;
            default:
                fragmentClass = MyProfileFrag.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        backstackSafeCheck();
        backstack.remove(fragmentClass);
        backstack.add(fragmentClass);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
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
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            setTitle(retrieveTitle(fragmentClass.toString()));
            mDrawer.closeDrawers();
        }
    }

    private void backstackSafeCheck() {//better safe than sorry
        if (backstack == null)
            backstack = new ArrayList<>();
    }

    private int retrieveTitle(String fragmentClass) {
        if (fragmentClass.equals(MyProfileFrag.class.toString()))
            return R.string.frag_profile_title;
        if (fragmentClass.equals(AllRidesFrag.class.toString()))
            return R.string.frag_allrides_title;
        if (fragmentClass.equals(MyRidesFrag.class.toString()))
            return R.string.frag_myrides_title;
        if (fragmentClass.equals(MyActiveRidesFrag.class.toString()))
            return R.string.frag_myactiverides_title;
        if (fragmentClass.equals(RidesHistoryFrag.class.toString()))
            return R.string.frag_history_title;
        if (fragmentClass.equals(FalaeFrag.class.toString()))
            return R.string.frag_falae_title;
        if (fragmentClass.equals(RideOfferFrag.class.toString()))
            return R.string.act_main_setRideOfferFragTitle;
        if (fragmentClass.equals(RideSearchFrag.class.toString()))
            return R.string.frag_searchride_title;
        return R.string.app_name;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_frag_bt) {
            backstackSafeCheck();
            backstack.remove(RideSearchFrag.class);
            backstack.add(RideSearchFrag.class);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new RideSearchFrag()).commit();
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

    public void showRequestersListFrag(List<User> users, int rideId, int color) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("users", (ArrayList<User>) users);
        bundle.putInt("rideId", rideId);
        bundle.putInt("color", color);

        backstackSafeCheck();
        backstack.remove(RequestersListFrag.class);
        backstack.add(RequestersListFrag.class);
        RequestersListFrag fragment = new RequestersListFrag();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    public void showRideOfferFrag() {
        backstackSafeCheck();
        backstack.remove(RideOfferFrag.class);
        backstack.add(RideOfferFrag.class);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new RideOfferFrag()).commit();
        setTitle(getString(R.string.act_main_setRideOfferFragTitle));
    }
}
