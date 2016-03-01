package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.ufrj.caronae.R;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.MyRidesFragmentPagerAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyRidesFrag extends Fragment {
    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    public MyRidesFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);
        ButterKnife.bind(this, view);

        viewPager.setAdapter(new MyRidesFragmentPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @OnClick(R.id.fab)
    public void fab() {
        ((MainAct) getActivity()).showRideOfferFrag();
    }
}
