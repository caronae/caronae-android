package br.ufrj.caronae.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.ufrj.caronae.frags.MyRidesListFrag;

public class RideDirectionFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Ida", "Volta"};
    private Fragment frags[];

    public RideDirectionFragmentPagerAdapter(FragmentManager fm, Class framentClass) {
        super(fm);

        try {
            Bundle bundle1 = new Bundle(), bundle2 = new Bundle();
            bundle1.putBoolean("going", true);
            bundle2.putBoolean("going", false);
            Fragment f1 = (Fragment) framentClass.newInstance(), f2 = (Fragment) framentClass.newInstance();
            f1.setArguments(bundle1);
            f2.setArguments(bundle2);
            frags = new Fragment[]{f1, f2};
        } catch (Exception e) {
            Log.e("RideDirectPagerAdapter", e.getMessage());
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return frags[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
