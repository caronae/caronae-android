package br.ufrj.caronae.comparators;

import br.ufrj.caronae.models.Ride;

public class RideComparatorByDateAndTime implements java.util.Comparator<Ride> {

    @Override
    public int compare(Ride r1, Ride r2) {
        int c = r1.getDate().compareTo(r2.getDate());
        if (c == 0) {
            return r1.getTime().compareTo(r2.getTime());
        }
        return c;
    }
}
