package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import br.ufrj.caronae.R;
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
    @BindView(R.id.fab_add_ride)
    FloatingActionButton fab_add_ride;

    static ProgressBar progressBar;

    public MyRidesFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_rides, container, false);
        ButterKnife.bind(this, view);

        viewPager.setAdapter(new RideDirectionFragmentPagerAdapter(getChildFragmentManager(), MyRidesListFrag.class, getResources().getStringArray(R.array.tab_tags)));
        tabLayout.setupWithViewPager(viewPager);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        configureTabIndicators();

        return view;
    }

    @OnClick(R.id.fab_add_ride)
    public void fab_add_ride() {
        ((MainAct) getActivity()).showRideOfferFrag();
    }

    public static void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
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
}
