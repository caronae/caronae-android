package br.ufrj.caronae.acts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                InputMethodManager inputMethodManager = (InputMethodManager)  MainAct.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                try {
                    //noinspection ConstantConditions
                    inputMethodManager.hideSoftInputFromWindow(MainAct.this.getCurrentFocus().getWindowToken(), 0);
                } catch (NullPointerException e) {
                    Log.e("onDrawerSlide", e.getMessage());
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        nvDrawer.addHeaderView(getHeaderView());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new RideSearchFrag()).commit();
    }

    private View getHeaderView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View nvHeader = inflater.inflate(R.layout.nav_header, null, false);

        TextView headerText = (TextView) nvHeader.findViewById(R.id.headerText);
        headerText.setText(App.getUser().getName());

        return nvHeader;
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
                fragmentClass = RideOfferFrag.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = RideSearchFrag.class;
                break;
            case R.id.nav_fourth_fragment:
                fragmentClass = MyRidesFrag.class;
                break;
            case R.id.nav_fifth_fragment:
                fragmentClass = MyActiveRidesFrag.class;
                break;
            case R.id.nav_sixth_fragment:
                App.logOut();
                startActivity(new Intent(this, LoginAct.class));
                finish();
                return;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
}
