package br.ufrj.caronae.acts;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import br.ufrj.caronae.frags.MyActiveRidesFrag;
import br.ufrj.caronae.frags.MyRidesFrag;
import br.ufrj.caronae.frags.ProfileFrag;
import br.ufrj.caronae.frags.RequestersListFrag;
import br.ufrj.caronae.frags.RideOfferFrag;
import br.ufrj.caronae.frags.RideSearchFrag;
import br.ufrj.caronae.models.User;

public class MainAct extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
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
        if (user.getEmail().isEmpty() ||  user.getPhoneNumber().isEmpty()) {
            fragment = new ProfileFrag();
        } else {
            fragment = new RideSearchFrag();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    private void getHeaderView(NavigationView nvDrawer) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View nvHeader = inflater.inflate(R.layout.nav_header, null, false);

        TextView name_tv = (TextView) nvHeader.findViewById(R.id.name_tv);
        name_tv.setText(App.getUser().getName());
        TextView course_tv = (TextView) nvHeader.findViewById(R.id.course_tv);
        course_tv.setText(App.getUser().getCourse());

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

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = ProfileFrag.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = MyRidesFrag.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = MyActiveRidesFrag.class;
                break;
            default:
                fragmentClass = MyRidesFrag.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new RideSearchFrag()).commit();
            setTitle(item.getTitle());
        }

        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void showRequestersListFrag(List<User> users, int rideId) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("users", (ArrayList<User>) users);
        bundle.putInt("rideId", rideId);

        RequestersListFrag fragment = new RequestersListFrag();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
    }

    public void showRideOfferFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new RideOfferFrag()).commit();
        setTitle("Oferecer carona");
    }
}
