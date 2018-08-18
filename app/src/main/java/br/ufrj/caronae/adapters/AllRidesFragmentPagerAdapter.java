package br.ufrj.caronae.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import br.ufrj.caronae.frags.AllRidesListFrag;
import br.ufrj.caronae.interfaces.Updatable;
import br.ufrj.caronae.models.modelsforjson.RideForJson;

public class AllRidesFragmentPagerAdapter extends FragmentPagerAdapter implements Updatable {
    final public static int PAGE_GOING = 0;
    final private static int PAGE_NOT_GOING = 1;
    private String tabTitles[];
    private Fragment frags[];
    private final AllRidesListFrag notGoingFrag;
    private final AllRidesListFrag goingFrag;

    public AllRidesFragmentPagerAdapter(FragmentManager fm, ArrayList<RideForJson> goingRides, ArrayList<RideForJson> notGoingRides, String[] tabTitles) {
        super(fm);

        this.tabTitles = tabTitles;

        Bundle goingBundle = new Bundle();
        goingBundle.putParcelableArrayList("rides", goingRides);
        goingBundle.putInt("ID", PAGE_GOING);
        goingFrag = new AllRidesListFrag();
        goingFrag.setArguments(goingBundle);

        Bundle notGoingBundle = new Bundle();
        notGoingBundle.putParcelableArrayList("rides", notGoingRides);
        notGoingBundle.putInt("ID", PAGE_NOT_GOING);
        notGoingFrag = new AllRidesListFrag();
        notGoingFrag.setArguments(notGoingBundle);

        frags = new Fragment[]{goingFrag, notGoingFrag};
    }

    @Override
    public int getCount() {
        return 2;
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

    @Override
    public void needsUpdating() {
        goingFrag.needsUpdating();
        notGoingFrag.needsUpdating();
    }
}