package br.ufrj.caronae.frags;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import br.ufrj.caronae.R;
import br.ufrj.caronae.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.RideDirectionFragmentPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyRidesFrag extends Fragment {
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    public MyRidesFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);
        ButterKnife.bind(this, view);

        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        setHasOptionsMenu(true);

        MainAct.showMainItems();

        viewPager.setAdapter(new RideDirectionFragmentPagerAdapter(getChildFragmentManager(), MyRidesListFrag.class, getResources().getStringArray(R.array.tab_tags)));
        tabLayout.setupWithViewPager(viewPager);

        configureTabIndicators();

        return view;
    }

    private void configureTabIndicators() {
        View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
        p.setMargins(25, 0, 25, 0);
        tab.requestLayout();

        tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
        p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
        p.setMargins(25, 0, 25, 0);
        tab.requestLayout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_my_rides, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
}
