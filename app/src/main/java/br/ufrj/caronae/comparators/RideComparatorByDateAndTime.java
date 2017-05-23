package br.ufrj.caronae.comparators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.ufrj.caronae.models.Ride;

public class RideComparatorByDateAndTime implements java.util.Comparator<Ride> {

    @Override
    public int compare(Ride r1, Ride r2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

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
