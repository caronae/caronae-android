package br.ufrj.caronae.models.modelsforjson;

import android.util.Log;

import java.util.List;

public class RideForJsonDeserializer {

    private List<RideForJson> data;
    private int current_page;
    private int last_page;

    public List<RideForJson> getRides() {
        return data;
    }

    public boolean hasRides() {
        return !getRides().isEmpty();
    }

    public boolean hasMorePages() {
        Log.d("allRides", "hasMorePages - current: " + current_page + ", last: " + last_page);
        return current_page < last_page;
    }

    public int getNextPage() {
        return current_page + 1;
    }
}
