package br.ufrj.caronae.models;

public class RideSearchFiltersForJson {
    private String zone;
    private String neighborhood;
    private String date;
    private boolean go;

    public RideSearchFiltersForJson(String zone, String neighborhood, String date, boolean go) {
        this.zone = zone;
        this.neighborhood = neighborhood;
        this.date = date;
        this.go = go;
    }

    public String getZone() {
        return zone;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getDate() {
        return date;
    }

    public boolean isGo() {
        return go;
    }
}
