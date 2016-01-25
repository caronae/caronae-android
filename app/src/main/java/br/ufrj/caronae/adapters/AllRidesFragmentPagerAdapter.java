package br.ufrj.caronae.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.ufrj.caronae.frags.AllRidesListFrag;

public class AllRidesFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Ida", "Volta"};
    private Fragment frags[];

    public AllRidesFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        frags = new Fragment[]{new AllRidesListFrag(), new AllRidesListFrag()};
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