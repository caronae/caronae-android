package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.ufrj.caronae.R;
import br.ufrj.caronae.adapters.RideDirectionFragmentPagerAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TabbedRideOfferFrag extends Fragment {
    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    public TabbedRideOfferFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabbed_ride_offer, container, false);
        ButterKnife.bind(this, view);

        viewPager.setAdapter(new RideDirectionFragmentPagerAdapter(getChildFragmentManager(), RideOfferFrag.class,  getResources().getStringArray(R.array.tab_tags)));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
