package br.ufrj.caronae.comparators;

import br.ufrj.caronae.models.modelsforjson.RideForJson;

public class RideOfferComparatorByTime implements java.util.Comparator<RideForJson> {

    @Override
    public int compare(RideForJson r1, RideForJson r2) {
        return r1.getTime().compareTo(r2.getTime());
    }
}
