package br.ufrj.caronae.frags;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.customizedviews.CustomDateTimePicker;
import br.ufrj.caronae.R;
import br.ufrj.caronae.data.SharedPref;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.acts.PlaceAct;
import br.ufrj.caronae.acts.RideSearchAct;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RideSearchFrag extends Fragment {

    @BindView(R.id.tab1)
    RelativeLayout isGoing_bt;
    @BindView(R.id.tab2)
    RelativeLayout isLeaving_bt;
    @BindView(R.id.tab1_tv)
    TextView isGoing_tv;
    @BindView(R.id.tab2_tv)
    TextView isLeaving_tv;

    @BindView(R.id.center_et)
    TextView center_et;
    @BindView(R.id.location_et)
    TextView location_et;
    @BindView(R.id.time_et)
    public TextView time_et;

    private boolean going, fromSearch;
    public String time;

    public RideSearchFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ride_search, container, false);
        ButterKnife.bind(this, view);
        try {
            fromSearch = getArguments().getBoolean("fromSearch", false);
        }catch (Exception e){}
        going = true;
        setButton(isLeaving_bt,isGoing_bt, isLeaving_tv, isGoing_tv);
        if(SharedPref.getGoingLabel() != null)
        {
            isGoing_tv.setText(SharedPref.getGoingLabel());
        }
        if(SharedPref.getLeavingLabel() != null)
        {
            isLeaving_tv.setText(SharedPref.getLeavingLabel());
        }
        loadLastFilters();
        return view;
    }

    @Override
    public void onStart()
    {
        Util.debug(SharedPref.LOCATION_INFO);
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        if(!SharedPref.CAMPI_INFO.isEmpty() && !SharedPref.CAMPI_INFO.equals(""))
        {
            center_et.setText(SharedPref.CAMPI_INFO);
            SharedPref.CAMPI_INFO = "";
        }
        super.onStart();
    }

    @Override
    public void onResume()
    {
        if(!SharedPref.LOCATION_INFO.isEmpty() && !SharedPref.LOCATION_INFO.equals(""))
        {
            location_et.setText(SharedPref.LOCATION_INFO);
            SharedPref.LOCATION_INFO = "";
        }
        if(!SharedPref.CAMPI_INFO.isEmpty() && !SharedPref.CAMPI_INFO.equals(""))
        {
            center_et.setText(SharedPref.CAMPI_INFO);
            SharedPref.CAMPI_INFO = "";
        }
        super.onResume();
    }

    private void loadLastFilters() {
        String l, c, d, tm;
        l = !SharedPref.getLocationSearch().equals(SharedPref.MISSING_PREF) ? SharedPref.getLocationSearch() : "Todos os Bairros";
        c = !SharedPref.getCenterSearch().equals(SharedPref.MISSING_PREF) ? SharedPref.getCenterSearch() : "Todos os Campi";
        d = !SharedPref.getDateSearch().equals(SharedPref.MISSING_PREF) ? SharedPref.getDateSearch() : "";
        tm = !SharedPref.getTimeSearch().equals(SharedPref.MISSING_PREF) ? SharedPref.getTimeSearch() : "";
        if(d.isEmpty() || tm.isEmpty())
        {
            setInitialDate();
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String getCurrentDateTime = sdf.format(calendar.getTime());
            time = d+tm;

            if (getCurrentDateTime.compareTo(time) < 0)
            {
                //Future
                String result = Util.getWeekDayFromBRDate(d) + ", " + d + tm;
                time_et.setText(result);
            }
            else
            {
                //Past
                setInitialDate();
            }
        }
        location_et.setText(l);
        center_et.setText(c);
        if(going)
        {
            setButton(isLeaving_bt, isGoing_bt,isLeaving_tv, isGoing_tv);
        }
        else
        {
            setButton(isGoing_bt, isLeaving_bt, isGoing_tv, isLeaving_tv);
        }
    }

    @OnClick(R.id.location_et)
    public void locationEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Buscar");
        intent.putExtra("selection", "neigh");
        intent.putExtra("allP", true);
        intent.putExtra("otherP", true);
        intent.putExtra("getBack", true);
        intent.putExtra("selectable", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.time_et)
    public void timeEt() {
        Activity activity = getActivity();
        CustomDateTimePicker cdtp;
        if(going) {
            if(SharedPref.getGoingLabel() == null)
                cdtp = new CustomDateTimePicker(activity, getResources().getString(R.string.arriving_ufrj), time, this, "Search");
            else
                cdtp = new CustomDateTimePicker(activity, SharedPref.getGoingLabel(), time, this, "Search");
        }else
        {
            if(SharedPref.getLeavingLabel() == null)
                cdtp = new CustomDateTimePicker(activity, getResources().getString(R.string.leaving_ufrj), time, this, "Search");
            else
                cdtp = new CustomDateTimePicker(activity, SharedPref.getLeavingLabel(), time, this, "Search");
        }
        Window window = cdtp.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cdtp.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, getResources().getDisplayMetrics());
        cdtp.show();
        cdtp.getWindow().setAttributes(lp);
    }

    @OnClick(R.id.center_et)
    public void centerEt() {
        Intent intent = new Intent(getActivity(), PlaceAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("backText", "Buscar");
        intent.putExtra("selection", "center");
        intent.putExtra("allP", true);
        intent.putExtra("getBack", false);
        intent.putExtra("selectable", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_slide_in, R.anim.anim_left_slide_out);
    }

    @OnClick(R.id.search_bt)
    public void searchBt()
    {
        String location = location_et.getText().toString();
        String center = center_et.getText().toString();
        //sexta-feira, 11/05/2018 07:00
        //01234567890123456789012345678
        String date = time_et.getText().toString().substring(time_et.getText().toString().length()-16, time_et.getText().toString().length()-6);
        String time = time_et.getText().toString().substring(time_et.getText().toString().length()-6);

        SharedPref.setLocationSearch(location);
        SharedPref.setCenterSearch(center);
        SharedPref.setDateSearch(date);
        SharedPref.setTimeSearch(time);
        String isGoing = going ? "1" : "0";
        if(!fromSearch) {
            Intent rideSearchAct = new Intent(getActivity(), RideSearchAct.class);
            rideSearchAct.putExtra("isGoing", isGoing);
            startActivity(rideSearchAct);
            getActivity().overridePendingTransition(R.anim.anim_up_slide_in, R.anim.anim_down_slide_out);
        }
        else
        {
            ((RideSearchAct)getActivity()).isGoing = isGoing;
            ((RideSearchAct)getActivity()).changeToList(true);
        }
    }

    @OnClick(R.id.tab1)
    public void goingTabSelected()
    {
        if(!going)
        {
            going = true;
            setButton(isLeaving_bt, isGoing_bt,isLeaving_tv, isGoing_tv);
        }
    }

    @OnClick(R.id.tab2)
    public void leavingTabSelected()
    {
        if(going)
        {
            going = false;
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

    private void setInitialDate()
    {
        Calendar rightNow = Calendar.getInstance();
        Date date = rightNow.getTime();
        SimpleDateFormat dateWithYear = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String ddmmyyyy = dateWithYear.format(date);
        String weekday = Util.getWeekDayFromBRDate(ddmmyyyy);
        int hourInt = rightNow.get(Calendar.HOUR_OF_DAY);
        int minuteInt = rightNow.get(Calendar.MINUTE) + 5;
        if(minuteInt >= 60)
        {
            hourInt += 2;
        }
        else{
            hourInt += 1;
        }

        if(hourInt >= 24)
        {
            hourInt -= 24;
            rightNow.add(Calendar.DAY_OF_YEAR, 1);
            date = rightNow.getTime();
            ddmmyyyy = dateWithYear.format(date);
        }

        if(hourInt < 10)
        {
            time = ddmmyyyy + " 0" + hourInt + ":00";
        }
        else
        {
            time = ddmmyyyy + " " + hourInt + ":00";
        }
        String result = weekday + ", " + time;
        time_et.setText(result);
    }
}