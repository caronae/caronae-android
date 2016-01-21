package br.ufrj.caronae.comparators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.models.modelsforjson.RideForJson;

public class RideOfferComparatorByDateAndTime implements java.util.Comparator<RideForJson> {

    @Override
    public int compare(RideForJson r1, RideForJson r2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        int c = 0;
        try {
            Date etDate = simpleDateFormat.parse(r1.getDate() + " " + r1.getTime());
            Date etDate2 = simpleDateFormat.parse(r2.getDate() + " " + r2.getTime());
            c = etDate.compareTo(etDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return c;
    }
}
