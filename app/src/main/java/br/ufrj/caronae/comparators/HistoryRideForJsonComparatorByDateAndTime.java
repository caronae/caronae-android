package br.ufrj.caronae.comparators;

import br.ufrj.caronae.models.modelsforjson.HistoryRideForJson;

public class HistoryRideForJsonComparatorByDateAndTime implements java.util.Comparator<HistoryRideForJson> {
    @Override
    public int compare(HistoryRideForJson r1, HistoryRideForJson r2) {
        return new RideComparatorByDateAndTime().compare(r1.getRide(), r2.getRide());
    }
}
