package br.ufrj.caronae.comparators;

import br.ufrj.caronae.models.RideOffer;

public class RideOfferComparatorByTime implements java.util.Comparator<RideOffer> {

    @Override
    public int compare(RideOffer r1, RideOffer r2) {
        return r1.getTime().compareTo(r2.getTime());
    }
}
