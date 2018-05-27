package br.ufrj.caronae.comparators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.Util;
import br.ufrj.caronae.models.modelsforjson.RideForJson;

public class SortRides implements java.util.Comparator<RideForJson>  {
    @Override
    public int compare(RideForJson ride1, RideForJson ride2) {
        Date time1 = new Date(),time2 = new Date();
        try{
            if(ride1.getDate().equals(ride2.getDate()))
            {
                time1 = toTime(ride1.getTime());
                time2 = toTime(ride2.getTime());
            }
            else
            {
                time1 = toDate(ride1.getDate());
                time2 = toDate(ride2.getDate());
            }
        }catch (Exception e){
            Util.debug("Error while sorting rides.");
        }
        return time1.compareTo(time2);
    }
    private Date toDate(String value) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.parse(value);
    }
    private Date toTime(String value) throws ParseException {
        DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return format.parse(value);
    }
}
