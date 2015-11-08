package br.ufrj.caronae.comparators;

import br.ufrj.caronae.models.modelsforjson.RideOfferForJson;

public class RideOfferComparatorByTime implements java.util.Comparator<RideOfferForJson> {

    @Override
    public int compare(RideOfferForJson r1, RideOfferForJson r2) {
        return r1.getTime().compareTo(r2.getTime());
    }
}
