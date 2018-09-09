package br.ufrj.caronae.frags;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.MainAct;
import br.ufrj.caronae.adapters.AllRidesFragmentPagerAdapter;
import br.ufrj.caronae.interfaces.Updatable;
import br.ufrj.caronae.models.modelsforjson.RideForJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllRidesFrag extends Fragment implements Updatable {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab1)
    RelativeLayout isGoing_bt;
    @BindView(R.id.tab2)
    RelativeLayout isLeaving_bt;
    @BindView(R.id.tab1_tv)
    TextView isGoing_tv;
    @BindView(R.id.tab2_tv)
    TextView isLeaving_tv;

    ArrayList<RideForJson> goingRides = new ArrayList<>();
    ArrayList<RideForJson> notGoingRides = new ArrayList<>();

    String isGoing;
    private AllRidesFragmentPagerAdapter pagerAdapter;

    public AllRidesFrag() {
        // Required empty public constructor
        Log.d("allRides", "Creating new AllRidesFrag");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("allRides", "onCreate AllRidesFrag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("allRides", "onCreateView AllRidesFrag");

        View view = inflater.inflate(R.layout.fragment_all_rides, container, false);
        ButterKnife.bind(this, view);

        Util.setColors();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                {
                    isGoing = "1";
                    SharedPref.setIsGoingPref(isGoing);
                    setButton(isLeaving_bt, isGoing_bt,isLeaving_tv, isGoing_tv);
                }
                else
                {
                    isGoing = "0";
                    SharedPref.setIsGoingPref(isGoing);
                    setButton(isGoing_bt, isLeaving_bt, isGoing_tv, isLeaving_tv);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(SharedPref.getGoingLabel() != null)
        {
            isGoing_tv.setText(SharedPref.getGoingLabel());
        }
        if(SharedPref.getLeavingLabel() != null)
        {
            isLeaving_tv.setText(SharedPref.getLeavingLabel());
        }

        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        setHasOptionsMenu(true);
        ((MainAct)getActivity()).showMainItems();

        pagerAdapter = new AllRidesFragmentPagerAdapter(getChildFragmentManager(), getContext(), goingRides, notGoingRides);
        viewPager.setAdapter(pagerAdapter);

        isGoing = SharedPref.isGoing;

        if(isGoing.equals("1"))
        {
            setButton(isLeaving_bt, isGoing_bt, isLeaving_tv, isGoing_tv);
            viewPager.setCurrentItem(0);
        }
        else
        {
            setButton(isGoing_bt, isLeaving_bt, isGoing_tv, isLeaving_tv);
            viewPager.setCurrentItem(1);
        }

        if(((MainAct)getActivity()).filterText.getText().equals(""))
        {
            ((MainAct)getActivity()).hideFilterCard(getContext());
        }
        return view;
    }

    //Creates the toolbar menu with options to access the filter/search fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @OnClick(R.id.tab1)
    public void goingTabSelected()
    {
        if(isGoing.equals("0"))
        {
            isGoing = "1";
            SharedPref.isGoing = "1";
            SharedPref.setIsGoingPref(isGoing);
            viewPager.setCurrentItem(0);
            setButton(isLeaving_bt, isGoing_bt,isLeaving_tv, isGoing_tv);
        }
    }

    @OnClick(R.id.tab2)
    public void leavingTabSelected()
    {
        if(isGoing.equals("1"))
        {
            isGoing = "0";
            SharedPref.isGoing = "0";
            SharedPref.setIsGoingPref(isGoing);
            viewPager.setCurrentItem(1);
            setButton(isGoing_bt, isLeaving_bt, isGoing_tv, isLeaving_tv);
        }
    }

    private void setButton(RelativeLayout button1, RelativeLayout button2, TextView bt1_tv, TextView bt2_tv)
    {
        button1.setFocusable(true);
        button1.setClickable(true);
        button2.setFocusable(false);
        button2.setClickable(false);
        GradientDrawable bt1Shape = (GradientDrawable)button1.getBackground();
        GradientDrawable bt2Shape = (GradientDrawable)button2.getBackground();
        bt1Shape.setColor(getResources().getColor(R.color.white));
        bt2Shape.setColor(getResources().getColor(R.color.dark_gray));
        bt1_tv.setTextColor(getResources().getColor(R.color.dark_gray));
        bt2_tv.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void needsUpdating() {
        pagerAdapter.needsUpdating();
    }
}

