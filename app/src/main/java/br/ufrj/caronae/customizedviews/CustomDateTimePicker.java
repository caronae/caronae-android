package br.ufrj.caronae.customizedviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.R;
import br.ufrj.caronae.Util;
import br.ufrj.caronae.frags.RideOfferFrag;
import br.ufrj.caronae.frags.RideSearchFrag;

public class CustomDateTimePicker extends Dialog implements View.OnClickListener {

    private TextView title_tv, positive_bt, negative_bt;
    private NumberPicker dayTime_np, hour_np, minute_np;
    private String title, time, type;
    private String[] hours, minutes, dateTime;
    private Calendar calendar;
    private Date today;
    private SimpleDateFormat dateFormat, dateUS, dateBR;
    private Fragment fragment;

    public CustomDateTimePicker(Activity activity, String title, String time, Fragment fragment, String type) {
        super(activity);
        this.title = title;
        this.time = time;
        this.fragment = fragment;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_datetimepicker);
        title_tv = (TextView) findViewById(R.id.title);
        setTitleText(title);
        calendar = Calendar.getInstance();
        today = calendar.getTime();
        dateFormat = new SimpleDateFormat("yyyy/MMM/dd", Locale.getDefault());
        dateUS = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateBR = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        hours = new String[24];
        if(type.equals("Search")) {
            String [] m = {"00", "30","00", "30","00", "30","00", "30","00", "30"};
            minutes = m;
        }
        else
        {
            minutes = new String[60];
        }
        dateTime = new String[720];
        for(int i = 0; i < 60; i++)
        {
            if(i < 10) {
                hours[i] = "0"+i;
                if(!type.equals("Search")) {
                    minutes[i] = "0" + i;
                }
            }else
            {
                if(i < 24) {
                    hours[i] = Integer.toString(i);
                }
                if(!type.equals("Search")) {
                    minutes[i] = Integer.toString(i);
                }
            }
        }
        positive_bt = (TextView) findViewById(R.id.positive_bt);
        negative_bt = (TextView) findViewById(R.id.negative_bt);
        dayTime_np = (NumberPicker) findViewById(R.id.day_time);
        hour_np = (NumberPicker) findViewById(R.id.hour);
        minute_np = (NumberPicker) findViewById(R.id.minutes);
        dayTime_np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hour_np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minute_np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        positive_bt.setOnClickListener(this);
        negative_bt.setOnClickListener(this);
        setNumberPicker(time);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.positive_bt:
                int d, h, m;
                d = dayTime_np.getValue();
                h = hour_np.getValue();
                m = minute_np.getValue();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, d - 360);
                Date thatDate = calendar.getTime();
                String date_txt = dateBR.format(thatDate);
                String result = date_txt + " " + hours[h] + ":" + minutes[m];
                if(type.equals("Offer"))
                {
                    RideOfferFrag frag = (RideOfferFrag)fragment;
                    frag.time = result;
                    frag.time_et.setText(result);
                }
                else
                {
                    RideSearchFrag frag = (RideSearchFrag) fragment;
                    frag.time = result;
                    result = Util.getWeekDayFromBRDate(date_txt) + ", " + result;
                    frag.time_et.setText(result);
                }
                dismiss();
                break;

            case R.id.negative_bt:
                dismiss();
                break;

            default:

                break;
        }
    }

    private void setTitleText(String titleText)
    {
        this.title_tv.setText(titleText);
    }

    private void setNumberPicker(String time)
    {
        // dd/MM/yyyy_hh:mm
        // 0123456789012345
        configNP();
        String day = time.substring(0,10);
        day = stringToDate(day);
        minute_np.setValue(Integer.parseInt(time.substring(14)));
        hour_np.setValue(Integer.parseInt(time.substring(11,13)));
        int index = 360;
        for(int i = 0; i < dateTime.length; i++)
        {
            if(dateTime[i].equals(day))
            {
                index = i;
            }
        }
        dayTime_np.setValue(index);

        dayTime_np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int[] values = setMinValues();
                if(numberPicker.getValue() < values[0] || numberPicker.getValue() <= values[0] && hour_np.getValue() < values[1] || numberPicker.getValue() <= values[0] && hour_np.getValue() <= values[1] && minute_np.getValue() < values[2])
                {
                    dayTime_np.setValue(values[0]);
                    hour_np.setValue(values[1]);
                    if(!type.equals("Search")) {
                        minute_np.setValue(values[2]);
                    }
                }
            }
        });

        hour_np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int[] values = setMinValues();
                if(dayTime_np.getValue() < values[0] || dayTime_np.getValue() <= values[0] && numberPicker.getValue() < values[1] || dayTime_np.getValue() <= values[0] && numberPicker.getValue() <= values[1] && minute_np.getValue() < values[2])
                {
                    dayTime_np.setValue(values[0]);
                    hour_np.setValue(values[1]);
                    if(!type.equals("Search")) {
                        minute_np.setValue(values[2]);
                    }
                }
            }
        });

        minute_np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int[] values = setMinValues();
                if(dayTime_np.getValue() < values[0] || dayTime_np.getValue() <= values[0] && hour_np.getValue() < values[1] || dayTime_np.getValue() <= values[0] && hour_np.getValue() <= values[1] && numberPicker.getValue() < values[2])
                {
                    dayTime_np.setValue(values[0]);
                    hour_np.setValue(values[1]);
                    if(!type.equals("Search")) {
                        minute_np.setValue(values[2]);
                    }
                }
            }
        });
    }

    private void configNP()
    {
        for(int i = 0; i < 720; i++)
        {
            dateTime[i] = getDate(i - 360);
        }
        hour_np.setMinValue(0);
        minute_np.setMinValue(0);
        dayTime_np.setMinValue(0);
        hour_np.setMaxValue(hours.length - 1);
        minute_np.setMaxValue(minutes.length - 1);
        dayTime_np.setMaxValue(dateTime.length - 1);
        hour_np.setWrapSelectorWheel(true);
        minute_np.setWrapSelectorWheel(true);
        dayTime_np.setWrapSelectorWheel(false);
        hour_np.setDisplayedValues(hours);
        minute_np.setDisplayedValues(minutes);
        dayTime_np.setDisplayedValues(dateTime);
    }

    private String getDate(int id)
    {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, id);
        Date nextDate = calendar.getTime();
        String day = dateBR.format(nextDate);
        if(dateBR.format(today).equals(day)){
            return "Hoje";
        }
        else {
            String formatedDate = dateFormat.format(nextDate);
            String dayOfMonth = formatedDate.substring(9) + " de " + formatedDate.substring(5, 8);
            String weekDay = Util.getWeekDayFromDateWithoutTodayString(dateUS.format(nextDate));
            return weekDay.toLowerCase() + " " + dayOfMonth;
        }
    }

    private String stringToDate(String aDate) {
        ParsePosition pos = new ParsePosition(0);
        Date stringDate = dateBR.parse(aDate, pos);
        String formatedDate = dateFormat.format(stringDate);
        return Util.getWeekDayFromDateWithoutTodayString(dateUS.format(stringDate)).toLowerCase() + " " + formatedDate.substring(9) + " de " + formatedDate.substring(5, 8);
    }

    private int[] setMinValues()
    {
        calendar = Calendar.getInstance();
        int minMinuteInt = calendar.get(Calendar.MINUTE) + 5;
        int minHourInt = calendar.get(Calendar.HOUR_OF_DAY);
        int values[] = new int[]{360, -1, -1};
        boolean nextDay = false;
        if(minMinuteInt >= 60)
        {
            minMinuteInt -= 60;
            minHourInt += 1;
            if(minHourInt >= 24) {
                minHourInt -= 24;
                nextDay = true;
            }
        }
        if(nextDay)
        {
            values[0] = 361;
        }
        values[1] = minHourInt;
        values[2] = minMinuteInt;
        return values;
    }
}